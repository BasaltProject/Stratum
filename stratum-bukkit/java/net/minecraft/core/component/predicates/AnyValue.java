// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.core.component.predicates;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;

public record AnyValue(DataComponentType<?> type) implements DataComponentPredicate {
    @Override
    public boolean matches(DataComponentGetter components) {
        return components.get(type) != null;
    }
}
