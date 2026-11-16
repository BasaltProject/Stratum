package org.basaltmc.stratum.bukkit;

/** The inventory of a fake player, in Bukkit slot numbers, with the worn slots mirrored to the native drawing. */
public final class FakePlayerState {
    private static final int kSlots = 41;
    private final byte[][] slots = new byte[kSlots][];
    private int held;
    private final DefaultAttributes attributes = new DefaultAttributes(new String[0]);
    private String world;
    private int id = -1;

    public synchronized byte[] item(int slot) {
        return slot < 0 || slot >= kSlots ? null : slots[slot];
    }

    public synchronized void setItem(int slot, byte[] stack) {
        if (slot < 0 || slot >= kSlots) {
            return;
        }
        slots[slot] = stack;
        if (id >= 0 && (slot == held || slot >= 36)) {
            push(slot == held ? 0 : equipSlot(slot));
        }
    }

    public synchronized int held() {
        return held;
    }

    public synchronized void setHeld(int slot) {
        held = Math.max(0, Math.min(8, slot));
        if (id >= 0) {
            push(0);
        }
    }

    public synchronized void clear() {
        java.util.Arrays.fill(slots, null);
        if (id >= 0) {
            for (int slot = 0; slot < 6; slot++) {
                WorldNatives.npcEquip(world, id, slot, null);
            }
        }
    }

    public synchronized void pushAll(String world, int id) {
        this.world = world;
        this.id = id;
        for (int slot = 0; slot < 6; slot++) {
            push(slot);
        }
    }

    private static int equipSlot(int bukkitSlot) {
        return switch (bukkitSlot) {
            case 40 -> 1;
            case 36 -> 2;
            case 37 -> 3;
            case 38 -> 4;
            default -> 5;
        };
    }

    private void push(int equipSlot) {
        int source = switch (equipSlot) {
            case 0 -> held;
            case 1 -> 40;
            case 2 -> 36;
            case 3 -> 37;
            case 4 -> 38;
            default -> 39;
        };
        WorldNatives.npcEquip(world, id, equipSlot, slots[source]);
    }

    public org.bukkit.attribute.AttributeInstance attribute(org.bukkit.attribute.Attribute attribute) {
        attributes.registerAttribute(attribute);
        return attributes.getAttribute(attribute);
    }
}
