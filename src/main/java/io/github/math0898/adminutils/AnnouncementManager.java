package io.github.math0898.adminutils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

import static io.github.math0898.adminutils.AdminUtils.console;
import static io.github.math0898.adminutils.AdminUtils.plugin;

/**
 * The AnnouncementManager class handles announcements. Announcements are declared in announcements.yml. Every minute
 * the AnnouncementManager makes a check if any announcements should be sent and sends them if that is the case.
 *
 * @author Sugaku
 */
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
    protected record Announcement (String message, /* Date date, */ int frequency, int offset) {

        /**
         * Sends this announcement to all currently online players. Whenever message contains %date% it will be replaced
         * with {@link #date} in UTC.
         */
        public void send () {
            Bukkit.getOnlinePlayers().forEach((p) -> p.sendMessage(message));
            // todo gotta consider dates at some point.
        }

        /**
         * Creates a new announcement from the given configuration section.
         *
         * @param section The configuration section that should be used to create this announcement.
         */
        public static Announcement create (ConfigurationSection section) {
            if (section == null) return null;
            String m = section.getString("message", "Message not defined").replace("&", "§");
            int frequency = section.getInt("frequency", 60);
            int offset = section.getInt("offset", new Random().nextInt() % frequency);
            return new Announcement(m, frequency, offset);
        }
    }

    /**
     * Initializes the AnnouncementManager so that everything is ready to run.
     */
    public static void init () {
        YamlConfiguration save = new YamlConfiguration();
        File container = new File("./plugins/AdminUtils");
        if (!container.exists()) //noinspection ResultOfMethodCallIgnored
            container.mkdir();
        File file = new File("./plugins/AdminUtils/announcements.yml");
        if (!file.exists()) plugin.saveResource("announcements.yml", false);
        try {
            save.load(file);
        } catch (Exception exception) {
            console(exception.getMessage(), Level.SEVERE);
            for (StackTraceElement s : exception.getStackTrace()) console(s.toString(), Level.SEVERE);
            return;
        }
        save.getKeys(false).forEach((s) -> announcements.add(Announcement.create(save.getConfigurationSection(s))));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, AnnouncementManager::checkAnnouncements, 20 * 60);
    }

    /**
     * Checks if any Announcements should be sent and sends them if that's the case.
     */
    public static void checkAnnouncements () {
        count++;
        announcements.forEach((a) -> { if ((count - a.offset) % a.frequency == 0) a.send(); });
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, AnnouncementManager::checkAnnouncements, 20 * 60);
    }
}
