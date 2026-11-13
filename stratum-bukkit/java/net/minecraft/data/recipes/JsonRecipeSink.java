// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

public interface JsonRecipeSink {
    void acceptJson(Identifier id, JsonObject recipe);
}
