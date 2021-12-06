package io.github.math0898.adminutils;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Date;

import static io.github.math0898.adminutils.AdminUtils.plugin;

public class AnnouncementManager {

    /**
     * The current 'count'. This is the number of minutes the server has been up basically.
     */
    private static int count = 0;

    /**
     * The announcements currently loaded from disc.
     */
    private static final ArrayList<Announcement> announcements = new ArrayList<>();

    /**
     * This inner record describes what is an Announcement.
     *
     * @author Sugaku
     */
    protected record Announcement (String message, Date date, int frequency, int offset) {

        /**
         * Sends this announcement to all currently online players. Whenever message contains %date% it will be replaced
         * with {@link #date} in UTC.
         */
        public void send () {
            Bukkit.getOnlinePlayers().forEach((p) -> p.sendMessage(message.replace("%date%", date.toString())));
            // todo might need tweaking.
        }
    }

    /**
     * Initializes the AnnouncementManager so that everything is ready to run.
     */
    public static void init () {
        // todo implement loading of announcements.
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, AnnouncementManager::checkAnnouncements, 20 * 60);
    }

    /**
     * Checks if any Announcements should be sent and sends them if that's the case.
     */
    public static void checkAnnouncements () {
        count++;
        announcements.forEach((a) -> { if (a.frequency % (count - a.offset) == 0) a.send(); });
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, AnnouncementManager::checkAnnouncements, 20 * 60);
    }
}
