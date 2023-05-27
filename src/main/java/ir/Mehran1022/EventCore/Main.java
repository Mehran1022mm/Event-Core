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

import ir.Mehran1022.FlameAPI.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {
    public static Main instance;
    private Boolean Active = false;
    private String EventDesc;
    private final List<Player> Players = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Configuration.loadConfig();
        Core.RegisterEvent(this, this);
        Core.RegisterCommand("event", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("event")) {
            return true;
        }
        if (args.length < 1) {
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            if (!sender.hasPermission("eventcore.admin")) {
                return true;
            }
            Configuration.loadConfig();
            long stop = System.currentTimeMillis();
            long time = stop - start;
            Core.SendMessage(sender, Configuration.PREFIX + "&aTook &c" + time + "ms &aTo Reload.");
        }
        if (!(sender instanceof Player)) {
            Core.SendMessage(sender, "Only Players Allowed.");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {

            if (!sender.hasPermission("eventcore.admin")) {
                Core.SendMessage(sender, Configuration.PREFIX + Configuration.NO_PERMISSION);
                return true;
            }
            if (Active) {
                Core.SendMessage(sender, Configuration.PREFIX + Configuration.ALREADY_STARTED);
                return true;
            }
            EventDesc = args.length > 1 ? String.join(" ", args).substring(6) : Configuration.NO_DESC;
            Active = true;
            Players.clear();
            Bukkit.broadcastMessage(Core.Color(Configuration.PREFIX + EventDesc));
            Core.SendActionBar((Player) sender, "&aSuccessfully Created An Event With Duration Of " + Configuration.DURATION);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Active = false;
                    Players.clear();
                    Bukkit.broadcastMessage(Core.Color(Configuration.PREFIX + Configuration.END));
                }
            }.runTaskLater(this, Configuration.DURATION * 20L);
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (!Active) {
                Core.SendMessage(sender,Configuration.PREFIX + Configuration.NO_EVENT);
                return true;
            }
            Player Player = (org.bukkit.entity.Player) sender;
            if (Players.contains(Player)) {
                Core.SendMessage(Player, Configuration.PREFIX + "&cAlready Connected.");
            } else {
                Players.add(Player);
                Core.SendToAnotherServer(Player, Configuration.SERVER_NAME);
                Bukkit.broadcastMessage(Core.Color(Configuration.PREFIX + "&b" + Player.getName() + "&f Has Joined The Event."));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("end")) {
            if (!sender.hasPermission("eventcore.admin")) {
                Core.SendMessage(sender, Configuration.PREFIX + Configuration.NO_PERMISSION);
                return true;
            }
            if (!Active) {
                Core.SendMessage(sender, Configuration.PREFIX + Configuration.NO_EVENT);
                return true;
            }
            Active = false;
            Players.clear();
            Core.SendMessage(sender, Configuration.PREFIX + "&aClosed The Active Event.");
        }
        return true;
    }
    @EventHandler
    public void OnPlayerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Active) {
            Core.SendMessage(player, Configuration.PREFIX + EventDesc);
        }
    }
    public static Main getInstance () {
        return instance;
    }
}