package org.basaltmc.stratum.bukkit.legacy;

import java.util.Map;

/** The calls a pre-flattening plugin makes that no longer exist, and the static helper each one becomes. */
final class LegacyCalls {
    record Target(String owner, String name, String descriptor) {}

    private record Gated(Target target, ApiVersion before) {}

    private static final String kMaterials = "org/basaltmc/stratum/bukkit/legacy/LegacyMaterials";
    private static final String kEvil = "org/basaltmc/stratum/bukkit/legacy/LegacyEvil";
    private static final String kMaterial = "org/bukkit/Material";
    private static final String kBlock = "org/bukkit/block/Block";
    private static final String kBlockState = "org/bukkit/block/BlockState";
    private static final String kStack = "org/bukkit/inventory/ItemStack";
    private static final String kWorld = "org/bukkit/World";
    private static final String kMaterialType = "Lorg/bukkit/Material;";
    private static final String kString = "Ljava/lang/String;";
    private static final String kLegacyEnums = "org/basaltmc/stratum/bukkit/legacy/LegacyEnums";
    private static final String kNames = "org/basaltmc/stratum/bukkit/legacy/LegacyNames";
    private static final String kKey = "Lorg/bukkit/NamespacedKey;";
    private static final String kKeyed = "Lorg/bukkit/Keyed;";
    private static final String kObject = "Ljava/lang/Object;";
    static final String kEnumValueOf = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;";
    private static final java.util.Set<String> kKeyedReceiver =
        java.util.Set.of("getKey", "getKeyOrThrow", "getKeyOrNull", "key", "isRegistered");

    private static final String kConcatDescriptor = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;"
        + "Ljava/lang/invoke/MethodType;";
    private static final String kConcat = "org/basaltmc/stratum/bukkit/legacy/LegacyConcat";

    private static final Map<String, Target> kFlatteningStatic = Map.ofEntries(
        Map.entry(kMaterial + " getMaterial (I)" + kMaterialType,
            new Target(kMaterials, "getMaterial", "(I)" + kMaterialType)),
        Map.entry(kMaterial + " values ()[" + kMaterialType, new Target(kMaterials, "values", "()[" + kMaterialType)),
        Map.entry(kMaterial + " valueOf (" + kString + ")" + kMaterialType,
            new Target(kMaterials, "valueOf", "(" + kString + ")" + kMaterialType)),
        Map.entry(kMaterial + " getMaterial (" + kString + ")" + kMaterialType,
            new Target(kMaterials, "getMaterial", "(" + kString + ")" + kMaterialType)),
        Map.entry(kMaterial + " matchMaterial (" + kString + ")" + kMaterialType,
            new Target(kMaterials, "matchMaterial", "(" + kString + ")" + kMaterialType)),
        Map.entry("java/lang/String valueOf (" + kObject + ")" + kString,
            new Target(kLegacyEnums, "stringOf", "(" + kObject + ")" + kString)),
        Map.entry("java/util/Objects toString (" + kObject + ")" + kString,
            new Target(kLegacyEnums, "objectsToString", "(" + kObject + ")" + kString)),
        Map.entry("java/util/Objects toString (" + kObject + kString + ")" + kString,
            new Target(kLegacyEnums, "objectsToString", "(" + kObject + kString + ")" + kString)),
        Map.entry("java/lang/invoke/StringConcatFactory makeConcatWithConstants (" + kConcatDescriptor.substring(1)
            + kString + "[" + kObject + ")Ljava/lang/invoke/CallSite;",
            new Target(kConcat, "makeConcatWithConstants", kConcatDescriptor + kString + "[" + kObject
                + ")Ljava/lang/invoke/CallSite;")),
        Map.entry("java/lang/invoke/StringConcatFactory makeConcat (" + kConcatDescriptor.substring(1)
            + ")Ljava/lang/invoke/CallSite;",
            new Target(kConcat, "makeConcat", kConcatDescriptor + ")Ljava/lang/invoke/CallSite;")));

    private static final Map<String, Target> kFlatteningInstance = Map.ofEntries(
        Map.entry(kMaterial + " name ()" + kString,
            new Target(kMaterials, "name", "(" + kMaterialType + ")" + kString)),
        Map.entry(kMaterial + " toString ()" + kString,
            new Target(kMaterials, "toString", "(" + kMaterialType + ")" + kString)),
        Map.entry(kBlock + " getTypeId ()I", new Target(kEvil, "getTypeId", "(L" + kBlock + ";)I")),
        Map.entry(kBlock + " setTypeId (I)Z", new Target(kEvil, "setTypeId", "(L" + kBlock + ";I)Z")),
        Map.entry(kBlock + " setTypeId (IZ)Z", new Target(kEvil, "setTypeId", "(L" + kBlock + ";IZ)Z")),
        Map.entry(kBlock + " setTypeIdAndData (IBZ)Z",
            new Target(kEvil, "setTypeIdAndData", "(L" + kBlock + ";IBZ)Z")),
        Map.entry(kBlock + " setData (B)V", new Target(kEvil, "setData", "(L" + kBlock + ";B)V")),
        Map.entry(kBlock + " setData (BZ)V", new Target(kEvil, "setData", "(L" + kBlock + ";BZ)V")),
        Map.entry(kStack + " getTypeId ()I", new Target(kEvil, "getTypeId", "(L" + kStack + ";)I")),
        Map.entry(kStack + " setTypeId (I)V", new Target(kEvil, "setTypeId", "(L" + kStack + ";I)V")),
        Map.entry(kWorld + " getBlockTypeIdAt (III)I",
            new Target(kEvil, "getBlockTypeIdAt", "(L" + kWorld + ";III)I")),
        Map.entry(kMaterial + " ordinal ()I", new Target(kMaterials, "ordinal", "(" + kMaterialType + ")I")),
        Map.entry(kStack + " getDurability ()S", new Target(kEvil, "getDurability", "(L" + kStack + ";)S")),
        Map.entry(kStack + " setDurability (S)V", new Target(kEvil, "setDurability", "(L" + kStack + ";S)V")),
        Map.entry("java/lang/Object toString ()" + kString,
            new Target(kLegacyEnums, "toString", "(" + kObject + ")" + kString)),
        Map.entry("java/lang/StringBuilder append (" + kObject + ")Ljava/lang/StringBuilder;",
            new Target(kLegacyEnums, "append", "(Ljava/lang/StringBuilder;" + kObject + ")Ljava/lang/StringBuilder;")),
        Map.entry("java/lang/StringBuffer append (" + kObject + ")Ljava/lang/StringBuffer;",
            new Target(kLegacyEnums, "append", "(Ljava/lang/StringBuffer;" + kObject + ")Ljava/lang/StringBuffer;")),
        Map.entry("java/io/PrintStream println (" + kObject + ")V",
            new Target(kLegacyEnums, "println", "(Ljava/io/PrintStream;" + kObject + ")V")),
        Map.entry("java/io/PrintStream print (" + kObject + ")V",
            new Target(kLegacyEnums, "print", "(Ljava/io/PrintStream;" + kObject + ")V")),
        Map.entry("org/bukkit/Tag isTagged (Lorg/bukkit/Keyed;)Z",
            new Target(kMaterials, "isTagged", "(Lorg/bukkit/Tag;Lorg/bukkit/Keyed;)Z")));

    private static final Map<String, Target> kEnumStatic = Map.of(
        "org/bukkit/enchantments/Enchantment getByKey (" + kKey + ")Lorg/bukkit/enchantments/Enchantment;",
        new Target(kNames, "getByKey_Enchantment", "(" + kKey + ")Lorg/bukkit/enchantments/Enchantment;"),
        "org/bukkit/potion/PotionEffectType getByKey (" + kKey + ")Lorg/bukkit/potion/PotionEffectType;",
        new Target(kNames, "getByKey_PotionEffectType", "(" + kKey + ")Lorg/bukkit/potion/PotionEffectType;"));

    private static final String kEnumMapOwner = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumMap";
    private static final String kEnumSetOwner = "org/basaltmc/stratum/bukkit/legacy/LegacyEnumSet";

    private static final Map<String, Target> kGuavaEnum = Map.of(
        "com/google/common/collect/Maps newEnumMap (Ljava/lang/Class;)L" + kEnumMapOwner + ";",
        new Target(kEnumMapOwner, "newEnumMap", "(Ljava/lang/Class;)L" + kEnumMapOwner + ";"),
        "com/google/common/collect/Maps newEnumMap (Ljava/util/Map;)L" + kEnumMapOwner + ";",
        new Target(kEnumMapOwner, "newEnumMap", "(Ljava/util/Map;)L" + kEnumMapOwner + ";"),
        "com/google/common/collect/Sets newEnumSet (Ljava/lang/Iterable;Ljava/lang/Class;)L" + kEnumSetOwner + ";",
        new Target(kEnumSetOwner, "newEnumSet", "(Ljava/lang/Iterable;Ljava/lang/Class;)L" + kEnumSetOwner + ";"));

    private static final Map<String, Target> kEnumInstance = Map.of(
        "java/lang/Enum name ()" + kString, new Target(kLegacyEnums, "name", "(" + kObject + ")" + kString),
        "java/lang/Enum toString ()" + kString, new Target(kLegacyEnums, "toString", "(" + kObject + ")" + kString),
        "java/lang/Enum ordinal ()I", new Target(kLegacyEnums, "ordinal", "(" + kObject + ")I"),
        "java/lang/Enum compareTo (Ljava/lang/Enum;)I",
        new Target(kLegacyEnums, "compareTo", "(" + kObject + kObject + ")I"),
        "java/lang/Class isEnum ()Z", new Target(kLegacyEnums, "isEnum", "(Ljava/lang/Class;)Z"),
        "java/lang/Class getEnumConstants ()[" + kObject,
        new Target(kLegacyEnums, "enumConstants", "(Ljava/lang/Class;)[" + kObject),
        "org/bukkit/Registry get (" + kKey + ")" + kKeyed,
        new Target(kNames, "registryGet", "(Lorg/bukkit/Registry;" + kKey + ")" + kKeyed));

    private static final Target kWorldAtLocation =
        new Target(kEvil, "getBlockTypeIdAt", "(L" + kWorld + ";Lorg/bukkit/Location;)I");

    private LegacyCalls() {}

    /** A static call that keeps its shape and only changes its owner. */
    static Target staticCall(ApiVersion plugin, String owner, String name, String descriptor) {
        String key = owner + " " + name + " " + descriptor;
        if (plugin.isOlderThan(ApiVersion.kRegistryEnums)) {
            Target found = kEnumStatic.get(key);
            if (found == null) {
                found = kGuavaEnum.get(key);
            }
            if (found != null) {
                return found;
            }
        }
        return plugin.isOlderThan(ApiVersion.kFlattening) ? kFlatteningStatic.get(key) : null;
    }

    /** A call on an instance that becomes a static helper taking the instance first. */
    static Target instanceCall(ApiVersion plugin, String owner, String name, String descriptor) {
        String key = owner + " " + name + " " + descriptor;
        if (plugin.isOlderThan(ApiVersion.kRegistryEnums)) {
            Target found = kEnumInstance.get(key);
            if (found != null) {
                return found;
            }
        }
        if (!plugin.isOlderThan(ApiVersion.kFlattening)) {
            return null;
        }
        if (owner.startsWith("org/bukkit/block/") && !owner.equals(kBlock)) {
            if (name.equals("getTypeId") && descriptor.equals("()I")) {
                return new Target(kEvil, "getTypeId", "(L" + kBlockState + ";)I");
            }
            if (name.equals("setTypeId") && descriptor.equals("(I)Z")) {
                return new Target(kEvil, "setTypeId", "(L" + kBlockState + ";I)Z");
            }
        }
        if (owner.equals(kWorld) && name.equals("getBlockTypeIdAt") && descriptor.equals("(Lorg/bukkit/Location;)I")) {
            return kWorldAtLocation;
        }
        return kFlatteningInstance.get(key);
    }

    /** Whether a call into the Bukkit API passes or returns a Material, which a pre-flattening plugin holds as LEGACY_. */
    static boolean bridgesMaterial(ApiVersion plugin, String owner, String name, String descriptor, boolean isStatic) {
        return plugin.isOlderThan(ApiVersion.kFlattening) && owner.startsWith("org/bukkit/")
            && (descriptor.contains(kMaterialType) || hasCollection(descriptor) || !isStatic && modernReceiver(owner, name))
            && !owner.startsWith("org/bukkit/material/")
            && !(owner.equals(kMaterial) && isStatic) && !(owner.equals(kStack) && name.equals("<init>"));
    }

    static boolean hasCollection(String descriptor) {
        return descriptor.contains("Ljava/util/Set;") || descriptor.contains("Ljava/util/List;")
            || descriptor.contains("Ljava/util/Collection;");
    }

    /** A Material method that rejects a LEGACY_ receiver, so the receiver is made modern first. */
    static boolean modernReceiver(String owner, String name) {
        return owner.equals(kMaterial) && kKeyedReceiver.contains(name);
    }
}
