// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixer;
import org.basaltmc.stratum.bukkit.facade.ServerAccess;
import com.mojang.datafixers.DSL;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.filefix.FileFixerUpper;

public class DataFixers {
    private DataFixers() {}

    public static DataFixer getDataFixer() {
        return ServerAccess.dataFixer();
    }
    public static final int BLENDING_VERSION = 4997;
    public static FileFixerUpper getFileFixer() {
        return new FileFixerUpper(null, java.util.List.of(), org.basaltmc.stratum.bukkit.WorldNatives.dataVersion());
    }
    public static CompletableFuture<?> optimize(Set<DSL.TypeReference> typesToOptimize) {
        return CompletableFuture.completedFuture(null);
    }
}
