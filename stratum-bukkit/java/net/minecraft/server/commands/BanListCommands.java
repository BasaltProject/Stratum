// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
public class BanListCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "banlist");
    }
}
