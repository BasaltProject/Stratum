package org.basaltmc.stratum.bukkit.legacy;

import java.util.Map;

/** The calls a pre-flattening plugin makes that no longer exist, and the static helper each one becomes. */
final class LegacyCalls {
    record Target(String owner, String name, String descriptor) {}

    private static final String kMaterials = "org/basaltmc/stratum/bukkit/legacy/LegacyMaterials";
    private static final String kEvil = "org/basaltmc/stratum/bukkit/legacy/LegacyEvil";
    private static final String kMaterial = "org/bukkit/Material";
    private static final String kBlock = "org/bukkit/block/Block";
    private static final String kBlockState = "org/bukkit/block/BlockState";
    private static final String kStack = "org/bukkit/inventory/ItemStack";
    private static final String kWorld = "org/bukkit/World";
    private static final String kMaterialType = "Lorg/bukkit/Material;";
    private static final String kString = "Ljava/lang/String;";

    private static final Map<String, Target> kStatic = Map.of(
        kMaterial + " getMaterial (I)" + kMaterialType, new Target(kMaterials, "getMaterial", "(I)" + kMaterialType),
        kMaterial + " values ()[" + kMaterialType, new Target(kMaterials, "values", "()[" + kMaterialType),
        kMaterial + " valueOf (" + kString + ")" + kMaterialType,
        new Target(kMaterials, "valueOf", "(" + kString + ")" + kMaterialType),
        kMaterial + " getMaterial (" + kString + ")" + kMaterialType,
        new Target(kMaterials, "getMaterial", "(" + kString + ")" + kMaterialType),
        kMaterial + " matchMaterial (" + kString + ")" + kMaterialType,
        new Target(kMaterials, "matchMaterial", "(" + kString + ")" + kMaterialType));

    private static final Map<String, Target> kInstance = Map.ofEntries(
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
            new Target(kEvil, "getBlockTypeIdAt", "(L" + kWorld + ";III)I")));

    private static final Target kWorldAtLocation =
        new Target(kEvil, "getBlockTypeIdAt", "(L" + kWorld + ";Lorg/bukkit/Location;)I");

    private LegacyCalls() {}

    /** A static call that keeps its shape and only changes its owner. */
    static Target staticCall(String owner, String name, String descriptor) {
        return kStatic.get(owner + " " + name + " " + descriptor);
    }

    /** A call on an instance that becomes a static helper taking the instance first. */
    static Target instanceCall(String owner, String name, String descriptor) {
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
        return kInstance.get(owner + " " + name + " " + descriptor);
    }
}
