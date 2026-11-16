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
    private static final int kMethodHandle = 15;
    private static final String kLegacyMysqlDriver = "com.mysql.jdbc.Driver";
    private static final String kModernMysqlDriver = "com.mysql.cj.jdbc.Driver";
    private static final String kEnumType = "Ljava/lang/Enum;";
    private static final String kLegacyEnumMap = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumMap";
    private static final String kLegacyEnumSet = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumSet";
    private static final String kNames = "org/basaltmc/stratum/bukkit/legacy/LegacyNames";

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
    }

    record Result(byte[] bytes, boolean formerClassCalls) {}

    /** Null when the class needs no change. */
    static Result apply(byte[] bytes, ApiVersion plugin, Predicate<String> becameInterface,
            Predicate<String> modernMaterial, Predicate<String> legacyMaterial) {
        ConstantPoolPatch patch = new ConstantPoolPatch(bytes);
        patch.renameClasses(LegacyRenames.classRenames(plugin));
        patch.patchMembers(plugin, becameInterface, modernMaterial, legacyMaterial);
        patch.implementFormerSuper(becameInterface);
        return patch.changed || patch.formerClassCalls ? new Result(patch.write(), patch.formerClassCalls) : null;
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
            int start = startsPackage(text, from) ? from : startsPackage(text, from + 1) ? from + 1 : -1;
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
        String renamed = LegacyRenames.rename(plugin, owner, name);
        if (owner.equals("org/bukkit/Material") && plugin.isOlderThan(ApiVersion.kFlattening)) {
            if (legacyMaterial.test("LEGACY_" + name)) {
                renamed = "LEGACY_" + name;
            } else if (!modernMaterial.test(renamed) && legacyMaterial.test("LEGACY_" + renamed)) {
                renamed = "LEGACY_" + renamed;
            }
        }
        if (renamed.equals(name)) {
            return;
        }
        int pairIndex = addPair(kNameAndType, renamed + " " + ref(pair, 1), addUtf8(renamed), ref(pair, 1));
        put2(entries.get(index), 3, pairIndex);
        changed = true;
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
        byte[] out = new byte[size + source.length - poolEnd + (formerSuper >= 0 ? 2 : 0)];
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
            System.arraycopy(source, poolEnd, out, at, source.length - poolEnd);
            return out;
        }
        int interfaces = u2(source, poolEnd + 6);
        int restFrom = poolEnd + 8 + 2 * interfaces;
        System.arraycopy(source, poolEnd, out, at, 8 + 2 * interfaces);
        put2(out, at + 4, superObject);
        put2(out, at + 6, interfaces + 1);
        put2(out, at + 8 + 2 * interfaces, formerSuper);
        System.arraycopy(source, restFrom, out, at + 10 + 2 * interfaces, source.length - restFrom);
        return out;
    }
}
