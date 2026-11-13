// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
public class GiveCommand {
    public static final int MAX_ALLOWED_ITEMSTACKS = 100;
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "give");
    }
}
