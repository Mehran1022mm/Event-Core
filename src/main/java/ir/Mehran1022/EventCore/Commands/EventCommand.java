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

package ir.Mehran1022.EventCore.Commands;

import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Managers.InventoryManager;
import ir.Mehran1022.EventCore.Utils.Common;
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

    /**
     * Handles the command execution.
     *
     * @param sender  the command sender
     * @param command the command object
     * @param label   the command label
     * @param args    the command arguments
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(final @NotNull CommandSender sender, final Command command, final @NotNull String label, final String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            Common.sendMessage(sender, "Only Players Allowed.");
            return true;
        }

        // Check if the command is "event"
        if (!command.getName().equalsIgnoreCase("event")) {
            return true;
        }

        // Check if there are no arguments
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return true;
            }

            final Player player = (Player) sender;

            // Open the inventory for the player
            new InventoryManager().openInventory(player, (player.hasPermission("eventcore.admin") ? InventoryManager.Role.ADMIN : InventoryManager.Role.PLAYER));
            Common.logDebug("[Debug] Opened a GUI for " + player.getName());
            return true;
        }

        // Handle different command arguments
        switch (args[0]) {
            case "reload": {
                // Check if the sender has the admin permission
                if (!sender.hasPermission("eventcore.admin")) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return true;
                }

                final long start = System.currentTimeMillis();

                // Reload the config
                ConfigManager.loadConfig();
                Common.sendMessage(sender, String.format("%s&aTook &c%s &aTo Reload.", ConfigManager.PREFIX, System.currentTimeMillis() - start));
                return true;
            }
            case "help": {
                // Send the help message
                sendHelpMessage(sender);
                return true;
            }
            case "start": {
                // Check if the sender has the admin permission
                if (!sender.hasPermission("eventcore.admin")) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return true;
                }

                // Check if the event is already active
                if (Active) {
                    Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.ALREADY_STARTED);
                    return true;
                }

                // Start the event
                startEvent(sender, args);
            }
            case "join": {
                // Handle the join command
                handleJoinCommand(sender);
                return true;
            }
            case "end": {
                // Handle the end command
                handleEndCommand(sender);
                return true;
            }
            case "block": {
                // Handle the block command
                handleBlockCommand(sender, args);
                return true;
            }
            case "unblock": {
                // Handle the unblock command
                handleUnblockCommand(sender, args);
                return true;
            }
        }
        return true;
    }

    /**
     * Generates a list of tab completions for a command.
     *
     * @param sender the command sender
     * @param cmd the command being tab completed
     * @param s the alias or label of the command being tab completed
     * @param args the arguments provided by the command sender
     * @return a list of tab completions
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        final List<String> suggestions = new ArrayList<>();

        // Generate suggestions based on the number of arguments
        if (args.length == 1) {
            suggestions.addAll(getCommandSuggestions(sender));
        } else if (args.length == 2 && (args[1].equalsIgnoreCase("Block") || args[1].equalsIgnoreCase("UnBlock")) && sender.hasPermission("eventcore.admin")) {
            suggestions.addAll(getPlayerNameSuggestions());
        }

        return suggestions;
    }

    /**
     * Sends a help message to the command sender.
     *
     * @param sender The command sender to send the message to.
     */
    private void sendHelpMessage(CommandSender sender) {
        // Send plugin version
        Common.sendMessage(sender, ConfigManager.PREFIX + "Event-Core v" + Main.getInstance().getDescription().getVersion());

        // Build the help message
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("   &a/Event &f- &cOpens ").append(sender.hasPermission("eventcore.admin") ? "Admin" : "Player").append(" Panel.\n");
        stringBuilder.append("   &a/Event Help &f- &cSends help Message.\n");
        stringBuilder.append("   &a/Event Join &f- &cSends you to events server.");

        // Add admin commands if the sender has admin permission
        if (sender.hasPermission("eventcore.admin")) {
            stringBuilder.append('\n');
            stringBuilder.append("   &a/Event End &f- &cCloses any open event.\n");
            stringBuilder.append("   &a/Event Reload &f- &cReloads plugin configuration files.\n");
            stringBuilder.append("   &a/Event Block <Player> &f- &cBlocks a player.\n");
            stringBuilder.append("   &a/Event UnBlock <Player> &f- &cUnBlocks a player.");
        }

        // Send the help message
        Common.sendMessage(sender, stringBuilder.toString());
    }

    /**
     * Starts the event by creating a boss bar, broadcasting a message, adding players to the boss bar, sending titles to players,
     * and scheduling a task to end the event after a certain duration.
     *
     * @param sender the command sender
     * @param args the command arguments
     */
    private void startEvent(CommandSender sender, String[] args) {
        Active = true;
        Players.clear();

        // Create the boss bar string
        String BossbarString = ConfigManager.BOSSBAR.replace("[Desc]", args.length >= 2 && args[1] != null ? String.join(" ", args).substring(6) : ConfigManager.NO_DESC);

        // Create the boss bar
        bossBar = Bukkit.createBossBar(BossbarString, getRandomBarColor(), getRandomBarStyle());

        // Broadcast the event description message
        Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + EventDesc));

        // Add players to the boss bar and send titles to players
        for (Player P : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(P);
            P.sendTitle(Common.color(ConfigManager.TITLE), Common.color(ConfigManager.SUBTITLE), ConfigManager.FADEIN, ConfigManager.STAY, ConfigManager.FADEOUT);
        }

        // Send action bar message to the sender
        Common.sendActionBar((Player) sender, "&a&lSuccessfully Created An Event With Duration Of " + ConfigManager.DURATION + " Seconds.");

        // Schedule a task to end the event after a certain duration
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

    /**
     * Handles the join command for the event.
     *
     * @param sender The command sender
     */
    private void handleJoinCommand(CommandSender sender) {
        Player player = (Player) sender;

        // Check if the event is active
        if (!Active) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_EVENT);
            return;
        }

        // Check if the player is banned
        if (Main.playersData.contains(player.getUniqueId().toString()) && Objects.equals(Main.playersData.get(player.getUniqueId() + ".BANNED"), true)) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.BLOCKED);
            return;
        }

        // Check if the player is already banned
        if (Main.playersData.contains(player.getUniqueId().toString()) && Objects.equals(Main.playersData.get(player.getUniqueId() + ".BANNED"), true)) {
            Common.sendMessage(player, ConfigManager.PREFIX + ConfigManager.BLOCKED);
            return;
        }

        // Check if the player is already connected
        if (Players.contains(player)) {
            Common.sendMessage(player, ConfigManager.PREFIX + "&cAlready Connected.");
        } else {
            Players.add(player);

            // Subtract the event cost from the player's bank if economy plugin found and cost is enabled
            if (Main.economyPluginFound && ConfigManager.ENABLE_COST) {
                Common.sendMessage(player, ConfigManager.PREFIX + "This Event Subtracted " + ConfigManager.COST.toString() + "$ From Your Bank. You Have " + Main.getEconomy().format(Main.getEconomy().withdrawPlayer(player, ConfigManager.COST).balance) + "$ Now");
                System.out.println("Withdrew " + ConfigManager.COST + " From " + player.getName());
            }

            // Send the player to another server
            Common.sendToAnotherServer(player, ConfigManager.SERVER_NAME);

            // Broadcast that the player has joined the event
            Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + "&b" + player.getName() + "&f Has Joined The Event."));
        }
    }

    /**
     * Handles the end command.
     *
     * @param sender The command sender
     */
    private void handleEndCommand(CommandSender sender) {
        // Check if the sender has the admin permission
        if (!sender.hasPermission("eventcore.admin")) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }

        // Check if there is an active event
        if (!Active) {
            Common.sendMessage(sender, ConfigManager.PREFIX + ConfigManager.NO_EVENT);
            return;
        }

        // Set the active flag to false and clear the players list
        Active = false;
        Players.clear();

        // Remove the boss bar and broadcast the end message
        bossBar.removeAll();
        Bukkit.broadcastMessage(Common.color(ConfigManager.PREFIX + ConfigManager.END));

        // Send a message to the sender indicating that the event has been closed
        Common.sendMessage(sender, ConfigManager.PREFIX + "&aClosed The Active Event.");
    }

    /**
     * Handles the block command.
     *
     * @param sender the command sender
     * @param args   the command arguments
     */
    private void handleBlockCommand(CommandSender sender, String[] args) {
        // Check if the sender has the admin permission
        if (!sender.hasPermission("eventcore.admin")) {
            sender.sendMessage(ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }

        // Check if the arguments array has at least one element
        if (args[1].length() < 1) {
            return;
        }

        // Get the player with the given name
        final Player player = Bukkit.getPlayer(args[1]);

        // Check if the player is offline
        if (player == null) {
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.OFFLINE).replace("[Player]", args[1]));
            return;
        }

        // Ban the player and save the updated player data
        if (Main.playersData.contains(player.getUniqueId().toString())) {
            Main.playersData.set(player.getUniqueId() + ".BANNED", true);
            try {
                Main.playersData.save(Main.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.BLOCK).replace("[Player]", player.getName()));
        }
    }

    /**
     * Handle the unblock command.
     *
     * @param sender the command sender
     * @param args   the command arguments
     */
    private void handleUnblockCommand(CommandSender sender, String[] args) {
        // Check if the sender has the admin permission
        if (!sender.hasPermission("eventcore.admin")) {
            sender.sendMessage(ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
            return;
        }

        // Check if the arguments array has at least one element
        if (args[1].length() < 1) {
            return;
        }

        // Get the player by their name
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            // Send a message if the player is offline
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.OFFLINE).replace("[Player]", args[1]));
            return;
        }

        // Get the player's name and UUID
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        // Check if the player's UUID is in the playersData list
        if (Main.playersData.contains(uuid.toString())) {
            // Set the "BANNED" key to false in the playersData list
            Main.playersData.set(uuid + ".BANNED", false);

            try {
                // Save the playersData list to file
                Main.playersData.save(Main.file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Send a message to the sender indicating that the player has been unblocked
            Common.sendMessage(sender, ConfigManager.PREFIX + (ConfigManager.UNBLOCK).replace("[Player]", playerName));
        }
    }

    private List<String> getCommandSuggestions(CommandSender sender) {
        final List<String> commands = new ArrayList<>(Arrays.asList("Join", "Help"));
        if (sender.hasPermission("eventcore.admin")) {
            commands.addAll(Arrays.asList("End", "Start", "Block", "UnBlock", "Reload"));
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
        BarStyle[] styles = BarStyle.values();
        return styles[new Random().nextInt(styles.length)];
    }
}
