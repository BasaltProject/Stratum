// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VaultBlockEntity extends BlockEntity {
    public VaultBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("vault"), worldPosition, blockState);
    }

    public VaultBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public ItemStack getDisplayItem() {
        return ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt().getCompoundOrEmpty("shared_data").getCompoundOrEmpty("display_item"))
            .result().orElse(ItemStack.EMPTY);
    }

    private final VaultClientData clientData = new VaultClientData();

    public VaultConfig getConfig() {
        CompoundTag tag = nbt().getCompoundOrEmpty("config");
        VaultConfig base = VaultConfig.DEFAULT;
        if (tag.isEmpty()) {
            return base;
        }
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> table = tag.contains("loot_table")
            ? lootKey(tag.getStringOr("loot_table", "")) : base.lootTable();
        java.util.Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> display =
            tag.contains("override_loot_table_to_display") ? java.util.Optional.of(lootKey(tag.getStringOr("override_loot_table_to_display", "")))
                : java.util.Optional.empty();
        ItemStack key = tag.contains("key_item") ? ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.getCompoundOrEmpty("key_item"))
            .result().orElse(base.keyItem()) : base.keyItem();
        return new VaultConfig(table, tag.getDoubleOr("activation_range", base.activationRange()),
            tag.getDoubleOr("deactivation_range", base.deactivationRange()), key, display);
    }

    public void setConfig(VaultConfig config) {
        CompoundTag tag = new CompoundTag();
        tag.putString("loot_table", config.lootTable().identifier().toString());
        tag.putDouble("activation_range", config.activationRange());
        tag.putDouble("deactivation_range", config.deactivationRange());
        ItemStack.OPTIONAL_CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, config.keyItem()).result().ifPresent(key -> tag.put("key_item", key));
        config.overrideLootTableToDisplay().ifPresent(display -> tag.putString("override_loot_table_to_display", display.identifier().toString()));
        CompoundTag all = nbt();
        all.put("config", tag);
        load(all);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootKey(String id) {
        return (net.minecraft.resources.ResourceKey) net.minecraft.resources.ResourceKey.create(
            (net.minecraft.resources.ResourceKey) net.minecraft.core.registries.Registries.LOOT_TABLE, net.minecraft.resources.Identifier.parse(id));
    }

    public VaultServerData getServerData() {
        if (!(getLevel() instanceof ServerLevel)) {
            return null;
        }
        CompoundTag tag = nbt().getCompoundOrEmpty("server_data");
        java.util.List<ItemStack> eject = new java.util.ArrayList<>();
        for (net.minecraft.nbt.Tag entry : tag.getListOrEmpty("items_to_eject")) {
            if (entry instanceof CompoundTag stack) {
                ItemStack.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, stack).result().ifPresent(eject::add);
            }
        }
        return new VaultServerData(uuids(tag.getListOrEmpty("rewarded_players")), tag.getLongOr("state_updating_resumes_at", 0L), eject,
            tag.getIntOr("total_ejections_needed", 0));
    }

    public VaultSharedData getSharedData() {
        CompoundTag tag = nbt().getCompoundOrEmpty("shared_data");
        return new VaultSharedData(getDisplayItem(), uuids(tag.getListOrEmpty("connected_players")),
            tag.getDoubleOr("connected_particles_range", VaultConfig.DEFAULT.deactivationRange()));
    }

    public VaultClientData getClientData() {
        return clientData;
    }

    private static java.util.Set<java.util.UUID> uuids(net.minecraft.nbt.ListTag list) {
        java.util.Set<java.util.UUID> players = new java.util.LinkedHashSet<>();
        for (net.minecraft.nbt.Tag entry : list) {
            if (entry instanceof net.minecraft.nbt.IntArrayTag array && array.getAsIntArray().length == 4) {
                int[] words = array.getAsIntArray();
                players.add(new java.util.UUID((long) words[0] << 32 | (words[1] & 0xFFFFFFFFL), (long) words[2] << 32 | (words[3] & 0xFFFFFFFFL)));
            }
        }
        return players;
    }

    public java.util.Set<java.util.UUID> getRewardedPlayers() {
        return uuids(nbt().getCompoundOrEmpty("server_data").getListOrEmpty("rewarded_players"));
    }

    public boolean hasRewarded(java.util.UUID player) {
        return getRewardedPlayers().contains(player);
    }

    public boolean isOminous() {
        return getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.OMINOUS);
    }
}
