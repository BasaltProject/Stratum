// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.datafix.fixes;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
public class MapColorComponentRemovalFix extends DataComponentRemainderFix {
    public MapColorComponentRemovalFix(Schema outputSchema) {
        super(outputSchema, "MapColorComponentRemovalFix", "minecraft:map_color");
    }
    protected <T> Dynamic<T> fixComponent(Dynamic<T> input) {
        return org.basaltmc.stratum.bukkit.facade.NativeStep.dynamic("item_stack", getClass().getSimpleName(), getVersionKey(), input);
    }
}
