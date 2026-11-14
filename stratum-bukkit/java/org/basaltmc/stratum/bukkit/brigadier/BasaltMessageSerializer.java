package org.basaltmc.stratum.bukkit.brigadier;

import com.mojang.brigadier.Message;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public final class BasaltMessageSerializer implements MessageComponentSerializer {
    private record ComponentMessage(Component component) implements Message {
        @Override
        public String getString() {
            return PlainTextComponentSerializer.plainText().serialize(component);
        }
    }

    @Override
    public @NotNull Component deserialize(@NotNull Message input) {
        return input instanceof ComponentMessage held ? held.component() : Component.text(input.getString());
    }

    @Override
    public @NotNull Message serialize(@NotNull Component component) {
        return new ComponentMessage(component);
    }
}
