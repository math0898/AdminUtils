package sugaku.adminutils;

import org.bukkit.ChatColor;
import sugaku.adminutils.commands.AdminUtils;

import java.util.Objects;
import static sugaku.adminutils.main.console;

public class CommandManager {

    /**
     * Sets up commands for use. Called on enabled.
     */
    public static void setup(main m){
        console("Setting up command executors.");
        Objects.requireNonNull(m.getCommand("adminutils")).setExecutor(new AdminUtils("adminutils"));
        console("Command executors setup.", ChatColor.GREEN);

        console("Setting up tab completion,");
        Objects.requireNonNull(m.getCommand("adminutils")).setTabCompleter(new AdminUtils("adminutils").autocomplete);
        console("Tab completion setup.", ChatColor.GREEN);
    }
}
