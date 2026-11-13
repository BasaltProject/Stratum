// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public final class WorldPresetTags {
    public static final TagKey<WorldPreset> NORMAL = org.basaltmc.stratum.bukkit.facade.Tags.key(Registries.WORLD_PRESET, "normal");
    public static final TagKey<WorldPreset> EXTENDED = org.basaltmc.stratum.bukkit.facade.Tags.key(Registries.WORLD_PRESET, "extended");

    private WorldPresetTags() {}
}
