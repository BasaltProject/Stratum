// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {}
