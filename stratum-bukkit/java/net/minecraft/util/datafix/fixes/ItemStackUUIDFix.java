// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.datafix.fixes;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
public class ItemStackUUIDFix extends AbstractUUIDFix {
    public ItemStackUUIDFix(Schema outputSchema) {
        super(outputSchema, net.minecraft.util.datafix.fixes.References.ITEM_STACK);
    }
    public TypeRewriteRule makeRule() {
        return org.basaltmc.stratum.bukkit.facade.NativeStepRule.of(getClass().getSimpleName(), getVersionKey());
    }
}
