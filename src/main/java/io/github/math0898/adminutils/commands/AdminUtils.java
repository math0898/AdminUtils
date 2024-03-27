package io.github.math0898.adminutils.commands;

import io.github.math0898.adminutils.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import io.github.math0898.adminutils.InventoryManager;

import java.util.*;

public class AdminUtils extends AbstractCommand implements CommandExecutor {

    /**
     * Tab completer which fills in options for this command.
     */
    public TabCompleter autocomplete = new AutocompleteAdminUtils();

    public AdminUtils(String name) { super(name); }

    /**
     * Code to be executed on run.
     * @return Whether the command executed successfully or not
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) return true;
        else if (args[0].equalsIgnoreCase("update")) updateSubcommand(sender);
        else if (args[0].equalsIgnoreCase("redtext")) redTextSubcommand(sender, args);
        else if (args[0].equalsIgnoreCase("inventory")) inventorySubcommand(sender, args);
        else if (args[0].equalsIgnoreCase("thanks")) thanksSubcommand(sender, args);

        return true;
    }

    private void updateSubcommand(CommandSender sender) {
        UpdateManager.getInstance().update();
        send(sender, "You are restarting the server for an update.");
    }

    private void redTextSubcommand(CommandSender sender, String[] args) {

    }

    private void inventorySubcommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            send(sender, "Please specify a player!");
            return;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            send(sender, "That player cannot be found.");
            return;
        }

        if (args[1].equalsIgnoreCase("save")) {
            InventoryManager.saveInventory(player);
            send(sender, player.getName() + "'s inventory has been saved.");
        }
        else if (args[1].equalsIgnoreCase("load")) {
            //TODO
        } else if (args[1].equalsIgnoreCase("view")) {
            InventoryManager.viewInventory(player, (Player) sender);
            send(sender, "This is " + player.getName() + "'s saved inventory.");
        }
    }

    private void thanksSubcommand(CommandSender sender, String[] args) {
        Objects.requireNonNull(Bukkit.getPlayer(args[1])).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Thank you for buying a rank! Your perks have arrived!");
    }
}

class AutocompleteAdminUtils implements TabCompleter {
    /**
     * Tab completion of the command described above.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("AdminUtils")) {
            ArrayList<String> list = new ArrayList<>();
            String[] options = null;
            if (args.length == 1) options = new String[]{"inventory", "redText", "update"};
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("RedText")) options = new String[]{"set", "view"};
                else if (args[0].equalsIgnoreCase("Inventory")) options = new String[]{"load", "save", "view"};
            }

            if (options == null) return null;

            if (!args[args.length - 1].equals("")) { for (String o : options) if (o.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) list.add(o); }
            else list.addAll(Arrays.asList(options));

            return list;
        }
        return null;
    }
}
