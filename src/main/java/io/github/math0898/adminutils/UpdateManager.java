package io.github.math0898.adminutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.List;

import static io.github.math0898.adminutils.commands.AbstractCommand.send;

/**
 * The UpdateManager manages the Update process by sending messages, whitelisting the server, kicking players, etc.
 *
 * @author Sugaku
 */
public class UpdateManager implements Listener {

    /**
     * The active UpdateManager instance.
     */
    private static UpdateManager instance = null;

    /**
     * Whether the update whitelist is active or not.
     */
    private boolean whitelistActive = false;

    /**
     * Creates a new UpdateManager.
     */
    private UpdateManager () {

    }

    /**
     * An accessor method to the UpdateManager instance.
     *
     * @return The active UpdateManager instance.
     */
    public static UpdateManager getInstance () {
        if (instance == null) instance = new UpdateManager();
        return instance;
    }

    /**
     * Starts the server update process.
     */
    public void update () {
        Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 1400);
        Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kickall &8[&cAdminUtils&8] &7The server is updating. See you in a few!"), 1200);
        Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> Bukkit.setWhitelist(false), 1399);
        Bukkit.setWhitelist(true);
        whitelistActive = true;
        List<CommandSender> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        targets.add(Bukkit.getConsoleSender());
        //Register announcements
        for (CommandSender p : targets) {
            send(p, "The server will restart in 60 seconds for an update!");
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "The server will restart in 30 seconds!"), 600);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "The server will restart in 15 seconds!"), 900);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "The server will restart in 5 seconds!"), 1100);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "Restarting in 4..."), 1120);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "Restarting in 3..."), 1140);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "Restarting in 2..."), 1160);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "Restarting in 1..."), 1180);
            Bukkit.getScheduler().runTaskLater(io.github.math0898.adminutils.AdminUtils.plugin, () -> send(p, "Server is restarting. See you in a few!"), 1200);
        }
    }

    /**
     * Called whenever a player attempts to log onto the server. We use this to kick players with a custom whitelist
     * message.
     *
     * @param event The player login attempt.
     */
    @EventHandler
    public void onAsyncPlayerPreLogin (AsyncPlayerPreLoginEvent event){
        if(whitelistActive){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', """
                    &8[&cAdminUtils&8] &7The server is restarting.
                    &7It may take up to a minute to shutdown,
                    &7and another minute or two to restart.
                    &7
                    &7We'll be back in under 3 minutes!"""));
        }
    }
}
