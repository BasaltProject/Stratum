package org.bukkit.craftbukkit.inventory;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(org.basaltmc.stratum.bukkit.item.ItemMetas.Serial.class)
public class CraftMetaBundle extends org.basaltmc.stratum.bukkit.item.MetaContainers.Bundles {
    public CraftMetaBundle(Material type, Map<String, Object> components) {
        super(type, components);
    }
}
