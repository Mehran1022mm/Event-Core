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
import ir.Mehran1022.EventCore.Commands.TabCompleter;
import ir.Mehran1022.EventCore.Listeners.InventoryClickListener;
import ir.Mehran1022.EventCore.Listeners.PlayerJoinEvent;
import ir.Mehran1022.EventCore.Utils.Common;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main instance;
    private static Economy econ = null;
    public static boolean EconomyPluginFound = true;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Configuration.loadConfig();
        LoadThings();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        EconomyPluginFound = setupEconomy();
        if (!EconomyPluginFound) {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                Common.Log("&c[Event-Core] Can't Use Cost Feature. Vault Not Found.");
            } else {
                Common.Log("&c[Event-Core] Can't Use Cost Feature. Failed To Get Economy Plugin.");
            }
        }
        Metrics Metrics = new Metrics(this, 18612);
    }
    private void LoadThings () {
        Common.RegisterEvent(new PlayerJoinEvent(), this);
        Common.RegisterCommand("event", new EventCommand());
        Common.RegisterEvent(new InventoryClickListener(), this);
        Common.RegisterTabCompleter(new TabCompleter(), "event");
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
        return econ != null;
    }
    public static Economy getEconomy () {
        return econ;
    }
    public static Main getInstance () {
        return instance;
    }
}