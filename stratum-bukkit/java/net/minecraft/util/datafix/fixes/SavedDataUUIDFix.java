// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.datafix.fixes;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
public class SavedDataUUIDFix extends AbstractUUIDFix {
    public SavedDataUUIDFix(Schema outputSchema) {
        super(outputSchema, net.minecraft.util.datafix.fixes.References.SAVED_DATA_RAIDS);
    }
    protected TypeRewriteRule makeRule() {
        return org.basaltmc.stratum.bukkit.facade.NativeStepRule.of(getClass().getSimpleName(), getVersionKey());
    }
}
