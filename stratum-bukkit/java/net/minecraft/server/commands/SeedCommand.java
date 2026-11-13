// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
public class SeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean checkPermissions) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "seed");
    }
}
