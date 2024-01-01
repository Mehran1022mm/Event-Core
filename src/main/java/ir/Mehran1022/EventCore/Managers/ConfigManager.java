package ir.mehran1022.eventcore.Managers;

import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.Utils.Common;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigManager {

    public static String PREFIX;

    public static String ALREADY_STARTED;

    public static String NO_PERMISSION;

    public static String NO_DESC;

    public static String END;

    public static String NO_EVENT;

    public static String SERVER_NAME;

    public static Integer DURATION;

    public static String BOSSBAR;

    public static String TITLE;

    public static String SUBTITLE;

    public static Integer FADEIN;

    public static Integer STAY;

    public static Integer FADEOUT;

    public static  boolean ENABLE_COST;

    public static Double COST;

    public static String OFFLINE;

    public static String BLOCK;

    public static String BLOCKED;

    public static String UNBLOCK;

    public static boolean AUTOUPDATE;

    public static boolean CHECKUPDATE;

    public static boolean DEBUG;

    public static String CONFIRMATION;

    public static void loadConfig() {
        Main instance = Main.getInstance();
        instance.reloadConfig();
        FileConfiguration config = instance.getConfig();

        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Loaded config.yml");
        }

        PREFIX = Common.color(config.getString("prefix") + " ");
        ALREADY_STARTED = Common.color(config.getString("messages.alreadyStarted"));
        NO_PERMISSION = Common.color(config.getString("messages.noPermission"));
        NO_DESC = Common.color(config.getString("messages.noDescription"));
        END = Common.color(config.getString("messages.eventEnd"));
        NO_EVENT = Common.color(config.getString("messages.noActiveEvent"));
        SERVER_NAME = config.getString("event-server");
        DURATION = config.getInt("duration");
        BOSSBAR = Common.color(config.getString("messages.bossbar"));
        TITLE = Common.color(config.getString("titles.title"));
        SUBTITLE = Common.color(config.getString("titles.subtitle"));
        FADEIN = config.getInt("titles.fade-in");
        STAY = config.getInt("titles.stay");
        FADEOUT = config.getInt("titles.fade-out");
        ENABLE_COST = config.getBoolean("cost.enabled");
        COST = config.getDouble("cost.cost");
        OFFLINE = Common.color(config.getString("messages.offline"));
        BLOCK = Common.color(config.getString("messages.block"));
        UNBLOCK = Common.color(config.getString("messages.unblock"));
        BLOCKED = Common.color(config.getString("messages.blocked"));
        CHECKUPDATE = config.getBoolean("update-check");
        DEBUG = config.getBoolean("debug-mode");
        CONFIRMATION = config.getString("messages.confirmation");

    }
}
