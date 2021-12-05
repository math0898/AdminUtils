package sugaku.adminutils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sugaku.adminutils.commands.AbstractCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static sugaku.adminutils.AdminUtils.console;
import static sugaku.adminutils.AdminUtils.plugin;

public class InventoryManager {

    /**
     * Makes sure the correct folder structure is setup to save and load inventories for the rest of this class to
     * function properly.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init() {
        console("Checking for required directories.");

        try {
            File container = new File("./plugins/AdminUtils/");
            if (!Files.exists(Paths.get(container.getPath()))) container.mkdir();
            File inventories = new File("./plugins/AdminUtils/Inventories");
            if (!Files.exists(Paths.get(inventories.getPath()))) inventories.mkdir();
            console("Directories created.", ChatColor.GREEN);
        } catch (Exception exception) {
            console("Could not create directories.", ChatColor.RED);
            console(exception.getMessage());
        }
    }

    /**
     * Saves the player's inventory that is given. Also writes it to a file so crashes and other server issues are less
     * likely to cause issues with the inventory save.
     *
     * @param p The player who's inventory is being saved.
     */
    public static void saveInventory(Player p) { saveInventory(p, "inv"); }

    /**
     * Saves the player's inventory that is given. Also writes it to a file so crashes and other server issues are less
     * likely to cause issues with the inventory save.
     *
     * @param p The player who's inventory is being saved.
     * @param extension The extension to place on the file.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveInventory(Player p, String extension) {
        if (p == null) return;
        Inventory inventory = p.getInventory();
        EntityEquipment equip = p.getEquipment();
        ItemStack[] items = inventory.getStorageContents();
        ItemStack[] equipment = null;
        if (equip != null) equipment = new ItemStack[]{equip.getHelmet(), equip.getChestplate(), equip.getLeggings(), equip.getBoots(), equip.getItemInOffHand()};
        File file = new File("./plugins/AdminUtils/Inventories/" + p.getUniqueId() + "." + extension);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n");
            for (ItemStack i: items) {
                if (i == null) continue;
                writer.write(serialize(i));
            }
            if (equipment != null) for (ItemStack i: equipment) {
                if (i == null) continue;
                writer.write(serialize(i));
            }
            writer.close();
        } catch(IOException ignored) { }
    }

    /**
     * Shows the player inventory to the user. More of a development command or one to confirm that saving an inventory
     * was successful rather than day to day server functionality.
     * @param p the player who's inventory is being viewed.
     * @param user The player who will see the opened inventory.
     */
    public static void viewInventory (Player p, Player user) {
        String invName = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AdminUtils" + ChatColor.DARK_GRAY + "] "+ ChatColor.GRAY + p.getName();
        Inventory inventory = plugin.getServer().createInventory(user, 54, invName);

        File file = new File("./plugins/AdminUtils/Inventories/" + p.getUniqueId() + ".inv");
        try {
            Scanner scanner = new Scanner(file);
            AbstractCommand.send(user, "Inventory save timestamp: " + scanner.nextLine());
            while (scanner.hasNextLine()) inventory.addItem(deserialize(scanner.nextLine()));
            scanner.close();
        } catch (IOException ignored) { }

        user.openInventory(inventory);
    }

    /**
     * Turns the item into a string which can then be converted back into an item using deserialize.
     * @param item The item to be converted.
     * @return The serialized string.
     */
    private static String serialize(ItemStack item) {

        String r = "";

        r += "<type> " + item.getType() + " </type>";
        r += " <amt> " + item.getAmount() + " </amt>";
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return r + " <done> \n";
        if (meta.hasDisplayName()) r += " <name> " + meta.getDisplayName() + " </name>";
        if (meta.hasEnchants()) {
            r += " <enchants>";
            Set<Enchantment> en = meta.getEnchants().keySet();
            for (Enchantment e: en) r += " " + e.getName() + " " + meta.getEnchantLevel(e);
            r += " </enchants>";
        }
        if (meta.hasLore()) {
            assert meta.getLore() != null;
            r += " <lore>";
            for (String l: meta.getLore()) r += " " +  l + " <br>";
            r += " </lore>";
        }
        if (meta.isUnbreakable()) r += " <unbreakable>";
        if (meta.hasAttributeModifiers()) {
            r += " <attributes>";
            Set<Attribute> attributes = meta.getAttributeModifiers().keySet();
            for (Attribute a: attributes) {
                r += " " + a.toString();
                for (AttributeModifier m: meta.getAttributeModifiers(a)) r += " <mod> " + m.getName() + " " + m.getUniqueId() + " " + m.getOperation() +" " + m.getAmount() + " " + m.getSlot() + " </mod>"; //TODO
            }
            r += " </attributes>";
        }
        Set<ItemFlag> flags = meta.getItemFlags();
        if (!flags.isEmpty()) {
            // TODO
        }

        r += " <done> \n";

        return r;
    }

    /**
     * Turns the given string into an ItemStack. Uses a couple of helper methods for the deciphering of enchantments and
     * attributes.
     * @param cipher The string to be deciphered.
     * @return The item described by the given string.
     */
    private static ItemStack deserialize (String cipher) {
        Scanner scanner = new Scanner(cipher);
        Material m = null;
        String a = "-1";
        String n = null;
        boolean b = false;
        ArrayList<Enchantment> enchants = new ArrayList<>();
        ArrayList<Integer> lvl = new ArrayList<>();
        ItemStack item;
        while (scanner.hasNext()) {
            String read = scanner.next();
            if (read.equals("<type>")) m = Material.matchMaterial(scanner.next());
            else if (read.equals("<amt>")) a = scanner.next();
            else if (read.equals("<name>")) {
                read = scanner.next();
                n = read;
                read = scanner.next();
                while (!read.equals("</name>")){
                    n += " " + read;
                    read = scanner.next();
                }
            }
            else if (read.equals("<enchants>")) {
                read = scanner.next();
                while (!read.equals("</enchants>")) {
                    enchants.add(Enchantment.getByKey(NamespacedKey.minecraft(read)));
                    lvl.add(Integer.parseInt(scanner.next()));
                    console("Read: " + read, true);
                    console("Lvl:" + lvl.get(lvl.size() - 1),true);
                }
                continue;
            }
            else if (read.equals("<unbreakable>")) { b = true; continue; }
            else if (read.equals("<done>")) break;
            scanner.next();
        }
        assert m != null;

        item = new ItemStack(m, Integer.parseInt(a));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (n != null) meta.setDisplayName(n);
        meta.setUnbreakable(b);
        for (int i = 0; i < enchants.size(); i++) meta.addEnchant(enchants.get(i), lvl.get(i), true);
        item.setItemMeta(meta);

        return item;
    }
}
