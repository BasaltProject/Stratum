// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SingleItemRecipe;

public class SingleItemRecipeBuilder extends JsonRecipeBuilder implements RecipeBuilder {
    private final Ingredient ingredient;
    private final ItemLike result;
    private final int count;

    private SingleItemRecipeBuilder(RecipeCategory category, Ingredient ingredient, ItemLike result, int count) {
        super(category);
        this.ingredient = ingredient;
        this.result = result;
        this.count = count;
    }

    public static SingleItemRecipeBuilder stonecutting(Ingredient ingredient, RecipeCategory category, ItemLike result, int count) {
        return new SingleItemRecipeBuilder(category, ingredient, result, count);
    }

    @Override
    public SingleItemRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public SingleItemRecipeBuilder group(String group) {
        this.groupName = group;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.recipeKey(RecipeJson.itemId(result));
    }

    @Override
    protected String type() {
        return "stonecutting";
    }

    @Override
    protected void fill(JsonObject json) {
        putGroup(json);
        json.add("ingredient", RecipeJson.ingredient(ingredient));
        json.add("result", RecipeJson.result(result, count));
    }
    private SingleItemRecipeBuilder(RecipeCategory category, SingleItemRecipe.Factory<?> factory, Ingredient ingredient, ItemStackTemplate result) {
        super((net.minecraft.data.recipes.RecipeCategory) null);
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.data.recipes.SingleItemRecipeBuilder#<init>");
    }
    public SingleItemRecipeBuilder(RecipeCategory category, SingleItemRecipe.Factory<?> factory, Ingredient ingredient, ItemLike result, int count) {
        super((net.minecraft.data.recipes.RecipeCategory) null);
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.data.recipes.SingleItemRecipeBuilder#<init>");
    }
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        super.save(output, id);
    }
}
