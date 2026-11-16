package org.basaltmc.stratum.bukkit;

import java.util.ArrayList;
import java.util.List;

final class HookPlayerEvents {
    private HookPlayerEvents() {}

    static void install() {
        EventBridge.map("player.command_send", org.bukkit.event.player.PlayerCommandSendEvent.class, f -> {
            List<String> names = new ArrayList<>();
            for (String name : f.text("commands").split("\n")) {
                if (!name.isEmpty()) {
                    names.add(name);
                }
            }
            return f.player() == null ? null : new org.bukkit.event.player.PlayerCommandSendEvent(f.player(), names);
        }, (e, f) -> f.put("commands", String.join("\n", ((org.bukkit.event.player.PlayerCommandSendEvent) e)
            .getCommands())));
        EventBridge.map("player.statistic", org.bukkit.event.player.PlayerStatisticIncrementEvent.class, f -> {
            Object[] parsed = org.basaltmc.stratum.bukkit.player.PlayerStatistics.parse(f.text("statistic"));
            if (parsed == null || f.player() == null) {
                return null;
            }
            org.bukkit.Statistic statistic = (org.bukkit.Statistic) parsed[0];
            int before = (int) f.whole("previous");
            int after = (int) f.whole("value");
            if (parsed.length == 1) {
                return new org.bukkit.event.player.PlayerStatisticIncrementEvent(f.player(), statistic, before, after);
            }
            return parsed[1] instanceof org.bukkit.Material material
                ? new org.bukkit.event.player.PlayerStatisticIncrementEvent(f.player(), statistic, before, after,
                    material)
                : new org.bukkit.event.player.PlayerStatisticIncrementEvent(f.player(), statistic, before, after,
                    (org.bukkit.entity.EntityType) parsed[1]);
        });
        EventBridge.map("player.recipe_discover", org.bukkit.event.player.PlayerRecipeDiscoverEvent.class, f -> {
            org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(f.text("recipe"));
            return f.player() == null || key == null ? null
                : new org.bukkit.event.player.PlayerRecipeDiscoverEvent(f.player(), key, true);
        });
        EventBridge.map("player.riptide", org.bukkit.event.player.PlayerRiptideEvent.class, f -> {
            org.bukkit.entity.Player who = f.player();
            org.bukkit.inventory.ItemStack item = f.stack("item");
            return who == null || item == null ? null : new org.bukkit.event.player.PlayerRiptideEvent(who, item,
                who.getLocation().getDirection().multiply(f.real("strength")));
        });
        EventBridge.map("player.velocity", org.bukkit.event.player.PlayerVelocityEvent.class,
            f -> f.player() == null ? null : new org.bukkit.event.player.PlayerVelocityEvent(f.player(),
                new org.bukkit.util.Vector(f.real("vx"), f.real("vy"), f.real("vz"))),
            (e, f) -> {
                org.bukkit.util.Vector velocity = ((org.bukkit.event.player.PlayerVelocityEvent) e).getVelocity();
                f.put("vx", velocity.getX());
                f.put("vy", velocity.getY());
                f.put("vz", velocity.getZ());
            });
        EventBridge.map("player.interact_entity_at", org.bukkit.event.player.PlayerInteractAtEntityEvent.class, f -> {
            org.bukkit.entity.Entity target = f.entity();
            return f.player() == null || target == null ? null
                : new org.bukkit.event.player.PlayerInteractAtEntityEvent(f.player(), target,
                    new org.bukkit.util.Vector(f.real("x"), f.real("y"), f.real("z")),
                    PlayerEvents.hand(f.text("hand")));
        });
        EventBridge.map("player.pickup_experience", com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent.class,
            f -> f.player() != null && f.world() != null
                && f.world().entity((int) f.whole("orb_id")) instanceof org.bukkit.entity.ExperienceOrb orb
                ? new com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent(f.player(), orb) : null);
        EventBridge.map("player.pickup_arrow", org.bukkit.event.player.PlayerPickupArrowEvent.class, f -> {
            BasaltWorld world = f.world();
            org.bukkit.entity.Entity arrow = world == null ? null : world.entity((int) f.whole("entity_id"));
            org.bukkit.Material material = org.bukkit.Material.matchMaterial(f.text("entity_type"));
            if (f.player() == null || !(arrow instanceof org.bukkit.entity.AbstractArrow shot) || material == null) {
                return null;
            }
            org.bukkit.entity.Entity item = org.basaltmc.stratum.bukkit.entity.EntityCore.template(world,
                org.bukkit.entity.EntityType.ITEM, arrow.getLocation().getX(), arrow.getLocation().getY(),
                arrow.getLocation().getZ(), 0f, 0f);
            ((org.basaltmc.stratum.bukkit.entity.Cored) item).core().stageNbt("Item",
                org.basaltmc.stratum.bukkit.entity.EntityItems.toNbt(new org.bukkit.inventory.ItemStack(material, 1)));
            return new org.bukkit.event.player.PlayerPickupArrowEvent(f.player(), (org.bukkit.entity.Item) item, shot);
        });
        EventBridge.map("player.recipe_book_click", com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent.class,
            f -> {
                org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(f.text("recipe"));
                return f.player() == null || key == null ? null
                    : new com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent(f.player(), key,
                        f.flag("make_all"));
            });
        EventBridge.map("player.take_lectern_book", org.bukkit.event.player.PlayerTakeLecternBookEvent.class, f ->
            f.player() != null && f.block().getState() instanceof org.bukkit.block.Lectern lectern
                ? new org.bukkit.event.player.PlayerTakeLecternBookEvent(f.player(), lectern) : null);
        EventBridge.map("player.lectern_page", io.papermc.paper.event.player.PlayerLecternPageChangeEvent.class, f -> {
            if (f.player() == null || !(f.block().getState() instanceof org.bukkit.block.Lectern lectern)) {
                return null;
            }
            int from = (int) f.whole("old_page");
            int to = (int) f.whole("new_page");
            return new io.papermc.paper.event.player.PlayerLecternPageChangeEvent(f.player(), lectern,
                lectern.getInventory().getItem(0), to < from
                    ? io.papermc.paper.event.player.PlayerLecternPageChangeEvent.PageChangeDirection.LEFT
                    : io.papermc.paper.event.player.PlayerLecternPageChangeEvent.PageChangeDirection.RIGHT, from, to);
        });
        EventBridge.map("player.sign_open", io.papermc.paper.event.player.PlayerOpenSignEvent.class, f -> {
            if (f.player() == null || !(f.block().getState() instanceof org.bukkit.block.Sign sign)) {
                return null;
            }
            return new io.papermc.paper.event.player.PlayerOpenSignEvent(f.player(), sign, signSide(f),
                switch (f.text("cause")) {
                    case "place" -> io.papermc.paper.event.player.PlayerOpenSignEvent.Cause.PLACE;
                    case "plugin" -> io.papermc.paper.event.player.PlayerOpenSignEvent.Cause.PLUGIN;
                    case "interact" -> io.papermc.paper.event.player.PlayerOpenSignEvent.Cause.INTERACT;
                    default -> io.papermc.paper.event.player.PlayerOpenSignEvent.Cause.UNKNOWN;
                });
        });
        EventBridge.map("player.sign_open", org.bukkit.event.player.PlayerSignOpenEvent.class, f -> {
            if (f.player() == null || !(f.block().getState() instanceof org.bukkit.block.Sign sign)) {
                return null;
            }
            return new org.bukkit.event.player.PlayerSignOpenEvent(f.player(), sign, signSide(f),
                switch (f.text("cause")) {
                    case "place" -> org.bukkit.event.player.PlayerSignOpenEvent.Cause.PLACE;
                    case "plugin" -> org.bukkit.event.player.PlayerSignOpenEvent.Cause.PLUGIN;
                    case "interact" -> org.bukkit.event.player.PlayerSignOpenEvent.Cause.INTERACT;
                    default -> org.bukkit.event.player.PlayerSignOpenEvent.Cause.UNKNOWN;
                });
        });
        EventBridge.map("player.input", org.bukkit.event.player.PlayerInputEvent.class, f -> {
            if (f.player() == null) {
                return null;
            }
            boolean forward = f.flag("forward");
            boolean backward = f.flag("backward");
            boolean left = f.flag("left");
            boolean right = f.flag("right");
            boolean jump = f.flag("jump");
            boolean sneak = f.flag("sneak");
            boolean sprint = f.flag("sprint");
            return new org.bukkit.event.player.PlayerInputEvent(f.player(), new org.bukkit.Input() {
                @Override
                public boolean isForward() {
                    return forward;
                }

                @Override
                public boolean isBackward() {
                    return backward;
                }

                @Override
                public boolean isLeft() {
                    return left;
                }

                @Override
                public boolean isRight() {
                    return right;
                }

                @Override
                public boolean isJump() {
                    return jump;
                }

                @Override
                public boolean isSneak() {
                    return sneak;
                }

                @Override
                public boolean isSprint() {
                    return sprint;
                }
            });
        });
        EventBridge.map("command.unknown", org.bukkit.event.command.UnknownCommandEvent.class, f -> {
            return f.player() == null ? null : new org.bukkit.event.command.UnknownCommandEvent(
                org.basaltmc.stratum.bukkit.brigadier.BasaltCommandSourceStack.of(f.player()), f.text("command"),
                UNKNOWN);
        }, (e, f) -> {
            net.kyori.adventure.text.Component message = ((org.bukkit.event.command.UnknownCommandEvent) e).message();
            if (message == null) {
                f.put("silent", true);
            } else if (!message.equals(UNKNOWN)) {
                f.put("message", net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(message));
            }
        });
    }

    private static org.bukkit.block.sign.Side signSide(EventBridge.Fields f) {
        return f.text("side").equals("back") ? org.bukkit.block.sign.Side.BACK : org.bukkit.block.sign.Side.FRONT;
    }

    private static final net.kyori.adventure.text.Component UNKNOWN =
        net.kyori.adventure.text.Component.translatable("command.unknown.command");
}
