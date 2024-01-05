package ir.Mehran1022.EventCore.Managers;

import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Utils.Common;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;

@UtilityClass
public class ConfigManager {

    public String PREFIX, ALREADY_STARTED, NO_PERMISSION, NO_DESC, END, NO_EVENT, SERVER_NAME;
    public Integer DURATION, FADEIN, STAY, FADEOUT;
    public String BOSSBAR, TITLE, SUBTITLE;
    public boolean ENABLE_COST, AUTOUPDATE, CHECKUPDATE, DEBUG;
    public Double COST;
    public String OFFLINE, BLOCK, BLOCKED, UNBLOCK, CONFIRMATION;

    /**
     * Loads the configuration settings.
     */
    public void loadConfig() {
        // Reload the configuration
        Main.getInstance().reloadConfig();

        final FileConfiguration config = Main.getInstance().getConfig();

        // Log debug message if debug mode is enabled
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Loaded config.yml");
        }

        // Load the configuration values into variables
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
