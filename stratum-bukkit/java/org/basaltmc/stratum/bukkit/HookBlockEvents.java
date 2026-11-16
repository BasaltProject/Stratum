package org.basaltmc.stratum.bukkit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

final class HookBlockEvents {
    private HookBlockEvents() {}

    private static Instrument instrument(String name) {
        return switch (name) {
            case "basedrum" -> Instrument.BASS_DRUM;
            case "snare" -> Instrument.SNARE_DRUM;
            case "hat" -> Instrument.STICKS;
            case "bass" -> Instrument.BASS_GUITAR;
            case "flute" -> Instrument.FLUTE;
            case "bell" -> Instrument.BELL;
            case "guitar" -> Instrument.GUITAR;
            case "chime" -> Instrument.CHIME;
            case "xylophone" -> Instrument.XYLOPHONE;
            case "iron_xylophone" -> Instrument.IRON_XYLOPHONE;
            case "cow_bell" -> Instrument.COW_BELL;
            case "didgeridoo" -> Instrument.DIDGERIDOO;
            case "bit" -> Instrument.BIT;
            case "banjo" -> Instrument.BANJO;
            case "pling" -> Instrument.PLING;
            case "zombie" -> Instrument.ZOMBIE;
            case "skeleton" -> Instrument.SKELETON;
            case "creeper" -> Instrument.CREEPER;
            case "dragon" -> Instrument.DRAGON;
            case "wither_skeleton" -> Instrument.WITHER_SKELETON;
            case "piglin" -> Instrument.PIGLIN;
            case "custom_head" -> Instrument.CUSTOM_HEAD;
            default -> Instrument.PIANO;
        };
    }

    static void install() {
        EventBridge.map("block.pre_dispense", io.papermc.paper.event.block.BlockPreDispenseEvent.class, f -> {
            ItemStack stack = PlayerEvents.stackOf(f.text("item"), 1);
            return stack == null ? null
                : new io.papermc.paper.event.block.BlockPreDispenseEvent(f.block(), stack, (int) f.whole("slot"));
        });
        EventBridge.map("block.failed_dispense", io.papermc.paper.event.block.BlockFailedDispenseEvent.class,
            f -> new io.papermc.paper.event.block.BlockFailedDispenseEvent(f.block()),
            (e, f) -> f.put("play_effect", ((io.papermc.paper.event.block.BlockFailedDispenseEvent) e).shouldPlayEffect()));
        EventBridge.map("block.brewing_fuel", org.bukkit.event.inventory.BrewingStandFuelEvent.class, f -> {
            ItemStack fuel = f.stack("item");
            return fuel == null ? null
                : new org.bukkit.event.inventory.BrewingStandFuelEvent(f.block(), fuel, (int) f.whole("fuel_power"));
        }, (e, f) -> {
            org.bukkit.event.inventory.BrewingStandFuelEvent fuel = (org.bukkit.event.inventory.BrewingStandFuelEvent) e;
            f.put("fuel_power", (long) fuel.getFuelPower());
            f.put("consuming", fuel.isConsuming());
        });
        EventBridge.map("block.cauldron_level", org.bukkit.event.block.CauldronLevelChangeEvent.class, f -> {
            Block block = f.block();
            BlockState state = block.getState();
            state.setBlockData(org.bukkit.Bukkit.createBlockData(f.text("new_block")));
            org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason reason;
            try {
                reason = org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason.valueOf(f.text("reason"));
            } catch (IllegalArgumentException unknown) {
                reason = org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason.UNKNOWN;
            }
            org.bukkit.entity.Entity who = f.whole("entity_id") >= 0 ? f.entity() : f.player();
            return new org.bukkit.event.block.CauldronLevelChangeEvent(block, who, reason, state);
        }, (e, f) -> f.put("new_block",
            ((org.bukkit.event.block.CauldronLevelChangeEvent) e).getNewState().getBlockData().getAsString()));
        EventBridge.map("block.note_play", org.bukkit.event.block.NotePlayEvent.class,
            f -> new org.bukkit.event.block.NotePlayEvent(f.block(), instrument(f.text("instrument")),
                new Note((int) f.whole("note"))));
        EventBridge.map("block.sponge_absorb", org.bukkit.event.block.SpongeAbsorbEvent.class, f -> {
            List<BlockState> states = new ArrayList<>();
            for (String line : f.text("blocks").split("\n")) {
                String[] at = line.split(",");
                if (at.length == 3) {
                    states.add(f.world().getBlockAt(Integer.parseInt(at[0]), Integer.parseInt(at[1]),
                        Integer.parseInt(at[2])).getState());
                }
            }
            return new org.bukkit.event.block.SpongeAbsorbEvent(f.block(), states);
        });
        EventBridge.map("block.compost", io.papermc.paper.event.block.CompostItemEvent.class, f -> {
            Material material = Material.matchMaterial(f.text("item"));
            return material == null ? null : new io.papermc.paper.event.block.CompostItemEvent(f.block(),
                new ItemStack(material, 1), f.flag("raises") ? 1 : 0);
        });
        EventBridge.map("block.redstone", org.bukkit.event.block.BlockRedstoneEvent.class,
            f -> new org.bukkit.event.block.BlockRedstoneEvent(f.block(), (int) f.whole("old_current"),
                (int) f.whole("new_current")),
            (e, f) -> f.put("new_current", (long) ((org.bukkit.event.block.BlockRedstoneEvent) e).getNewCurrent()));
        EventBridge.map("block.tnt_prime", com.destroystokyo.paper.event.block.TNTPrimeEvent.class, f -> {
            com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason reason;
            try {
                reason = com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason.valueOf(
                    f.text("reason").toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException unknown) {
                reason = com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason.ITEM;
            }
            org.bukkit.entity.Player who = f.text("player_name").isEmpty() ? null
                : BasaltServer.instance().playerByName(f.text("player_name"));
            return new com.destroystokyo.paper.event.block.TNTPrimeEvent(f.block(), reason, who);
        });
        EventBridge.map("block.tnt_prime", org.bukkit.event.block.TNTPrimeEvent.class, f -> {
            org.bukkit.event.block.TNTPrimeEvent.PrimeCause cause = switch (f.text("reason")) {
                case "explosion" -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.EXPLOSION;
                case "fire" -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.FIRE;
                case "projectile" -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.PROJECTILE;
                case "redstone" -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.REDSTONE;
                case "dispenser" -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.DISPENSER;
                default -> org.bukkit.event.block.TNTPrimeEvent.PrimeCause.PLAYER;
            };
            org.bukkit.entity.Player who = f.text("player_name").isEmpty() ? null
                : BasaltServer.instance().playerByName(f.text("player_name"));
            return new org.bukkit.event.block.TNTPrimeEvent(f.block(), cause, who, null);
        });
        EventBridge.map("block.moisture_change", org.bukkit.event.block.MoistureChangeEvent.class, f -> {
            Block block = f.block();
            BlockState state = block.getState();
            if (state.getBlockData() instanceof org.bukkit.block.data.type.Farmland farmland) {
                farmland.setMoisture((int) f.whole("moisture"));
                state.setBlockData(farmland);
            }
            return new org.bukkit.event.block.MoistureChangeEvent(block, state);
        });
        EventBridge.map("block.bell_ring", io.papermc.paper.event.block.BellRingEvent.class,
            f -> new io.papermc.paper.event.block.BellRingEvent(f.block(), PlayerEvents.face(f.text("direction")),
                f.player()));
        EventBridge.map("block.crafter_craft", org.bukkit.event.block.CrafterCraftEvent.class, f -> {
            Material material = Material.matchMaterial(f.text("result"));
            if (material == null) {
                return null;
            }
            ItemStack result = new ItemStack(material, (int) f.whole("count"));
            return new org.bukkit.event.block.CrafterCraftEvent(f.block(), new org.bukkit.inventory.ShapelessRecipe(
                new org.bukkit.NamespacedKey("minecraft", "crafter"), result), result);
        }, (e, f) -> {
            ItemStack result = ((org.bukkit.event.block.CrafterCraftEvent) e).getResult();
            f.put("result", result.getType().getKey().toString());
            f.put("count", (long) result.getAmount());
        });
        EventBridge.map("block.can_build", org.bukkit.event.block.BlockCanBuildEvent.class, f -> {
            Material material = Material.matchMaterial(f.text("block"));
            return material == null ? null : new org.bukkit.event.block.BlockCanBuildEvent(f.block(), f.player(),
                material.createBlockData(), f.flag("buildable"));
        }, (e, f) -> f.put("buildable", ((org.bukkit.event.block.BlockCanBuildEvent) e).isBuildable()));
        EventBridge.map("block.brew_start", org.bukkit.event.block.BrewingStartEvent.class, f -> {
            Material material = Material.matchMaterial(f.text("source"));
            return material == null ? null : new org.bukkit.event.block.BrewingStartEvent(f.block(),
                new ItemStack(material, 1), (int) f.whole("total_time"));
        });
        EventBridge.map("block.dispense_loot", org.bukkit.event.block.BlockDispenseLootEvent.class, f -> {
            org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(f.text("loot_table"));
            org.bukkit.loot.LootTable table = key == null ? null : org.bukkit.Bukkit.getLootTable(key);
            List<ItemStack> loot = new ArrayList<>();
            for (String line : f.text("loot").split("\n")) {
                int bar = line.indexOf('|');
                ItemStack stack = bar < 0 ? null : PlayerEvents.stackOf(line.substring(0, bar),
                    Long.parseLong(line.substring(bar + 1)));
                if (stack != null) {
                    loot.add(stack);
                }
            }
            return table == null ? null : new org.bukkit.event.block.BlockDispenseLootEvent(f.player(), f.block(), loot,
                table);
        }, (e, f) -> {
            StringBuilder lines = new StringBuilder();
            for (ItemStack stack : ((org.bukkit.event.block.BlockDispenseLootEvent) e).getDispensedLoot()) {
                if (stack != null && !stack.getType().isAir()) {
                    lines.append(lines.length() == 0 ? "" : "\n").append(stack.getType().getKey()).append('|')
                        .append(stack.getAmount());
                }
            }
            f.put("loot", lines.toString());
        });
        EventBridge.map("block.vault_display_item", org.bukkit.event.block.VaultDisplayItemEvent.class,
            f -> new org.bukkit.event.block.VaultDisplayItemEvent(f.block(),
                PlayerEvents.stackOf(f.text("item"), f.whole("count"))), (e, f) -> {
                ItemStack shown = ((org.bukkit.event.block.VaultDisplayItemEvent) e).getDisplayItem();
                boolean none = shown == null || shown.getType().isAir();
                f.put("item", none ? "minecraft:air" : shown.getType().getKey().toString());
                f.put("count", none ? 0L : (long) shown.getAmount());
            });
        EventBridge.map("block.vault_change_state", io.papermc.paper.event.block.VaultChangeStateEvent.class, f -> {
            org.bukkit.block.data.type.Vault.State from = org.bukkit.block.data.type.Vault.State.valueOf(
                f.text("from").toUpperCase(java.util.Locale.ROOT));
            org.bukkit.block.data.type.Vault.State to = org.bukkit.block.data.type.Vault.State.valueOf(
                f.text("to").toUpperCase(java.util.Locale.ROOT));
            return new io.papermc.paper.event.block.VaultChangeStateEvent(f.block(), f.player(), from, to);
        });
        EventBridge.map("block.brew", org.bukkit.event.inventory.BrewEvent.class, f -> {
            if (!(f.block().getState() instanceof org.bukkit.block.BrewingStand stand)) {
                return null;
            }
            List<ItemStack> results = new ArrayList<>();
            for (String id : f.text("results").split("\n", -1)) {
                Material material = id.isEmpty() ? null : Material.matchMaterial(id);
                results.add(material == null ? null : new ItemStack(material, 1));
            }
            return new org.bukkit.event.inventory.BrewEvent(f.block(), stand.getInventory(), results,
                (int) f.whole("fuel"));
        });
    }
}
