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

    public static  Boolean ENABLE_COST;

    public static Double COST;

    public static String OFFLINE;

    public static String BLOCK;

    public static String BLOCKED;

    public static String UNBLOCK;

    public static Boolean AUTOUPDATE;

    public static Boolean CHECKUPDATE;

    public static void loadConfig() {
        Main instance = Main.getInstance();
        instance.reloadConfig();
        FileConfiguration config = instance.getConfig();

        PREFIX = Common.color(config.getString("Prefix") + " ");
        ALREADY_STARTED = Common.color(config.getString("Messages.AlreadyStarted"));
        NO_PERMISSION = Common.color(config.getString("Messages.NoPermission"));
        NO_DESC = Common.color(config.getString("Messages.NoDescription"));
        END = Common.color(config.getString("Messages.EventEnd"));
        NO_EVENT = Common.color(config.getString("Messages.NoActiveEvent"));
        SERVER_NAME = config.getString("EventServer");
        DURATION = config.getInt("Duration");
        BOSSBAR = Common.color(config.getString("Messages.Bossbar"));
        TITLE = Common.color(config.getString("Titles.Title"));
        SUBTITLE = Common.color(config.getString("Titles.Subtitle"));
        FADEIN = config.getInt("Titles.FadeIn");
        STAY = config.getInt("Titles.Stay");
        FADEOUT = config.getInt("Titles.FadeOut");
        ENABLE_COST = config.getBoolean("Cost.Enabled");
        COST = config.getDouble("Cost.Cost");
        OFFLINE = Common.color(config.getString("Messages.Offline"));
        BLOCK = Common.color(config.getString("Messages.Block"));
        UNBLOCK = Common.color(config.getString("Messages.Unblock"));
        BLOCKED = Common.color(config.getString("Messages.Blocked"));
        AUTOUPDATE = config.getBoolean("Auto-Update");
        CHECKUPDATE = config.getBoolean("Check-Update");

    }
}
