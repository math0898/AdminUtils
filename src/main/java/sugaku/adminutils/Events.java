package sugaku.adminutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

public class Events implements Listener {

    /**
     * Handles the situation when an entity takes damage from an entity. Used to cancel end crystal damage on players.
     *
     * @param event The entity damaged by entity event. Filtered first by victims being players then by attackers
     *              being crystals.
     */
    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) if (event.getDamager().getType() == EntityType.ENDER_CRYSTAL) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    /**
     * Handles the situation when a player interacts. First checks the held item of the player for a lava bucket or
     * flint and steel at which point it checks for more than one nearby player. Canceling the event if one is found.
     *
     * @param event The player interact event. Filtered by click, item, and then special conditions.
     */
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem().getType() == Material.FLINT_AND_STEEL || event.getItem().getType() == Material.LAVA_BUCKET) {
                assert event.getClickedBlock() != null;
                Location l = event.getClickedBlock().getLocation();
                if (event.getClickedBlock().getWorld().getNearbyEntities(new BoundingBox(l.getX() - 5, l.getY() - 5, l.getZ() - 5, l.getX() + 5, l.getY() + 5, l.getZ() + 5), entity -> entity instanceof Player).size() >= 2) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AdminUtils"+ ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "There is another player nearby!");
                }
            }
        }
    }

    /**
     * Called when a player joins the server. Checks if their inventory has been saved yet today and sends a promotional
     * or thank you message if it hasn't been.
     *
     * @param event The player login event.
     */
    @EventHandler
    public void onPlayerLogin (PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(AdminUtils.plugin, () -> handleOnPlayerLogin(event) , 10*20);
    }

    /**
     * Handles the actual event after a short delay.
     *
     * @param event The player login event.
     */
    public void handleOnPlayerLogin (PlayerLoginEvent event) {
        LocalDateTime time = LocalDateTime.now();
        UUID uuid = event.getPlayer().getUniqueId();
        File f = new File("./plugins/AdminUtils/Inventories/" + uuid + "." + time.getMonthValue() + "-" + time.getDayOfMonth() + "-" + time.getYear());
        AdminUtils.console(ChatColor.GRAY + "Checking for: " + f.getPath());
        if (f.exists()) { AdminUtils.console(ChatColor.GREEN + "File found."); return; }
        AdminUtils.console(ChatColor.GOLD + "Backing up inventory of " + event.getPlayer().getName());
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        InventoryManager.saveInventory(player, "" + time.getMonthValue() + "-" + time.getDayOfMonth() + "-" + time.getYear());
    }
}
