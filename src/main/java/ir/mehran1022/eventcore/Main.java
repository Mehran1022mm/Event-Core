package ir.mehran1022.eventcore;

import ir.mehran1022.eventcore.command.EventCommand;
import ir.mehran1022.eventcore.listener.PlayerJoinEvent;
import ir.mehran1022.eventcore.manager.CommunicationManager;
import ir.mehran1022.eventcore.manager.ConfigManager;
import ir.mehran1022.eventcore.manager.InventoryManager;
import ir.mehran1022.eventcore.manager.UpdateManager;
import ir.mehran1022.eventcore.util.Common;
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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigManager.loadConfig();
        loadPlayersData();
        val vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (!economyPluginFound && vaultPlugin != null) {
            Common.debug("[Debug] use cost feature. I couldn't find Vault.");
            } else {
            Common.debug("[Debug] Can't use cost feature. Failed to get economy plugin.");
            getConfig().set("Cost.Enabled", false);
        }
        loadThings();
    }

    private void loadThings() {
        Common.registerEvent(new InventoryManager(), this);
        Common.registerEvent(new PlayerJoinEvent(), this);
        Common.debug("[Debug] Registered events");
        Common.registerCommand("event", new EventCommand());
        Common.debug("[Debug] Registered commands");
        Common.registerTabCompleter(new EventCommand(), "event");
        Common.debug("[Debug] Registered tab completer");
        economyPluginFound = setupEconomy();
        if (ConfigManager.CHECKUPDATE) {
            UpdateManager.start();
            Common.debug("[Debug] Started update-checker worker");
        }
        if (ConfigManager.BUNGEECORD) {
            setupChannel();
            Common.debug("[Debug] Setup channel successful");
        }
        new Metrics(this, 18612);
    }

    public void setupChannel() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "Event-Core");
        getServer().getMessenger().registerIncomingPluginChannel(this, "Event-Core", new CommunicationManager());
        CommunicationManager.sendSyncRequest();
    }

    @Override
    public void onDisable() {
        UpdateManager.stop();
        Common.debug("[Debug] Stopped update-check worker");
    }

    private void loadPlayersData() {
        file = new File(getDataFolder(), "playersData.yml");
        if (!file.exists()) {
            saveResource("playersData.yml", false);
            Common.debug("[Debug] Created playersData.yml");
        }
        playersData = YamlConfiguration.loadConfiguration(file);
        Common.debug("[Debug] Assigned players data");
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Common.debug("[Debug] Vault not found.");
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Common.debug("[Debug] Economy provider not found.");
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