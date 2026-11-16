package org.bukkit.craftbukkit.command;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/** The class plugins reach for to turn a Bukkit sender into a vanilla command source. */
public final class VanillaCommandWrapper extends BukkitCommand {
    public VanillaCommandWrapper(String name) {
        super(name);
    }

    public static CommandSourceStack getListener(CommandSender sender) {
        if (sender instanceof CraftPlayer player) {
            return player.getHandle().createCommandSourceStack();
        }
        return DedicatedServer.instance().createCommandSourceStack();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return List.of();
    }
}
