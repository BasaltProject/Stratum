// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

/** Keeps the wire shape of an argument type the facade has no class for, so a command tree still decodes. */
final class OpaqueArgumentInfo implements ArgumentTypeInfo<ArgumentType<?>, OpaqueArgumentInfo.Template> {
    private final boolean carriesRegistryKey;

    OpaqueArgumentInfo(boolean carriesRegistryKey) {
        this.carriesRegistryKey = carriesRegistryKey;
    }

    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf out) {
        if (carriesRegistryKey) {
            out.writeIdentifier(template.registry);
        }
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf in) {
        return new Template(carriesRegistryKey ? in.readIdentifier() : null);
    }

    @Override
    public void serializeToJson(Template template, JsonObject out) {
        if (template.registry != null) {
            out.addProperty("registry", template.registry.toString());
        }
    }

    @Override
    public Template unpack(ArgumentType<?> argument) {
        return new Template(null);
    }

    final class Template implements ArgumentTypeInfo.Template<ArgumentType<?>> {
        private final Identifier registry;

        Template(Identifier registry) {
            this.registry = registry;
        }

        @Override
        public ArgumentType<?> instantiate(CommandBuildContext context) {
            return StringArgumentType.greedyString();
        }

        @Override
        public ArgumentTypeInfo<ArgumentType<?>, ?> type() {
            return OpaqueArgumentInfo.this;
        }
    }
}
