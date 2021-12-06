package io.github.math0898.adminutils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.PluginsCommand;

public abstract class AbstractCommand extends PluginsCommand {

    /**
     * The prefix sent with every message.
     */
    private static final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AdminUtils" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

    public AbstractCommand(String name) { super(name); }

    /**
     * Sends a message to the given recipient.
     */
    public static void send(CommandSender user, String message) {
        user.sendMessage(prefix + message);
    }
}
