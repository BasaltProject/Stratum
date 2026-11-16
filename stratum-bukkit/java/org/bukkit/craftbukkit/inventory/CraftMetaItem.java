package org.bukkit.craftbukkit.inventory;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import org.basaltmc.stratum.bukkit.gen.ItemMetaBase;

/** Root of every item meta; the state is the component map of the subclass, and these fields stay detached. */
public abstract class CraftMetaItem extends ItemMetaBase {
    final Map<DataComponentType<?>, Object> unhandledTags = new HashMap<>();

    public abstract boolean isEmpty();
}
