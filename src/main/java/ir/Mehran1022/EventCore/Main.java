package ir.Mehran1022.EventCore;


import ir.Mehran1022.EventCore.Commands.EventCommand;
import ir.Mehran1022.EventCore.Listeners.PlayerJoinEvent;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Managers.InventoryManager;
import ir.Mehran1022.EventCore.Managers.UpdateManager;
import ir.Mehran1022.EventCore.Utils.Common;
import lombok.val;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static Economy econ = null;
    public static boolean economyPluginFound = true;
    public static FileConfiguration playersData;
    public static File file;

    /**
     * Initializes the plugin when it is enabled.
     */
    @Override
    public void onEnable() {
        // Set the instance variable
        instance = this;

        // Save the default configuration file
        saveDefaultConfig();

        // Load the config from file
        ConfigManager.loadConfig();

        // Load player data
        loadPlayersData();

        // Check if the Vault plugin is present
        val vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");

        // If the Vault plugin is not found and economy plugin is not already found
        if (!economyPluginFound && vaultPlugin == null) {
            // Log an error message
            Common.log("&cCan't Use Cost Feature. Vault Not Found.");
        } else {
            // Log an error message
            Common.log("&cCan't Use Cost Feature. Failed To Get Economy Plugin.");

            // Disable the Cost feature in the config
            getConfig().set("Cost.Enabled", false);
        }

        // Load things
        loadThings();

        // Log a warning message about the disabled Bungeecord addon
        Common.log("&eBungeecord addon is disabled in this version because of major bugs. We recommend to remove it from your bungeecord server until we fix it!");
    }

    /**
     * Load things for the plugin.
     */
    private void loadThings() {
        // Register events
        Common.registerEvent(new InventoryManager(), this);
        Common.registerEvent(new PlayerJoinEvent(), this);
        Common.logDebug("[Debug] Registered events");

        // Register commands
        Common.registerCommand("event", new EventCommand());
        Common.logDebug("[Debug] Registered commands");

        // Register tab completer
        Common.registerTabCompleter(new EventCommand(), "event");
        Common.logDebug("[Debug] Registered tab completer");

        // Setup economy plugin
        economyPluginFound = setupEconomy();

        // Start update-checker worker if check update is enabled
        if (ConfigManager.CHECKUPDATE) {
            UpdateManager.start();
            Common.logDebug("[Debug] Started update-checker worker");
        }

        // Initialize metrics
        new Metrics(this, 18612);
    }

    @Override
    public void onDisable() {
        UpdateManager.stop();
        Common.logDebug("[Debug] Stopped update-check worker");
    }

    /**
     * Load the players' data from the playersData.yml file.
     */
    private void loadPlayersData() {
        // Define the file path for playersData.yml
        file = new File(getDataFolder(), "playersData.yml");

        // Check if the file exists, if not, create it
        if (!file.exists()) {
            saveResource("playersData.yml", false);
            Common.logDebug("[Debug] Created playersData.yml");
        }

        // Load the players' data from the YAML configuration file
        playersData = YamlConfiguration.loadConfiguration(file);

        // Log a debug message to indicate that the players' data has been assigned
        Common.logDebug("[Debug] Assigned players data");
    }

    /**
     * Checks if the economy system is properly set up.
     *
     * @return true if the economy system is set up, false otherwise.
     */
    private boolean setupEconomy() {
        // Check if Vault plugin is present
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Common.logDebug("[Debug] Vault not found");
            return false;
        }

        // Check if Economy provider is registered
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Common.logDebug("[Debug] Economy provider not found");
            return false;
        }

        // Set the econ variable to the Economy provider
        econ = rsp.getProvider();

        // Return true to indicate successful setup
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Main getInstance() {
        return instance;
    }
}