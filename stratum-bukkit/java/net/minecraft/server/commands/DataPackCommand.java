// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.packs.repository.Pack;
public class DataPackCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "datapack");
    }
    interface Inserter {
        void apply(List<Pack> selected, Pack pack) throws CommandSyntaxException;
    }
}
