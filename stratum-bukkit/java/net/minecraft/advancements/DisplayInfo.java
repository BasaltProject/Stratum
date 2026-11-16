// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.advancements;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStackTemplate;
public record DisplayInfo(ItemStackTemplate icon, Component title, Component description, Optional<ClientAsset.ResourceTexture> background, AdvancementType type, boolean showToast, boolean announceToChat, boolean hidden) {
    public static final Codec<DisplayInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
        ItemStackTemplate.CODEC.fieldOf("icon").forGetter(DisplayInfo::icon),
        ComponentSerialization.CODEC.fieldOf("title").forGetter(DisplayInfo::title),
        ComponentSerialization.CODEC.fieldOf("description").forGetter(DisplayInfo::description),
        ClientAsset.ResourceTexture.CODEC.optionalFieldOf("background").forGetter(DisplayInfo::background),
        AdvancementType.CODEC.optionalFieldOf("frame", AdvancementType.TASK).forGetter(DisplayInfo::type),
        Codec.BOOL.optionalFieldOf("show_toast", true).forGetter(DisplayInfo::showToast),
        Codec.BOOL.optionalFieldOf("announce_to_chat", true).forGetter(DisplayInfo::announceToChat),
        Codec.BOOL.optionalFieldOf("hidden", false).forGetter(DisplayInfo::hidden)).apply(i, DisplayInfo::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayInfo> STREAM_CODEC = StreamCodec.of(
        (output, info) -> {
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, info.title());
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, info.description());
            ItemStackTemplate.STREAM_CODEC.encode(output, info.icon());
            AdvancementType.STREAM_CODEC.encode(output, info.type());
            int flags = (info.background().isPresent() ? 1 : 0) | (info.showToast() ? 2 : 0) | (info.hidden() ? 4 : 0);
            output.writeInt(flags);
            info.background().ifPresent(texture -> output.writeIdentifier(texture.id()));
        },
        input -> {
            Component title = ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
            Component description = ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
            ItemStackTemplate icon = ItemStackTemplate.STREAM_CODEC.decode(input);
            AdvancementType frame = AdvancementType.STREAM_CODEC.decode(input);
            int flags = input.readInt();
            Optional<ClientAsset.ResourceTexture> background = (flags & 1) != 0
                ? Optional.of(new ClientAsset.ResourceTexture(input.readIdentifier())) : Optional.empty();
            return new DisplayInfo(icon, title, description, background, frame, (flags & 2) != 0, false, (flags & 4) != 0);
        });
}
