// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.advancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.data.worldgen.BootstrapContext;
public record AdvancementHolder(Identifier id, Advancement value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementHolder> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC, AdvancementHolder::id, Advancement.STREAM_CODEC, AdvancementHolder::value, AdvancementHolder::new);
    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AdvancementHolder holder && id.equals(holder.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public String toString() {
        return id.toString();
    }
    public void register(BootstrapContext<Advancement> output) {
    }
}
