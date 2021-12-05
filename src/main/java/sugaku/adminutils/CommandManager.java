package sugaku.adminutils;

import org.bukkit.ChatColor;
import java.util.Objects;

import static sugaku.adminutils.AdminUtils.console;
import static sugaku.adminutils.AdminUtils.plugin;

public class CommandManager {

    /**
     * Sets up commands for use. Called on enabled.
     */
    public static void setup () {
        console("Setting up command executors.");
        Objects.requireNonNull(plugin.getCommand("adminutils")).setExecutor(new sugaku.adminutils.commands.AdminUtils("adminutils"));
        console("Command executors setup.", ChatColor.GREEN);

        console("Setting up tab completion,");
        Objects.requireNonNull(plugin.getCommand("adminutils")).setTabCompleter(new sugaku.adminutils.commands.AdminUtils("adminutils").autocomplete);
        console("Tab completion setup.", ChatColor.GREEN);
    }
}
