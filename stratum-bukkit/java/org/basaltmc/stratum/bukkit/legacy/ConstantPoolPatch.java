package org.basaltmc.stratum.bukkit.legacy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** Edits a class file's constant pool in place, so renamed API needs no change to the code or its stack maps. */
final class ConstantPoolPatch {
    private static final int kUtf8 = 1;
    private static final int kClass = 7;
    private static final int kFieldref = 9;
    private static final int kMethodref = 10;
    private static final int kInterfaceMethodref = 11;
    private static final int kNameAndType = 12;
    private static final int kInvokeDynamic = 18;
    private static final int kMethodHandle = 15;
    private static final String kLegacyMysqlDriver = "com.mysql.jdbc.Driver";
    private static final String kModernMysqlDriver = "com.mysql.cj.jdbc.Driver";
    private static final int kInstanceOf = 0xC1;
    private static final String kEnumType = "Ljava/lang/Enum;";
    private static final String kLegacyEnumMap = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumMap";
    private static final String kLegacyEnumSet = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumSet";
    private static final String kNames = "org/basaltmc/stratum/bukkit/legacy/LegacyNames";

    private static final String kLegacyPrefix = "org/basaltmc/stratum/bukkit/legacy/Legacy";
    private static final java.util.Set<String> kEnumSafe =
        java.util.Set.of("name", "ordinal", "toString", "equals", "hashCode", "valueOf", "compareTo");

    private static final Map<String, String> kRerouted = Map.ofEntries(
        Map.entry("org/bukkit/block/banner/PatternType valueOf", "valueOf_PatternType"),
        Map.entry("org/bukkit/map/MapCursor$Type valueOf", "valueOf_MapCursorType"),
        Map.entry("org/bukkit/enchantments/Enchantment getByName", "getByName_Enchantment"),
        Map.entry("org/bukkit/block/Biome valueOf", "valueOf_Biome"),
        Map.entry("org/bukkit/entity/EntityType valueOf", "valueOf_EntityType"),
        Map.entry("org/bukkit/entity/EntityType fromName", "fromName_EntityType"),
        Map.entry("org/bukkit/potion/PotionEffectType getByName", "getByName_PotionEffectType"),
        Map.entry("org/bukkit/potion/PotionType valueOf", "valueOf_PotionType"),
        Map.entry("org/bukkit/Particle valueOf", "valueOf_Particle"),
        Map.entry("org/bukkit/loot/LootTables valueOf", "valueOf_LootTables"),
        Map.entry("org/bukkit/attribute/Attribute valueOf", "valueOf_Attribute"),
        Map.entry("org/bukkit/inventory/ItemFlag valueOf", "valueOf_ItemFlag"),
        Map.entry("org/bukkit/Sound valueOf", "valueOf_Sound"));

    private final byte[] source;
    private final int poolEnd;
    private final byte[] body;
    private boolean widenSelf;
    private boolean hideAnnotation;
    private Predicate<String> formerEnum = name -> false;
    private final List<byte[]> entries = new ArrayList<>();
    private final Map<String, Integer> added = new HashMap<>();
    private boolean changed;
    private boolean formerClassCalls;
    private int formerSuper = -1;
    private int superObject;

    private ConstantPoolPatch(byte[] source) {
        this.source = source;
        int count = u2(source, 8);
        entries.add(null);
        int at = 10;
        for (int index = 1; index < count; index++) {
            int tag = source[at] & 0xFF;
            int size = switch (tag) {
                case kUtf8 -> 3 + u2(source, at + 1);
                case 3, 4, 9, 10, 11, 12, 17, 18 -> 5;
                case 5, 6 -> 9;
                case 7, 8, 16, 19, 20 -> 3;
                case 15 -> 4;
                default -> throw new IllegalArgumentException("constant pool tag " + tag);
            };
            byte[] entry = new byte[size];
            System.arraycopy(source, at, entry, 0, size);
            entries.add(entry);
            if (tag == 5 || tag == 6) {
                entries.add(null);
                index++;
            }
            at += size;
        }
        poolEnd = at;
        body = java.util.Arrays.copyOfRange(source, poolEnd, source.length);
    }

    record Result(byte[] bytes, boolean formerClassCalls) {}

    record Env(Predicate<String> becameInterface, Predicate<String> modernMaterial, Predicate<String> legacyMaterial,
               Predicate<String> widensEnum) {}

    /** Null when the class needs no change. */
    static Result apply(byte[] bytes, ApiVersion plugin, Env env) {
        ConstantPoolPatch patch = new ConstantPoolPatch(bytes);
        patch.renameClasses(LegacyRenames.classRenames(plugin));
        patch.patchMembers(plugin, env.becameInterface(), env.modernMaterial(), env.legacyMaterial());
        patch.implementFormerSuper(env.becameInterface());
        if (plugin.isOlderThan(ApiVersion.kRegistryEnums)) {
            patch.widenEnum(env.widensEnum());
        }
        patch.formerEnum = env.becameInterface();
        patch.walkBody(plugin, env.modernMaterial(), env.legacyMaterial());
        return patch.changed || patch.formerClassCalls ? new Result(patch.write(), patch.formerClassCalls) : null;
    }

    /** Whether the class takes Enum where a former enum such as Sound may now be passed, and can be widened to Object. */
    static boolean widensEnum(byte[] bytes, Predicate<String> widensOwn) {
        return new ConstantPoolPatch(bytes).widenable(widensOwn);
    }

    private static int u2(byte[] bytes, int at) {
        return (bytes[at] & 0xFF) << 8 | bytes[at + 1] & 0xFF;
    }

    private static void put2(byte[] bytes, int at, int value) {
        bytes[at] = (byte) (value >> 8);
        bytes[at + 1] = (byte) value;
    }

    private int tag(int index) {
        return entries.get(index)[0] & 0xFF;
    }

    private int ref(int index, int field) {
        return u2(entries.get(index), 1 + field * 2);
    }

    private String utf8(int index) {
        byte[] entry = entries.get(index);
        return new String(entry, 3, entry.length - 3, StandardCharsets.ISO_8859_1);
    }

    private String className(int classIndex) {
        return utf8(ref(classIndex, 0));
    }

    private int append(String key, byte[] entry) {
        Integer known = added.get(key);
        if (known != null) {
            return known;
        }
        entries.add(entry);
        added.put(key, entries.size() - 1);
        return entries.size() - 1;
    }

    private int addUtf8(String text) {
        byte[] raw = text.getBytes(StandardCharsets.ISO_8859_1);
        byte[] entry = new byte[3 + raw.length];
        entry[0] = kUtf8;
        put2(entry, 1, raw.length);
        System.arraycopy(raw, 0, entry, 3, raw.length);
        return append("u " + text, entry);
    }

    private int addPair(int tag, String key, int first, int second) {
        byte[] entry = new byte[5];
        entry[0] = (byte) tag;
        put2(entry, 1, first);
        put2(entry, 3, second);
        return append(tag + " " + key, entry);
    }

    private int addClass(String name) {
        byte[] entry = new byte[3];
        entry[0] = kClass;
        put2(entry, 1, addUtf8(name));
        return append("c " + name, entry);
    }

    private void renameClasses(Map<String, String> renames) {
        for (int index = 1; index < entries.size(); index++) {
            byte[] entry = entries.get(index);
            if (entry == null || entry[0] != kUtf8) {
                continue;
            }
            String text = utf8(index);
            String patched = text.equals(kLegacyMysqlDriver) ? kModernMysqlDriver : replaceTokens(text, renames);
            if (!patched.equals(text)) {
                byte[] raw = patched.getBytes(StandardCharsets.ISO_8859_1);
                byte[] replaced = new byte[3 + raw.length];
                replaced[0] = kUtf8;
                put2(replaced, 1, raw.length);
                System.arraycopy(raw, 0, replaced, 3, raw.length);
                entries.set(index, replaced);
                changed = true;
            }
        }
    }

    private static boolean isNameChar(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '/' || c == '$';
    }

    private static boolean startsPackage(String text, int at) {
        return text.startsWith("org/", at) || text.startsWith("java/", at);
    }

    /** A class name after primitive parameters in a descriptor, as in "(BIILjava/util/EnumSet;)V". */
    private static int glued(String text, int from, int to) {
        for (int at = from + 2; at < to; at++) {
            if (startsPackage(text, at) && text.charAt(at - 1) == 'L'
                    && text.substring(from, at - 1).chars().allMatch(c -> "BCDFIJSZ".indexOf(c) >= 0)) {
                return at;
            }
        }
        return -1;
    }

    private static String replaceTokens(String text, Map<String, String> renames) {
        if (renames.isEmpty() || !text.contains("org/") && !text.contains("java/")) {
            return text;
        }
        StringBuilder out = null;
        int copied = 0;
        for (int from = 0; from < text.length();) {
            if (!isNameChar(text.charAt(from))) {
                from++;
                continue;
            }
            int to = from;
            while (to < text.length() && isNameChar(text.charAt(to))) {
                to++;
            }
            int start = startsPackage(text, from) ? from : startsPackage(text, from + 1) ? from + 1 : glued(text, from, to);
            String replacement = start < 0 ? null : renames.get(text.substring(start, to));
            if (replacement != null) {
                out = out == null ? new StringBuilder() : out;
                out.append(text, copied, start).append(replacement);
                copied = to;
            }
            from = to;
        }
        return out == null ? text : out.append(text, copied, text.length()).toString();
    }

    private void patchMembers(ApiVersion plugin, Predicate<String> becameInterface, Predicate<String> modernMaterial,
            Predicate<String> legacyMaterial) {
        int original = entries.size();
        for (int index = 1; index < original; index++) {
            byte[] entry = entries.get(index);
            if (entry == null) {
                continue;
            }
            int tag = entry[0] & 0xFF;
            if (tag == kFieldref) {
                patchField(index, plugin, modernMaterial, legacyMaterial);
            } else if (tag == kMethodref || tag == kInterfaceMethodref) {
                patchMethod(index, plugin, becameInterface);
            } else if (tag == kMethodHandle) {
                patchHandle(index, becameInterface);
            }
        }
    }

    private void patchField(int index, ApiVersion plugin, Predicate<String> modernMaterial,
            Predicate<String> legacyMaterial) {
        String owner = className(ref(index, 0));
        if (!owner.startsWith("org/bukkit/")) {
            return;
        }
        int pair = ref(index, 1);
        String name = utf8(ref(pair, 0));
        String renamed = renameConstant(plugin, owner, name, modernMaterial, legacyMaterial);
        if (renamed.equals(name)) {
            return;
        }
        int pairIndex = addPair(kNameAndType, renamed + " " + ref(pair, 1), addUtf8(renamed), ref(pair, 1));
        put2(entries.get(index), 3, pairIndex);
        changed = true;
    }

    private static String renameConstant(ApiVersion plugin, String owner, String name,
            Predicate<String> modernMaterial, Predicate<String> legacyMaterial) {
        String renamed = LegacyRenames.rename(plugin, owner, name);
        if (owner.equals("org/bukkit/Material") && plugin.isOlderThan(ApiVersion.kFlattening)) {
            if (legacyMaterial.test("LEGACY_" + name)) {
                renamed = "LEGACY_" + name;
            } else if (!modernMaterial.test(renamed) && legacyMaterial.test("LEGACY_" + renamed)) {
                renamed = "LEGACY_" + renamed;
            }
        }
        return renamed;
    }

    private static boolean isEnumDescriptor(String text) {
        return text.contains(kEnumType) && text.indexOf('<') < 0
            && (text.charAt(0) == '(' || text.charAt(0) == 'L' || text.charAt(0) == '[');
    }

    private int enumClassIndex() {
        for (int index = 1; index < entries.size(); index++) {
            byte[] entry = entries.get(index);
            if (entry != null && (entry[0] & 0xFF) == kClass && utf8(ref(index, 0)).equals("java/lang/Enum")) {
                return index;
            }
        }
        return 0;
    }

    private boolean testsForEnum(int enumClass) {
        for (int at = 0; at + 2 < body.length; at++) {
            if ((body[at] & 0xFF) == kInstanceOf && u2(body, at + 1) == enumClass) {
                return true;
            }
        }
        return false;
    }

    private boolean widenable(Predicate<String> widensOwn) {
        int superClass = u2(body, 4);
        if (superClass == 0 || className(superClass).equals("java/lang/Enum")) {
            return false;
        }
        String selfName = className(u2(body, 2));
        int enumClass = enumClassIndex();
        if (enumClass > 0 && testsForEnum(enumClass)) {
            return false;
        }
        boolean mentions = false;
        for (int index = 1; index < entries.size(); index++) {
            byte[] entry = entries.get(index);
            if (entry == null) {
                continue;
            }
            int tag = entry[0] & 0xFF;
            if (tag == kFieldref && utf8(ref(ref(index, 1), 1)).contains(kEnumType)) {
                return false;
            }
            if (tag != kMethodref && tag != kInterfaceMethodref) {
                continue;
            }
            if (enumClass > 0 && ref(index, 0) == enumClass && !kEnumSafe.contains(utf8(ref(ref(index, 1), 0)))) {
                return false;
            }
            String desc = utf8(ref(ref(index, 1), 1));
            String owner = className(ref(index, 0));
            if (isEnumDescriptor(desc)) {
                mentions = true;
                if (!owner.equals(selfName) && !owner.equals("java/lang/Enum") && !owner.startsWith(kLegacyPrefix)
                        && !widensOwn.test(owner)) {
                    return false;
                }
            }
        }
        int at = 8 + 2 * u2(body, 6);
        for (int group = 0; group < 2; group++) {
            int count = u2(body, at);
            at += 2;
            for (int each = 0; each < count; each++) {
                String desc = utf8(u2(body, at + 4));
                if (desc.contains(kEnumType) && (group == 0 || desc.indexOf(')') < 0
                        || desc.indexOf(kEnumType, desc.indexOf(')')) >= 0)) {
                    return false;
                }
                mentions |= isEnumDescriptor(desc);
                at = skipAttributes(at + 6);
            }
        }
        return mentions;
    }

    private int skipAttributes(int at) {
        int count = u2(body, at);
        at += 2;
        for (int each = 0; each < count; each++) {
            at += 6 + ((body[at + 2] & 0xFF) << 24 | (body[at + 3] & 0xFF) << 16 | (body[at + 4] & 0xFF) << 8
                | body[at + 5] & 0xFF);
        }
        return at;
    }

    private void widenEnum(Predicate<String> widensOwn) {
        widenSelf = widenable(widensOwn);
        String selfName = className(u2(body, 2));
        int original = entries.size();
        for (int index = 1; index < original; index++) {
            byte[] entry = entries.get(index);
            if (entry == null) {
                continue;
            }
            int tag = entry[0] & 0xFF;
            if (tag != kFieldref && tag != kMethodref && tag != kInterfaceMethodref && tag != kInvokeDynamic) {
                continue;
            }
            int pair = ref(index, 1);
            String desc = utf8(ref(pair, 1));
            if (!isEnumDescriptor(desc)) {
                continue;
            }
            if (tag == kInvokeDynamic ? !widenSelf : className(ref(index, 0)).startsWith("java/")
                    || !(className(ref(index, 0)).equals(selfName) ? widenSelf : widensOwn.test(className(ref(index, 0))))) {
                continue;
            }
            String widened = desc.replace(kEnumType, "Ljava/lang/Object;");
            put2(entry, 3, addPair(kNameAndType, ref(pair, 0) + " " + widened, ref(pair, 0), addUtf8(widened)));
            changed = true;
        }
        if (widenSelf) {
            for (int index = 1; index < original; index++) {
                byte[] entry = entries.get(index);
                if (entry != null && (entry[0] & 0xFF) == kClass) {
                    String name = utf8(ref(index, 0));
                    if (name.startsWith("[") && name.endsWith(kEnumType) && name.indexOf('L') == name.lastIndexOf('[') + 1) {
                        put2(entry, 1, addUtf8(name.replace(kEnumType, "Ljava/lang/Object;")));
                        changed = true;
                    }
                }
            }
        }
        int enumClass = enumClassIndex();
        if (!widenSelf || enumClass == 0) {
            return;
        }
        int keep = addClass("java/lang/Enum");
        int object = addClass("java/lang/Object");
        for (int index = 1; index < original; index++) {
            byte[] entry = entries.get(index);
            if (entry != null && ((entry[0] & 0xFF) == kMethodref || (entry[0] & 0xFF) == kInterfaceMethodref)
                    && ref(index, 0) == enumClass) {
                String name = utf8(ref(ref(index, 1), 0));
                put2(entry, 1, name.equals("equals") || name.equals("hashCode") ? object : keep);
            }
        }
        put2(entries.get(enumClass), 1, addUtf8("java/lang/Object"));
        changed = true;
    }

    private int attributes(int at, ApiVersion plugin, Predicate<String> modern, Predicate<String> legacy) {
        int count = u2(body, at);
        at += 2;
        for (int each = 0; each < count; each++) {
            String name = utf8(u2(body, at));
            int length = (body[at + 2] & 0xFF) << 24 | (body[at + 3] & 0xFF) << 16 | (body[at + 4] & 0xFF) << 8
                | body[at + 5] & 0xFF;
            int data = at + 6;
            hideAnnotation = false;
            switch (name) {
                case "RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations" -> {
                    int pairs = u2(body, data);
                    int cursor = data + 2;
                    for (int annotation = 0; annotation < pairs; annotation++) {
                        cursor = annotation(cursor, plugin, modern, legacy);
                    }
                }
                case "RuntimeVisibleParameterAnnotations", "RuntimeInvisibleParameterAnnotations" -> {
                    int cursor = data + 1;
                    for (int param = 0; param < (body[data] & 0xFF); param++) {
                        int pairs = u2(body, cursor);
                        cursor += 2;
                        for (int annotation = 0; annotation < pairs; annotation++) {
                            cursor = annotation(cursor, plugin, modern, legacy);
                        }
                    }
                }
                case "AnnotationDefault" -> elementValue(data, plugin, modern, legacy);
                default -> {
                }
            }
            if (hideAnnotation) {
                String hidden = name.equals("AnnotationDefault") ? "AnnotationDefaultHidden" : name.replace("Visible", "Invisible");
                put2(body, at, addUtf8(hidden));
                changed = true;
            }
            at = data + length;
        }
        return at;
    }

    private int annotation(int at, ApiVersion plugin, Predicate<String> modern, Predicate<String> legacy) {
        int pairs = u2(body, at + 2);
        at += 4;
        for (int each = 0; each < pairs; each++) {
            at = elementValue(at + 2, plugin, modern, legacy);
        }
        return at;
    }

    private int elementValue(int at, ApiVersion plugin, Predicate<String> modern, Predicate<String> legacy) {
        char tag = (char) (body[at] & 0xFF);
        at++;
        switch (tag) {
            case 'e' -> {
                String type = utf8(u2(body, at));
                String name = utf8(u2(body, at + 2));
                if (type.startsWith("Lorg/bukkit/") && type.endsWith(";")
                        && formerEnum.test(type.substring(1, type.length() - 1))) {
                    hideAnnotation = true;
                } else if (type.startsWith("Lorg/bukkit/") && type.endsWith(";")) {
                    String renamed = renameConstant(plugin, type.substring(1, type.length() - 1), name, modern, legacy);
                    if (!renamed.equals(name)) {
                        put2(body, at + 2, addUtf8(renamed));
                        changed = true;
                    }
                }
                return at + 4;
            }
            case '@' -> {
                return annotation(at, plugin, modern, legacy);
            }
            case '[' -> {
                int count = u2(body, at);
                at += 2;
                for (int each = 0; each < count; each++) {
                    at = elementValue(at, plugin, modern, legacy);
                }
                return at;
            }
            default -> {
                return at + 2;
            }
        }
    }

    private void walkBody(ApiVersion plugin, Predicate<String> modern, Predicate<String> legacy) {
        int at = 8 + 2 * u2(body, 6);
        for (int group = 0; group < 2; group++) {
            int count = u2(body, at);
            at += 2;
            for (int each = 0; each < count; each++) {
                if (widenSelf) {
                    String desc = utf8(u2(body, at + 4));
                    if (isEnumDescriptor(desc)) {
                        put2(body, at + 4, addUtf8(desc.replace(kEnumType, "Ljava/lang/Object;")));
                        changed = true;
                    }
                }
                at = attributes(at + 6, plugin, modern, legacy);
            }
        }
        attributes(at, plugin, modern, legacy);
    }

    private void implementFormerSuper(Predicate<String> becameInterface) {
        int superClass = u2(source, poolEnd + 4);
        if (superClass == 0 || !className(superClass).startsWith("org/bukkit/")
                || !becameInterface.test(className(superClass))) {
            return;
        }
        int object = addClass("java/lang/Object");
        for (int index = 1; index < entries.size(); index++) {
            byte[] entry = entries.get(index);
            if (entry != null && (entry[0] & 0xFF) == kMethodref && ref(index, 0) == superClass
                    && utf8(ref(ref(index, 1), 0)).equals("<init>")) {
                put2(entry, 1, object);
            }
        }
        formerSuper = superClass;
        superObject = object;
        changed = true;
    }

    private void widenEnumCall(int index) {
        int pair = ref(index, 1);
        String desc = utf8(ref(pair, 1));
        if (!desc.contains(kEnumType)) {
            return;
        }
        String widened = desc.replace(kEnumType, "Ljava/lang/Object;");
        int pairIndex = addPair(kNameAndType, ref(pair, 0) + " " + widened, ref(pair, 0), addUtf8(widened));
        put2(entries.get(index), 3, pairIndex);
        changed = true;
    }

    private void retarget(int index, LegacyCalls.Target target) {
        int pairIndex = addPair(kNameAndType, target.name() + " " + target.descriptor(), addUtf8(target.name()),
            addUtf8(target.descriptor()));
        byte[] entry = entries.get(index);
        entry[0] = kMethodref;
        put2(entry, 1, addClass(target.owner()));
        put2(entry, 3, pairIndex);
        changed = true;
    }

    private void patchMethod(int index, ApiVersion plugin, Predicate<String> becameInterface) {
        String owner = className(ref(index, 0));
        int pair = ref(index, 1);
        String name = utf8(ref(pair, 0));
        String desc = utf8(ref(pair, 1));
        if (owner.equals(kLegacyEnumMap) || owner.equals(kLegacyEnumSet)) {
            widenEnumCall(index);
            return;
        }
        if (name.equals("valueOf") && owner.equals("java/lang/Enum") && desc.equals(LegacyCalls.kEnumValueOf)
                && plugin.isOlderThan(ApiVersion.kRegistryEnums)) {
            formerClassCalls = true;
            return;
        }
        if (owner.equals("java/lang/Enum") && (name.equals("equals") || name.equals("hashCode"))
                && plugin.isOlderThan(ApiVersion.kRegistryEnums)) {
            put2(entries.get(index), 1, addClass("java/lang/Object"));
            changed = true;
            return;
        }
        LegacyCalls.Target replaced = LegacyCalls.staticCall(plugin, owner, name, desc);
        if (replaced != null) {
            retarget(index, replaced);
            return;
        }
        if (LegacyCalls.instanceCall(plugin, owner, name, desc) != null
                || LegacyCalls.bridgesMaterial(plugin, owner, name, desc, false)) {
            formerClassCalls = true;
            return;
        }
        if (!owner.startsWith("org/bukkit/")) {
            return;
        }
        String helper = kRerouted.get(owner + " " + name);
        if (helper != null && desc.startsWith("(Ljava/lang/String;)L") && desc.length() > 21) {
            String dated = plugin.isOlderThan(new ApiVersion(1, 20, 5))
                && (helper.equals("valueOf_PatternType") || helper.equals("valueOf_MapCursorType")) ? helper + "Old" : helper;
            int pairIndex = addPair(kNameAndType, dated + " " + ref(pair, 1), addUtf8(dated), ref(pair, 1));
            byte[] entry = entries.get(index);
            entry[0] = kMethodref;
            put2(entry, 1, addClass(kNames));
            put2(entry, 3, pairIndex);
            changed = true;
        } else if ((entries.get(index)[0] & 0xFF) == kMethodref && becameInterface.test(owner) && !name.equals("<init>")) {
            formerClassCalls = true;
        }
    }

    private void patchHandle(int index, Predicate<String> becameInterface) {
        byte[] entry = entries.get(index);
        int kind = entry[1] & 0xFF;
        int target = u2(entry, 2);
        if ((kind != 5 && kind != 6) || tag(target) != kMethodref || !becameInterface.test(className(ref(target, 0)))) {
            return;
        }
        int interfaceRef = addPair(kInterfaceMethodref, ref(target, 0) + " " + ref(target, 1), ref(target, 0), ref(target, 1));
        entry[1] = (byte) (kind == 5 ? 9 : 6);
        put2(entry, 2, interfaceRef);
        changed = true;
    }

    private byte[] write() {
        if (entries.size() > 0xFFFF) {
            throw new IllegalStateException("constant pool overflow");
        }
        int size = 10;
        for (byte[] entry : entries) {
            size += entry == null ? 0 : entry.length;
        }
        byte[] out = new byte[size + body.length + (formerSuper >= 0 ? 2 : 0)];
        System.arraycopy(source, 0, out, 0, 10);
        put2(out, 8, entries.size());
        int at = 10;
        for (byte[] entry : entries) {
            if (entry != null) {
                System.arraycopy(entry, 0, out, at, entry.length);
                at += entry.length;
            }
        }
        if (formerSuper < 0) {
            System.arraycopy(body, 0, out, at, body.length);
            return out;
        }
        int interfaces = u2(body, 6);
        int restFrom = 8 + 2 * interfaces;
        System.arraycopy(body, 0, out, at, restFrom);
        put2(out, at + 4, superObject);
        put2(out, at + 6, interfaces + 1);
        put2(out, at + 8 + 2 * interfaces, formerSuper);
        System.arraycopy(body, restFrom, out, at + 10 + 2 * interfaces, body.length - restFrom);
        return out;
    }
}
