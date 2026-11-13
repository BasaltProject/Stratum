// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.storage;
public interface WritableLevelData extends LevelData {
    default void setSpawn(LevelData.RespawnData respawnData) {
        return;
    }
}
