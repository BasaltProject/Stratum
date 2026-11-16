// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.advancements;

import net.minecraft.world.level.storage.loot.ValidationContextSource;
public interface CriterionTriggerInstance {
    default void validate(ValidationContextSource validator) {
    }
}
