// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.flag;
public class FeatureFlagUniverse {
    private final String name;
    public FeatureFlagUniverse(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
