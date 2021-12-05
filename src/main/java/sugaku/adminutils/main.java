package sugaku.adminutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {


    public static Plugin plugin;

    /**
     * Prints the given string into console with optional coloring.
     */
    public static void console(String message) { console(message, ChatColor.GRAY); }
    public static void console(String message, ChatColor color) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AdminUtils" + ChatColor.DARK_GRAY + "] " + color + message);
    }
    public static void console (String message, boolean debug) {
        if (debug) console("[" + ChatColor.YELLOW  + "Dev" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + message, ChatColor.DARK_GRAY);
        else console(message);
    }

    @Override
    public void onEnable() {
        //Loading
        console("Loading Sugaku's AdminUtils Plugin...");

        //Commands
        CommandManager.setup(this);

        //Registering events
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        //File manager setup
        InventoryManager.init();

        //Other
        plugin = this;

        //Loaded
        console("AdminUtils loaded successfully!", ChatColor.GREEN);
    }

    @Override
    public void onDisable() {
        console("Tearing down...");

        console("Tear down successful!", ChatColor.GREEN);
    }
}
