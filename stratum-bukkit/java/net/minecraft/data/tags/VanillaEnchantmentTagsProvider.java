// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

public class VanillaEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public VanillaEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        addNativeTags();
    }
}
