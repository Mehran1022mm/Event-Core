package ir.Mehran1022.EventCore;


import ir.Mehran1022.EventCore.Commands.EventCommand;
import ir.Mehran1022.EventCore.Listeners.PlayerJoinEvent;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Managers.InventoryManager;
import ir.Mehran1022.EventCore.Managers.UpdateManager;
import ir.Mehran1022.EventCore.Utils.Common;
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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigManager.loadConfig();
        loadPlayersData();
        if (!economyPluginFound) {
            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                Common.log("&cCan't Use Cost Feature. Vault Not Found.");
            } else {
                Common.log("&cCan't Use Cost Feature. Failed To Get Economy Plugin.");
                getConfig().set("Cost.Enabled", false);
            }
        }
        loadThings();
        Common.log("&eBungeecord addon is disabled in this version because of major bugs. We recommend to remove it from your bungeecord server until we fix it!");
    }

    private void loadThings() {
        Common.registerEvent(new InventoryManager(), this);
        Common.registerEvent(new PlayerJoinEvent(), this);
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Registered events");
        }
        Common.registerCommand("event", new EventCommand());
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Registered commands");
        }
        Common.registerTabCompleter(new EventCommand(), "event");
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Registered tab completer");
        }
        economyPluginFound = setupEconomy();
        if (ConfigManager.CHECKUPDATE) {
            UpdateManager.start();
            if (ConfigManager.DEBUG) {
                Common.log("[Debug] Started update-checker worker");
            }
        }
        new Metrics(this, 18612);
    }

    @Override
    public void onDisable() {
        UpdateManager.stop();
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Stopped update-check worker");
        }
    }

    private void loadPlayersData() {
        file = new File(getDataFolder(), "playersData.yml");
        if (!file.exists()) {
            saveResource("playersData.yml", false);
            if (ConfigManager.DEBUG) {
                Common.log("[Debug] Created playersData.yml");
            }
        }
        playersData = YamlConfiguration.loadConfiguration(file);
        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Assigned players data");
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            if (ConfigManager.DEBUG) {
                Common.log("[Debug] Vault not found");
            }
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            if (ConfigManager.DEBUG) {
                Common.log("[Debug] Economy provider not found");
            }
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Main getInstance() {
        return instance;
    }
}