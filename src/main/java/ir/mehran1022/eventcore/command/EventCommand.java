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

package ir.mehran1022.eventcore.command;

import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.manager.CommunicationManager;
import ir.mehran1022.eventcore.manager.ConfigManager;
import ir.mehran1022.eventcore.manager.InventoryManager;
import ir.mehran1022.eventcore.util.Common;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class EventCommand implements CommandExecutor, TabCompleter {

    public static Boolean Active = false;

    public static String EventDesc;

    public static List<Player> Players = new ArrayList<>();
    private static BossBar bossBar;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("event")) {
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return true;
            }
            final Player player = (Player) sender;
            new InventoryManager().openInventory(player, (player.hasPermission("eventcore.admin") ? InventoryManager.Role.ADMIN : InventoryManager.Role.PLAYER));
            Common.debug("[Debug] Opened a GUI for " + player.getName());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("eventcore.admin")) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return true;
                }
                final long start = System.currentTimeMillis();
                ConfigManager.loadConfig();
                Common.sendMessage(sender, ConfigManager.PREFIX + "&aTook &c" + (System.currentTimeMillis() - start) + "ms &aTo Reload.");
                return true;
            }
            case "sync" -> {
                handleSync(sender);
                return true;
            }
            case "help" -> {
                sendHelpMessage(sender);
                return true;
            }
            case "start" -> {
                if (!sender.hasPermission("eventcore.admin")) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return true;
                }
                if (Active) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.ALREADY_STARTED);
                    return true;
                }
                startEvent(sender, args);
                return true;
            }
            case "join" -> {
                handleJoinCommand(sender);
                return true;
            }
            case "end" -> {
                handleEndCommand(sender);
                return true;
            }
            case "block" -> {
                handleBlockCommand(sender, args);
                return true;
            }
            case "unblock" -> {
                handleUnblockCommand(sender, args);
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        final List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.addAll(getCommandSuggestions(sender));
        } else if (args.length == 2 && (args[1].equalsIgnoreCase("Block") || args[1].equalsIgnoreCase("UnBlock")) && sender.hasPermission("eventcore.admin")) {
            suggestions.addAll(getPlayerNameSuggestions());
        }
        return suggestions;
    }

    private void sendHelpMessage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Common.sendMessage(sender, "&cOnly Players Allowed.");
        }
        String prefix = ConfigManager.PREFIX + "Event-Core v" + Main.getInstance().getDescription().getVersion();
        String[] messages = {
                "   &a/Event &f- &cOpens " + (sender.hasPermission("eventcore.admin") ? "Admin" : "Player") + " Panel.",
                "   &a/Event Help &f- &cSends help Message.",
                "   &a/Event Join &f- &cSends you to events server."
        };
        Common.sendMessage(sender, prefix);
        for (String message : messages) {
            Common.sendMessage(sender, message);
        }

        if (sender.hasPermission("eventcore.admin")) {
            String[] adminMessages = {
                    "   &a/Event End &f- &cCloses any open event.",
                    "   &a/Event Reload &f- &cReloads plugin configuration files.",
                    "   &a/Event Block <Player> &f- &cBlocks a player.",
                    "   &a/Event UnBlock <Player> &f- &cUnBlocks a player.",
                    "   &a/Event Sync &f- &cSync bungeecord."
            };
            for (String message : adminMessages) {
                Common.sendMessage(sender, message);
            }
        }
    }

    private void startEvent(CommandSender sender, String[] args) {
        EventDesc = args.length >= 2 && args[1] != null ? String.join(" ", args).substring(6) : ConfigManager.NO_DESC;
        Active = true;
        Players.clear();
        bossBar = Bukkit.createBossBar(ConfigManager.BOSSBAR.replace("[Desc]", EventDesc), getRandomBarColor(), getRandomBarStyle());
        Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + EventDesc));
        if (ConfigManager.BUNGEECORD && CommunicationManager.isSynced()) {
            CommunicationManager.sendBroadcastToBungee(Common.color(ConfigManager.PREFIX + EventDesc));
        }
        for (Player P : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(P);
            P.sendTitle(Common.color(ConfigManager.TITLE), Common.color(ConfigManager.SUBTITLE), ConfigManager.FADEIN, ConfigManager.STAY, ConfigManager.FADEOUT);
        }
        Common.sendActionBar((Player) sender, "&a&lSuccessfully Created An Event With Duration Of " + ConfigManager.DURATION + " Seconds.");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Active) {
                    Active = false;
                    Players.clear();
                    bossBar.removeAll();
                    Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + ConfigManager.END));
                }
            }
        }.runTaskLater(Main.getInstance(), ConfigManager.DURATION * 20L);
    }

    private void handleJoinCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Common.sendMessage(sender, "&cOnly Players Allowed.");
        }
        if (!Active) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_EVENT);
            return;
        }
        assert sender instanceof Player;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (Main.playersData.contains(uuid.toString()) && Objects.equals(Main.playersData.get(uuid + ".BANNED"), true)) {
            Common.sendMessage(player, ConfigManager.PREFIX + ConfigManager.BLOCKED);
            return;
        }
        if (Players.contains(player)) {
            Common.sendMessage(player, ConfigManager.PREFIX + "&cAlready Connected.");
        } else {
            Players.add(player);
            if (Main.economyPluginFound && ConfigManager.ENABLE_COST) {
                EconomyResponse response = Main.getEconomy().withdrawPlayer(player, ConfigManager.COST);
                Common.sendMessage(player, ConfigManager.PREFIX + "This Event Subtracted " + ConfigManager.COST.toString() + "$ From Your Bank. You Have " + Main.getEconomy().format(Main.getEconomy().withdrawPlayer(player, ConfigManager.COST).balance) + "$ Now");
                Common.debug("Withdrew " + ConfigManager.COST + " From " + player.getName());
            }
            CommunicationManager.sendToAnotherServer(player, ConfigManager.SERVER_NAME);
            Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + "&b" + player.getName() + "&f Has Joined The Event."));
        }
    }

    private void handleEndCommand(CommandSender sender) {
        if (!sender.hasPermission("eventcore.admin")) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }
        if (!Active) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_EVENT);
            return;
        }
        Active = false;
        Players.clear();
        bossBar.removeAll();
        Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + ConfigManager.END));
        Common.sendMessage(sender, ConfigManager.PREFIX + "&aClosed The Active Event.");
    }

    private void handleBlockCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eventcore.admin")) {
            sender.sendMessage(ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }
        if (args[1].length() < 1) {
            return;
        }
        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.OFFLINE).replace("[Player]", args[1]));
            return;
        }
        UUID uuid = player.getUniqueId();
        if (Main.playersData.contains(uuid.toString())) {
            Main.playersData.set(uuid + ".BANNED", true);
            try {
                Main.playersData.save(Main.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.BLOCK).replace("[Player]", player.getName()));
        }
    }

    private void handleUnblockCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eventcore.admin")) {
            sender.sendMessage(ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }
        if (args[1].length() < 1) {
            return;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.OFFLINE).replace("[Player]", args[1]));
            return;
        }
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        if (Main.playersData.contains(uuid.toString())) {
            Main.playersData.set(uuid + ".BANNED", false);
            try {
                Main.playersData.save(Main.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.UNBLOCK).replace("[Player]", playerName));
        }
    }

    private void handleSync(CommandSender sender) {
        if (!sender.hasPermission("eventcore.admin")) {
            sender.sendMessage(ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }
        if (!ConfigManager.BUNGEECORD) {
            sender.sendMessage(ConfigManager.PREFIX + Common.color("&cYou can't sync because Bungeecord is turned off. Contact your server administrator."));
        }
        CommunicationManager.sendSyncRequest();
    }

    private List<String> getCommandSuggestions(CommandSender sender) {
        final List<String> commands = new ArrayList<>(Arrays.asList("join", "help"));
        if (sender.hasPermission("eventcore.admin")) {
            commands.addAll(Arrays.asList("sync", "end", "start", "block", "unblock", "reload"));
        }
        return commands;
    }

    private List<String> getPlayerNameSuggestions() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private BarColor getRandomBarColor() {
        final BarColor[] colors = BarColor.values();
        return colors[new Random().nextInt(colors.length)];
    }

    private BarStyle getRandomBarStyle() {
        final BarStyle[] styles = BarStyle.values();
        return  styles[new Random().nextInt(styles.length)];
    }
}
