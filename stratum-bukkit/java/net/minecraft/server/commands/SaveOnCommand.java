// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
public class SaveOnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "save-on");
    }
}
