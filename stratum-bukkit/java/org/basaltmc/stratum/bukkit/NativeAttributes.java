package org.basaltmc.stratum.bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

/** Attribute instances of a fake player: base values, modifiers and the computed value are held natively. */
final class NativeAttributes {
    private final FakePlayerState owner;
    private final Map<Attribute, AttributeInstance> made = new HashMap<>();

    NativeAttributes(FakePlayerState owner) {
        this.owner = owner;
    }

    synchronized AttributeInstance attribute(Attribute attribute) {
        if (!owner.bound()) {
            return null;
        }
        return org.basaltmc.stratum.bukkit.facade.Memo.get(made, attribute, key -> {
            double[] range = KeyedFacts.attributeRange(key.getKey().getKey());
            WorldNatives.npcAttrRegister(owner.world(), owner.id(), key.getKey().toString(), key.getDefaultValue(),
                range == null ? 1.0 : range[1], range == null ? 0.0 : range[2]);
            return new View(owner, key);
        });
    }

    private static final class View implements AttributeInstance {
        private final FakePlayerState owner;
        private final Attribute attribute;
        private final String key;

        View(FakePlayerState owner, Attribute attribute) {
            this.owner = owner;
            this.attribute = attribute;
            this.key = attribute.getKey().toString();
        }

        @Override
        public Attribute getAttribute() {
            return attribute;
        }

        @Override
        public double getBaseValue() {
            double value = WorldNatives.npcAttrBase(owner.world(), owner.id(), key);
            return Double.isNaN(value) ? 0.0 : value;
        }

        @Override
        public void setBaseValue(double base) {
            WorldNatives.npcAttrSetBase(owner.world(), owner.id(), key, base);
        }

        @Override
        public Collection<AttributeModifier> getModifiers() {
            String[] flat = WorldNatives.npcAttrModifiers(owner.world(), owner.id(), key);
            List<AttributeModifier> all = new ArrayList<>();
            for (int index = 0; index + 3 < flat.length; index += 4) {
                all.add(new AttributeModifier(NamespacedKey.fromString(flat[index]), Double.parseDouble(flat[index + 1]),
                    AttributeModifier.Operation.values()[Integer.parseInt(flat[index + 2])],
                    EquipmentSlotGroup.getByName(flat[index + 3])));
            }
            return all;
        }

        @Override
        public void addModifier(AttributeModifier modifier) {
            WorldNatives.npcAttrAdd(owner.world(), owner.id(), key, modifier.getKey().asString(), modifier.getAmount(),
                modifier.getOperation().ordinal(), modifier.getSlotGroup().toString());
        }

        @Override
        public void removeModifier(AttributeModifier modifier) {
            WorldNatives.npcAttrRemove(owner.world(), owner.id(), key, modifier.getKey().asString());
        }

        @Override
        public double getValue() {
            double value = WorldNatives.npcAttrValue(owner.world(), owner.id(), key);
            return Double.isNaN(value) ? 0.0 : value;
        }

        @Override
        public double getDefaultValue() {
            return attribute.getDefaultValue();
        }

        @Override
        public AttributeModifier getModifier(net.kyori.adventure.key.Key id) {
            for (AttributeModifier modifier : getModifiers()) {
                if (modifier.getKey().asString().equals(id.asString())) {
                    return modifier;
                }
            }
            return null;
        }

        @Override
        public void removeModifier(net.kyori.adventure.key.Key id) {
            WorldNatives.npcAttrRemove(owner.world(), owner.id(), key, id.asString());
        }

        @Override
        public AttributeModifier getModifier(java.util.UUID id) {
            return getModifier(net.kyori.adventure.key.Key.key("minecraft", id.toString()));
        }

        @Override
        public void removeModifier(java.util.UUID id) {
            removeModifier(net.kyori.adventure.key.Key.key("minecraft", id.toString()));
        }

        @Override
        public void addTransientModifier(AttributeModifier modifier) {
            addModifier(modifier);
        }
    }
}
