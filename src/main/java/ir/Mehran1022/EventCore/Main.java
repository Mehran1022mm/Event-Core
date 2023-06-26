/*
                                MIT License

                        Copyright (c) 2023 Mehran1022

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
               furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
                    copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                                        SOFTWARE.
*/

package ir.Mehran1022.EventCore;

import ir.Mehran1022.EventCore.Commands.EventCommand;
import ir.Mehran1022.EventCore.Listeners.InventoryClickListener;
import ir.Mehran1022.EventCore.Listeners.PlayerJoinEvent;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Managers.UpdateManager;
import ir.Mehran1022.EventCore.Utils.Common;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    public static Main instance;

    private static Economy econ = null;

    public static boolean EconomyPluginFound = true;

    public static FileConfiguration PlayersData;

    public static java.io.File File;

    @Override
    public void onEnable () {
        instance = this;
        saveDefaultConfig();
        ConfigManager.loadConfig();
        if (!CompatibleVersion()) {
            getServer().getPluginManager().disablePlugin(this);
        }
        if (!EconomyPluginFound) {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                Common.Log("&c[Event-Core] Can't Use Cost Feature. Vault Not Found.");
            } else {
                Common.Log("&c[Event-Core] Can't Use Cost Feature. Failed To Get Economy Plugin.");
            }
        }
        LoadThings();
    }
    /**
     * @Override
     * public void onDisable () { }
     */
    private void LoadThings () {
        PlayersData();
        Common.RegisterEvent(new PlayerJoinEvent(), this);
        Common.RegisterCommand("event", new EventCommand());
        Common.RegisterEvent(new InventoryClickListener(), this);
        Common.RegisterTabCompleter(new EventCommand(), "event");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "EventCore");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "EventCoreMessage");
        EconomyPluginFound = setupEconomy();
        UpdateManager UM = new UpdateManager(); UM.Start();
        Metrics Metrics = new Metrics(this, 18612);
    }
    private void PlayersData () {
        File = new File(getDataFolder(), "PlayersData.yml");
        if (!File.exists()) {
            saveResource("PlayersData.yml", false);
        }
        PlayersData = YamlConfiguration.loadConfiguration(File);
    }
    private Boolean CompatibleVersion () {
        String Ver = getServer().getVersion();
        if (!Ver.contains("1.16") && !Ver.contains("1.17") && !Ver.contains("1.18") && Ver.contains("1.19") && !Ver.contains("1.20")) {
            Common.Log("Incompatible Version, Please Use 1.16.x And Above.");
            return false;
        }
        return true;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Vault Is Null.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("RegisteredServiceProvider IS Null.");
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public static Economy getEconomy () {
        return econ;
    }
    public static Main getInstance () {
        return instance;
    }
}