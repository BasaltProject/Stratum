package org.basaltmc.stratum.bukkit;

/** A view over the native fake player: health, posture, inventory and attributes all live there. */
public final class FakePlayerState {
    private final NativeAttributes attributes = new NativeAttributes(this);
    private String world;
    private int id = -1;

    public boolean bind(String world, String uuid, String name) {
        int made = WorldNatives.npcCreate(world, uuid, name, 0.0, 0.0, 0.0);
        if (made < 0) {
            return false;
        }
        this.world = world;
        this.id = made;
        return true;
    }

    public boolean bound() {
        return id >= 0;
    }

    public int id() {
        return id;
    }

    String world() {
        return world;
    }

    public void unbind() {
        if (id >= 0) {
            WorldNatives.npcRemove(world, id);
            id = -1;
        }
    }

    public byte[] item(int slot) {
        return id < 0 ? null : WorldNatives.npcItem(world, id, slot);
    }

    public void setItem(int slot, byte[] stack) {
        if (id >= 0) {
            WorldNatives.npcSetItem(world, id, slot, stack);
        }
    }

    public int held() {
        return id < 0 ? 0 : WorldNatives.npcHeld(world, id);
    }

    public void setHeld(int slot) {
        if (id >= 0) {
            WorldNatives.npcSetHeld(world, id, slot);
        }
    }

    public void clear() {
        if (id >= 0) {
            WorldNatives.npcClearItems(world, id);
        }
    }

    public double health() {
        return id < 0 ? 20.0 : WorldNatives.npcHealth(world, id);
    }

    public void setHealth(double health) {
        if (id >= 0) {
            WorldNatives.npcSetHealth(world, id, health);
        }
    }

    public double maxHealth() {
        if (id < 0) {
            return 20.0;
        }
        double value = WorldNatives.npcAttrValue(world, id, "minecraft:max_health");
        return Double.isNaN(value) ? 20.0 : value;
    }

    public int flags() {
        return id < 0 ? 0 : WorldNatives.npcPostureOf(world, id) & 0xFF;
    }

    public int pose() {
        return id < 0 ? 0 : WorldNatives.npcPostureOf(world, id) >> 8;
    }

    public void setPosture(int flags, int pose) {
        if (id >= 0) {
            WorldNatives.npcPosture(world, id, flags, pose);
        }
    }

    public org.bukkit.attribute.AttributeInstance attribute(org.bukkit.attribute.Attribute attribute) {
        return attributes.attribute(attribute);
    }
}
