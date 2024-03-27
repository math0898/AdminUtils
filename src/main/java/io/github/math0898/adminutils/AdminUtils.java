package io.github.math0898.adminutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class AdminUtils extends JavaPlugin {

    /**
     * This points to the plugin object. It allows for registering of event handlers and scheduling of events.
     */
    public static JavaPlugin plugin;

    /**
     * Prints the given message to the console. This method will print the message with the default color gray and at
     * information debug level.
     *
     * @param message The message to print to the console.
     */
    public static void console (String message) {
        console(message, ChatColor.GRAY);
    }

    /**
     * Prints the given message to the console using the given color at the information level.
     *
     * @param message The message to print to the console.
     * @param color The starting color for the message.
     */
    public static void console (String message, ChatColor color) {
        console(color + message, Level.INFO);
    }

    /**
     * Prints the given message to the console at the given level. The color is automatically determined using the
     * logger level. Adding a color to the message string will override this default color selection.
     *
     * @param message The message to print to the console.
     * @param level The level to send the message at.
     */
    public static void console (String message, Level level) {
        if (level == Level.WARNING) Bukkit.getLogger().log(level, ChatColor.YELLOW + message);
        else if (level == Level.SEVERE) Bukkit.getLogger().log(level, ChatColor.RED + message);
        else Bukkit.getLogger().log(level, message);
    }

    /**
     * Sends a message if and only if the plugin is currently in debug mode. This is determined by passing the boolean
     * used to flag debugging.
     *
     * @param message The message to print to the console.
     * @param debug Whether the plugin is flagged as debugging.
     */
    public static void console (String message, boolean debug) {
        if (debug) console("[Debug] " + message, Level.WARNING);
    }

    @Override
    public void onEnable() {
        plugin = this;
        console("Loading Sugaku's AdminUtils Plugin...");
        CommandManager.setup();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(UpdateManager.getInstance(), this);
        InventoryManager.init();
        AnnouncementManager.init();
        console("AdminUtils loaded successfully!", ChatColor.GREEN);
    }

    @Override
    public void onDisable() {
        console("Tearing down...");

        console("Tear down successful!", ChatColor.GREEN);
    }
}
