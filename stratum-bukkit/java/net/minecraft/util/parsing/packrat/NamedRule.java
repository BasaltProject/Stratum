// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.parsing.packrat;

public interface NamedRule<S, T> {
    Atom<T> name();

    Rule<S, T> value();
}
