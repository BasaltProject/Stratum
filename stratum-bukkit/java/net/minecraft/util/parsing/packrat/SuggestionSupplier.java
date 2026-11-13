// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.parsing.packrat;

import java.util.stream.Stream;

public interface SuggestionSupplier<S> {
    Stream<String> possibleValues(ParseState<S> state);

    static <S> SuggestionSupplier<S> empty() {
        return state -> Stream.empty();
    }
}
