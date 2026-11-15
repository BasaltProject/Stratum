package io.papermc.paper.command.brigadier.argument;

import com.google.common.collect.Range;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.predicate.BlockInWorldPredicate;
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate;
import io.papermc.paper.command.brigadier.argument.range.DoubleRangeProvider;
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider;
import io.papermc.paper.command.brigadier.argument.resolvers.AngleResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.ColumnBlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.ColumnFinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.math.Position;
import io.papermc.paper.math.Rotation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;

/** Brigadier argument types for plugin commands, parsed here and resolved against the Basalt server on demand. */
public final class BasaltArgumentProvider implements VanillaArgumentProvider {
    @FunctionalInterface
    private interface Parser<T> {
        T parse(StringReader reader) throws CommandSyntaxException;
    }

    private record Arg<T>(Parser<T> parser) implements ArgumentType<T> {
        @Override
        public T parse(StringReader reader) throws CommandSyntaxException {
            return parser.parse(reader);
        }
    }

    private static <T> ArgumentType<T> arg(Parser<T> parser) {
        return new Arg<>(parser);
    }

    private static CommandSyntaxException fail(StringReader reader, String message) {
        return new SimpleCommandExceptionType(new LiteralMessage(message)).createWithContext(reader);
    }

    private static boolean wordChar(char c) {
        return c != ' ' && c != '\t';
    }

    private static String word(StringReader reader) {
        int start = reader.getCursor();
        while (reader.canRead() && wordChar(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    private static String keyText(StringReader reader) {
        int start = reader.getCursor();
        while (reader.canRead() && (Character.isLetterOrDigit(reader.peek()) || "_-.:/".indexOf(reader.peek()) >= 0)) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    /** A token that may carry a bracketed tail such as `@a[distance=..5]` or `stick[custom_name="x y"]`. */
    private static String bracketed(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        int depth = 0;
        boolean quoted = false;
        while (reader.canRead()) {
            char c = reader.peek();
            if (quoted) {
                quoted = c != '"';
            } else if (c == '"') {
                quoted = true;
            } else if (c == '[' || c == '{') {
                depth++;
            } else if (c == ']' || c == '}') {
                depth--;
            } else if (depth == 0 && !wordChar(c)) {
                break;
            }
            reader.skip();
        }
        if (depth != 0 || quoted) {
            throw fail(reader, "Unclosed bracket or quote");
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    private static void space(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead() || reader.peek() != ' ') {
            throw fail(reader, "Incomplete position");
        }
        reader.skip();
    }

    private static Location origin(CommandSourceStack stack) {
        return stack.getLocation();
    }

    private static List<Entity> select(CommandSourceStack stack, String text) {
        if (text.startsWith("@")) {
            return Bukkit.selectEntities(stack.getSender(), text);
        }
        Player named = Bukkit.getPlayerExact(text);
        if (named == null) {
            try {
                named = Bukkit.getPlayer(UUID.fromString(text));
            } catch (IllegalArgumentException notAnId) {
                named = null;
            }
        }
        return named == null ? List.of() : List.of(named);
    }

    private static <T extends Entity> List<T> need(List<T> found, boolean single, String what)
            throws CommandSyntaxException {
        if (found.isEmpty()) {
            throw new SimpleCommandExceptionType(new LiteralMessage("No " + what + " was found")).create();
        }
        if (single && found.size() > 1) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Only one " + what + " is allowed, but the provided"
                + " selector allows more than one")).create();
        }
        return found;
    }

    private ArgumentType<EntitySelectorArgumentResolver> entities(boolean single) {
        return arg(reader -> {
            String text = bracketed(reader);
            return (EntitySelectorArgumentResolver) stack -> need(select(stack, text), single, "entity");
        });
    }

    private ArgumentType<PlayerSelectorArgumentResolver> players(boolean single) {
        return arg(reader -> {
            String text = bracketed(reader);
            return (PlayerSelectorArgumentResolver) stack -> {
                List<Player> found = new ArrayList<>();
                for (Entity each : select(stack, text)) {
                    if (each instanceof Player player) {
                        found.add(player);
                    }
                }
                return need(found, single, "player");
            };
        });
    }

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entity() {
        return entities(true);
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> player() {
        return players(true);
    }

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entities() {
        return entities(false);
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> players() {
        return players(false);
    }

    @Override
    public ArgumentType<PlayerProfileListResolver> playerProfiles() {
        return arg(reader -> {
            String text = bracketed(reader);
            return (PlayerProfileListResolver) stack -> {
                List<com.destroystokyo.paper.profile.PlayerProfile> profiles = new ArrayList<>();
                if (text.startsWith("@")) {
                    for (Entity each : Bukkit.selectEntities(stack.getSender(), text)) {
                        if (each instanceof Player player) {
                            profiles.add(player.getPlayerProfile());
                        }
                    }
                } else {
                    profiles.add(Bukkit.createProfile(text));
                }
                return profiles;
            };
        });
    }

    @Override
    public ArgumentType<BlockPositionResolver> blockPosition() {
        return arg(reader -> {
            String[] parts = threeParts(reader);
            return (BlockPositionResolver) stack -> {
                Location at = origin(stack);
                return Position.block(whole(parts[0], at.getX()), whole(parts[1], at.getY()), whole(parts[2], at.getZ()));
            };
        });
    }

    @Override
    public ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        return arg(reader -> {
            String[] parts = twoParts(reader);
            return (ColumnBlockPositionResolver) stack -> {
                Location at = origin(stack);
                int x = whole(parts[0], at.getX());
                int z = whole(parts[1], at.getZ());
                return new io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition() {
                    @Override
                    public int blockX() {
                        return x;
                    }

                    @Override
                    public int blockZ() {
                        return z;
                    }
                };
            };
        });
    }

    @Override
    public ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        return arg(reader -> {
            String[] parts = threeParts(reader);
            return (FinePositionResolver) stack -> {
                Location at = origin(stack);
                return Position.fine(real(parts[0], at.getX(), centerIntegers), real(parts[1], at.getY(), false),
                    real(parts[2], at.getZ(), centerIntegers));
            };
        });
    }

    @Override
    public ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        return arg(reader -> {
            String[] parts = twoParts(reader);
            return (ColumnFinePositionResolver) stack -> {
                Location at = origin(stack);
                double x = real(parts[0], at.getX(), centerIntegers);
                double z = real(parts[1], at.getZ(), centerIntegers);
                return new io.papermc.paper.command.brigadier.argument.position.ColumnFinePosition() {
                    @Override
                    public double x() {
                        return x;
                    }

                    @Override
                    public double z() {
                        return z;
                    }
                };
            };
        });
    }

    private static String[] threeParts(StringReader reader) throws CommandSyntaxException {
        String first = word(reader);
        space(reader);
        String second = word(reader);
        space(reader);
        return new String[] {first, second, word(reader)};
    }

    private static String[] twoParts(StringReader reader) throws CommandSyntaxException {
        String first = word(reader);
        space(reader);
        return new String[] {first, word(reader)};
    }

    private static double number(String text) {
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private static int whole(String text, double origin) {
        return text.startsWith("~") ? (int) Math.floor(origin + number(text.substring(1))) : (int) Math.floor(number(text));
    }

    private static double real(String text, double origin, boolean center) {
        if (text.startsWith("~")) {
            return origin + number(text.substring(1));
        }
        double value = number(text);
        return center && !text.contains(".") ? value + 0.5 : value;
    }

    @Override
    public ArgumentType<RotationResolver> rotation() {
        return arg(reader -> {
            String[] parts = twoParts(reader);
            return (RotationResolver) stack -> {
                Location at = origin(stack);
                return Rotation.rotation((float) real(parts[0], at.getYaw(), false),
                    (float) real(parts[1], at.getPitch(), false));
            };
        });
    }

    @Override
    public ArgumentType<AngleResolver> angle() {
        return arg(reader -> {
            String text = word(reader);
            return (AngleResolver) stack -> (float) real(text, origin(stack).getYaw(), false);
        });
    }

    @Override
    public ArgumentType<AxisSet> axes() {
        return arg(reader -> {
            Set<Axis> chosen = EnumSet.noneOf(Axis.class);
            for (char c : word(reader).toCharArray()) {
                try {
                    chosen.add(Axis.valueOf(String.valueOf(c).toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException bad) {
                    throw fail(reader, "Invalid swizzle, expected combination of 'x', 'y' and 'z'");
                }
            }
            return new Axes(chosen);
        });
    }

    private static final class Axes extends AbstractSet<Axis> implements AxisSet {
        private final Set<Axis> axes;

        Axes(Set<Axis> axes) {
            this.axes = axes;
        }

        @Override
        public Iterator<Axis> iterator() {
            return axes.iterator();
        }

        @Override
        public int size() {
            return axes.size();
        }
    }

    @Override
    public ArgumentType<BlockState> blockState() {
        return arg(reader -> {
            String text = bracketed(reader);
            try {
                return Bukkit.createBlockData(text).createBlockState();
            } catch (IllegalArgumentException bad) {
                throw fail(reader, "Unknown block '" + text + "'");
            }
        });
    }

    @Override
    public ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        return arg(reader -> {
            String text = bracketed(reader);
            Tag<Material> tag = text.startsWith("#")
                ? Bukkit.getTag(Tag.REGISTRY_BLOCKS, NamespacedKey.fromString(text.substring(1)), Material.class) : null;
            org.bukkit.block.data.BlockData data;
            try {
                data = text.startsWith("#") ? null : Bukkit.createBlockData(text);
            } catch (IllegalArgumentException bad) {
                throw fail(reader, "Unknown block '" + text + "'");
            }
            if (tag == null && data == null) {
                throw fail(reader, "Unknown block tag '" + text + "'");
            }
            return (block, loadChunk) -> {
                if (!loadChunk && !block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                    return BlockInWorldPredicate.Result.UNLOADED_CHUNK;
                }
                boolean matches = tag != null ? tag.isTagged(block.getType()) : block.getBlockData().matches(data);
                return matches ? BlockInWorldPredicate.Result.TRUE : BlockInWorldPredicate.Result.FALSE;
            };
        });
    }

    @Override
    public ArgumentType<ItemStack> itemStack() {
        return arg(reader -> {
            String text = bracketed(reader);
            try {
                return Bukkit.getItemFactory().createItemStack(text);
            } catch (IllegalArgumentException bad) {
                throw fail(reader, "Unknown item '" + text + "'");
            }
        });
    }

    @Override
    public ArgumentType<ItemStackPredicate> itemStackPredicate() {
        return arg(reader -> {
            String text = bracketed(reader);
            if (text.startsWith("#")) {
                Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, NamespacedKey.fromString(text.substring(1)),
                    Material.class);
                if (tag == null) {
                    throw fail(reader, "Unknown item tag '" + text + "'");
                }
                return stack -> tag.isTagged(stack.getType());
            }
            int bracket = text.indexOf('[');
            Material material = Material.matchMaterial(bracket < 0 ? text : text.substring(0, bracket));
            if (material == null) {
                throw fail(reader, "Unknown item '" + text + "'");
            }
            return stack -> stack.getType() == material;
        });
    }

    @Override
    public ArgumentType<NamedTextColor> namedColor() {
        return arg(reader -> {
            String text = word(reader);
            NamedTextColor color = NamedTextColor.NAMES.value(text.toLowerCase(Locale.ROOT));
            if (color == null) {
                throw fail(reader, "Unknown color '" + text + "'");
            }
            return color;
        });
    }

    @Override
    public ArgumentType<TextColor> hexColor() {
        return arg(reader -> {
            String text = word(reader);
            TextColor color = TextColor.fromHexString(text.startsWith("#") ? text : "#" + text);
            if (color == null) {
                throw fail(reader, "Invalid hex color '" + text + "'");
            }
            return color;
        });
    }

    private static String json(StringReader reader) throws CommandSyntaxException {
        reader.skipWhitespace();
        if (reader.canRead() && (reader.peek() == '{' || reader.peek() == '[' || reader.peek() == '"')) {
            return bracketed(reader);
        }
        return word(reader);
    }

    @Override
    public ArgumentType<Component> component() {
        return arg(reader -> {
            String text = json(reader);
            try {
                return text.startsWith("{") || text.startsWith("[") || text.startsWith("\"")
                    ? GsonComponentSerializer.gson().deserialize(text) : Component.text(text);
            } catch (RuntimeException bad) {
                throw fail(reader, "Invalid chat component '" + text + "'");
            }
        });
    }

    @Override
    public ArgumentType<Style> style() {
        return arg(reader -> {
            String text = json(reader);
            try {
                return GsonComponentSerializer.gson().deserialize(text).style();
            } catch (RuntimeException bad) {
                throw fail(reader, "Invalid style '" + text + "'");
            }
        });
    }

    @Override
    public ArgumentType<SignedMessageResolver> signedMessage() {
        return arg(reader -> {
            String text = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            return new SignedMessageResolver() {
                @Override
                public String content() {
                    return text;
                }

                @Override
                public CompletableFuture<SignedMessage> resolveSignedMessage(String argumentName,
                        com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
                    return CompletableFuture.completedFuture(SignedMessage.system(text, null));
                }
            };
        });
    }

    @Override
    public ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        return arg(reader -> {
            String text = word(reader);
            for (DisplaySlot slot : DisplaySlot.values()) {
                if (slot.getId().equals(text)) {
                    return slot;
                }
            }
            throw fail(reader, "Unknown display slot '" + text + "'");
        });
    }

    @Override
    public ArgumentType<NamespacedKey> namespacedKey() {
        return arg(reader -> {
            String text = keyText(reader);
            NamespacedKey key = NamespacedKey.fromString(text);
            if (key == null) {
                throw fail(reader, "Invalid identifier '" + text + "'");
            }
            return key;
        });
    }

    @Override
    public ArgumentType<Key> key() {
        return arg(reader -> {
            String text = keyText(reader);
            try {
                return Key.key(text);
            } catch (RuntimeException bad) {
                throw fail(reader, "Invalid identifier '" + text + "'");
            }
        });
    }

    private static <T extends Comparable<T>> Range<T> range(StringReader reader, java.util.function.Function<String, T> read)
            throws CommandSyntaxException {
        String text = word(reader);
        try {
            int dots = text.indexOf("..");
            if (dots < 0) {
                return Range.singleton(read.apply(text));
            }
            String low = text.substring(0, dots);
            String high = text.substring(dots + 2);
            if (low.isEmpty() && high.isEmpty()) {
                return Range.all();
            }
            if (low.isEmpty()) {
                return Range.atMost(read.apply(high));
            }
            return high.isEmpty() ? Range.atLeast(read.apply(low)) : Range.closed(read.apply(low), read.apply(high));
        } catch (NumberFormatException bad) {
            throw fail(reader, "Invalid range '" + text + "'");
        }
    }

    @Override
    public ArgumentType<IntegerRangeProvider> integerRange() {
        return arg(reader -> {
            Range<Integer> range = range(reader, Integer::valueOf);
            return (IntegerRangeProvider) () -> range;
        });
    }

    @Override
    public ArgumentType<DoubleRangeProvider> doubleRange() {
        return arg(reader -> {
            Range<Double> range = range(reader, Double::valueOf);
            return (DoubleRangeProvider) () -> range;
        });
    }

    private static <E extends Enum<E>> E constant(StringReader reader, Class<E> type, String what)
            throws CommandSyntaxException {
        String text = word(reader);
        try {
            return Enum.valueOf(type, text.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException bad) {
            throw fail(reader, "Unknown " + what + " '" + text + "'");
        }
    }

    @Override
    public ArgumentType<World> world() {
        return arg(reader -> {
            String text = keyText(reader);
            World found = Bukkit.getWorld(text);
            NamespacedKey key = NamespacedKey.fromString(text);
            if (found == null && key != null) {
                found = Bukkit.getWorld(key);
            }
            if (found == null) {
                throw fail(reader, "Unknown dimension '" + text + "'");
            }
            return found;
        });
    }

    @Override
    public ArgumentType<GameMode> gameMode() {
        return arg(reader -> constant(reader, GameMode.class, "game mode"));
    }

    @Override
    public ArgumentType<HeightMap> heightMap() {
        return arg(reader -> constant(reader, HeightMap.class, "heightmap"));
    }

    @Override
    public ArgumentType<UUID> uuid() {
        return arg(reader -> {
            String text = word(reader);
            try {
                return UUID.fromString(text);
            } catch (IllegalArgumentException bad) {
                throw fail(reader, "Invalid UUID '" + text + "'");
            }
        });
    }

    @Override
    public ArgumentType<Criteria> objectiveCriteria() {
        return arg(reader -> Criteria.create(word(reader)));
    }

    @Override
    public ArgumentType<LookAnchor> entityAnchor() {
        return arg(reader -> constant(reader, LookAnchor.class, "anchor"));
    }

    @Override
    public ArgumentType<Integer> time(int minTicks) {
        return arg(reader -> {
            String text = word(reader);
            double scale = 1.0;
            String digits = text;
            if (!text.isEmpty()) {
                switch (text.charAt(text.length() - 1)) {
                    case 'd' -> scale = 24000.0;
                    case 's' -> scale = 20.0;
                    case 't' -> scale = 1.0;
                    default -> scale = 0.0;
                }
                if (scale != 0.0) {
                    digits = text.substring(0, text.length() - 1);
                } else {
                    scale = 1.0;
                }
            }
            int ticks;
            try {
                ticks = Math.round((float) (Double.parseDouble(digits) * scale));
            } catch (NumberFormatException bad) {
                throw fail(reader, "Invalid time '" + text + "'");
            }
            if (ticks < minTicks) {
                throw fail(reader, "Tick count must not be less than " + minTicks + ", found " + ticks);
            }
            return ticks;
        });
    }

    @Override
    public ArgumentType<Mirror> templateMirror() {
        return arg(reader -> constant(reader, Mirror.class, "mirror"));
    }

    @Override
    public ArgumentType<StructureRotation> templateRotation() {
        return arg(reader -> constant(reader, StructureRotation.class, "rotation"));
    }

    @Override
    public <T> ArgumentType<TypedKey<T>> resourceKey(RegistryKey<T> registryKey) {
        return arg(reader -> {
            String text = keyText(reader);
            try {
                return TypedKey.create(registryKey, Key.key(text));
            } catch (RuntimeException bad) {
                throw fail(reader, "Invalid identifier '" + text + "'");
            }
        });
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> ArgumentType<T> resource(RegistryKey<T> registryKey) {
        return arg(reader -> {
            String text = keyText(reader);
            NamespacedKey key = NamespacedKey.fromString(text);
            Object found = key == null ? null : ((org.bukkit.Registry) RegistryAccess.registryAccess()
                .getRegistry((RegistryKey) registryKey)).get(key);
            if (found == null) {
                throw fail(reader, "Unknown " + registryKey.key().value() + " '" + text + "'");
            }
            return (T) found;
        });
    }
}
