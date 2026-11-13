// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.commands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
public class AttributeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        org.basaltmc.stratum.bukkit.facade.NativeCommands.delegate(dispatcher, "attribute");
    }
}
