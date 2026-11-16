// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.advancements;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import org.basaltmc.stratum.bukkit.facade.ReflectiveCodecs;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
public record AdvancementRewards(int experience, HolderSet<LootTable> loot, List<ResourceKey<Recipe<?>>> recipes, Optional<CacheableFunction> function) {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Codec<List<ResourceKey<Recipe<?>>>> kRecipes = Identifier.CODEC.listOf()
        .xmap(ids -> (List) ids.stream().map(id -> ResourceKey.create((ResourceKey) Registries.RECIPE, id)).toList(),
            keys -> ((List<ResourceKey<?>>) (List) keys).stream().map(ResourceKey::identifier).toList());
    public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("experience", 0).forGetter(AdvancementRewards::experience),
        ReflectiveCodecs.holderSet(LootTable.class).optionalFieldOf("loot", HolderSet.empty()).forGetter(AdvancementRewards::loot),
        kRecipes.optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes),
        CacheableFunction.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)).apply(i, AdvancementRewards::new));
    public static final AdvancementRewards EMPTY = new AdvancementRewards(0, HolderSet.empty(), List.of(), Optional.empty());
    public static class Builder {
        private int experience;
        private Optional<Identifier> function = Optional.empty();
        public static Builder experience(int amount) {
            return new Builder().addExperience(amount);
        }
        public Builder addExperience(int amount) {
            experience += amount;
            return this;
        }
        public static Builder function(Identifier id) {
            return new Builder().runs(id);
        }
        public Builder runs(Identifier function) {
            this.function = Optional.of(function);
            return this;
        }
        public AdvancementRewards build() {
            return new AdvancementRewards(experience, HolderSet.empty(), List.of(), function.map(CacheableFunction::new));
        }
            public static Builder loot(Holder<LootTable> id) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.advancements.AdvancementRewards.Builder#loot");
        }
        public Builder addLootTable(Holder<LootTable> id) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.advancements.AdvancementRewards.Builder#addLootTable");
        }
        public static Builder recipe(ResourceKey<Recipe<?>> id) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.advancements.AdvancementRewards.Builder#recipe");
        }
        public Builder addRecipe(ResourceKey<Recipe<?>> id) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.advancements.AdvancementRewards.Builder#addRecipe");
        }
}
    public void grant(ServerPlayer player) {
    }
}
