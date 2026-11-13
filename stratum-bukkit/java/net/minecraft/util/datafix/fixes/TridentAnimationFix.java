// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.datafix.fixes;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
public class TridentAnimationFix extends DataComponentRemainderFix {
    public TridentAnimationFix(Schema outputSchema) {
        super(outputSchema, "TridentAnimationFix", "minecraft:consumable");
    }
    protected <T> Dynamic<T> fixComponent(Dynamic<T> input) {
        return org.basaltmc.stratum.bukkit.facade.NativeStep.dynamic("item_stack", getClass().getSimpleName(), getVersionKey(), input);
    }
}
