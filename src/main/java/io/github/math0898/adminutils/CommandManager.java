package io.github.math0898.adminutils;

import io.github.math0898.adminutils.commands.AdminUtils;
import org.bukkit.ChatColor;
import java.util.Objects;

import static io.github.math0898.adminutils.AdminUtils.console;
import static io.github.math0898.adminutils.AdminUtils.plugin;

public class CommandManager {

    /**
     * Sets up commands for use. Called on enabled.
     */
    public static void setup () {
        console("Setting up command executors.");
        Objects.requireNonNull(plugin.getCommand("adminutils")).setExecutor(new AdminUtils("adminutils"));
        console("Command executors setup.", ChatColor.GREEN);

        console("Setting up tab completion,");
        Objects.requireNonNull(plugin.getCommand("adminutils")).setTabCompleter(new AdminUtils("adminutils").autocomplete);
        console("Tab completion setup.", ChatColor.GREEN);
    }
}
