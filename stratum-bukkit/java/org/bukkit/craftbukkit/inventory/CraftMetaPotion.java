package org.bukkit.craftbukkit.inventory;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(org.basaltmc.stratum.bukkit.item.ItemMetas.Serial.class)
public class CraftMetaPotion extends org.basaltmc.stratum.bukkit.item.MetaPotion {
    public CraftMetaPotion(Material type, Map<String, Object> components) {
        super(type, components);
    }
}
