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

import ir.Mehran1022.EventCore.Configuration;
import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventCommand implements CommandExecutor {
    private Main Plugin;
    public static Boolean Active = false;
    public static String EventDesc;
    public static List<Player> Players = new ArrayList<>();

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("event")) { return true; }

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("eventcore.admin")) {
                    Item Border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));

                    ItemStack Emerald = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta Emerald_Meta = Emerald.getItemMeta();
                    List<String> Emerald_Lore = new ArrayList<>();
                    Emerald_Lore.add(Common.Color("&7Starts An Event With The Default Description."));
                    Objects.requireNonNull(Emerald_Meta).setLore(Emerald_Lore);
                    Emerald_Meta.setDisplayName(Common.Color("&aStart"));
                    Emerald.setItemMeta(Emerald_Meta);

                    ItemStack Redstone = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta Redstone_Meta = Emerald.getItemMeta();
                    List<String> Redstone_Lore = new ArrayList<>();
                    Redstone_Lore.add(Common.Color("&7Closes The Active Event."));
                    Objects.requireNonNull(Redstone_Meta).setLore(Redstone_Lore);
                    Redstone_Meta.setDisplayName(Common.Color("&cEnd"));
                    Redstone.setItemMeta(Redstone_Meta);

                    ItemStack Grindstone = new ItemStack(Material.GRINDSTONE);
                    ItemMeta Grindstone_Meta = Grindstone.getItemMeta();
                    List<String> Grindstone_Lore = new ArrayList<>();
                    Grindstone_Lore.add(Common.Color("&7Reloads The Configuration Files."));
                    Objects.requireNonNull(Grindstone_Meta).setLore(Grindstone_Lore);
                    Grindstone_Meta.setDisplayName(Common.Color("&dReload"));
                    Grindstone.setItemMeta(Grindstone_Meta);

                    Gui GUI = Gui.normal()
                            .setStructure(
                                    "# # # # # # # # #",
                                    "# . . . . . . . #",
                                    "# . - . . . + . #",
                                    "# . . . ! . . . #",
                                    "# # # # # # # # #")
                            .addIngredient('#', Border)
                            .addIngredient('-', Emerald)
                            .addIngredient('+', Redstone)
                            .addIngredient('!', Grindstone)
                            .build();

                    Window window = Window.single()
                            .setViewer(player)
                            .setTitle("Events - Admin View")
                            .setGui(GUI)
                            .build();

                    window.open();

                    return true;
                } else {
                    Item Border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));

                    ItemStack Emerald = new ItemStack(Material.EMERALD);
                    ItemMeta Emerald_Meta = Emerald.getItemMeta();
                    List<String> Emerald_Lore = new ArrayList<>();
                    Emerald_Lore.add(Common.Color("&7Click To Join."));
                    Objects.requireNonNull(Emerald_Meta).setLore(Emerald_Lore);
                    Emerald_Meta.setDisplayName(Common.Color("&aJoin"));
                    Emerald.setItemMeta(Emerald_Meta);

                    Gui GUI = Gui.normal()
                            .setStructure(
                                    "# # # # # # # # #",
                                    "# . . . . . . . #",
                                    "# . . . - . . . #",
                                    "# . . . . . . . #",
                                    "# # # # # # # # #")
                            .addIngredient('#', Border)
                            .addIngredient('-', Emerald)
                            .build();

                    Window window = Window.single()
                            .setViewer(player)
                            .setTitle("Events")
                            .setGui(GUI)
                            .build();

                    window.open();

                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            if (!sender.hasPermission("eventcore.admin")) {
                return true;
            }
            Configuration.loadConfig();
            long stop = System.currentTimeMillis();
            long time = stop - start;
            Common.SendMessage(sender, Configuration.PREFIX + "&aTook &c" + time + "ms &aTo Reload.");
        }
        if (!(sender instanceof Player)) {
            Common.SendMessage(sender, "Only Players Allowed.");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {

            if (!sender.hasPermission("eventcore.admin")) {
                Common.SendMessage(sender, Configuration.PREFIX + Configuration.NO_PERMISSION);
                return true;
            }
            if (Active) {
                Common.SendMessage(sender, Configuration.PREFIX + Configuration.ALREADY_STARTED);
                return true;
            }
            EventDesc = args.length > 1 ? String.join(" ", args).substring(6) : Configuration.NO_DESC;
            Active = true;
            Players.clear();
            String BossbarString = Configuration.BOSSBAR.replace("[Desc]", EventDesc);
            BossBar Bossbar = Bukkit.createBossBar(BossbarString, BarColor.RED, BarStyle.SOLID);
            Bukkit.broadcastMessage(Common.Color(Configuration.PREFIX + EventDesc));
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bossbar.addPlayer(player);
            }
            Common.SendActionBar((Player) sender, "&aSuccessfully Created An Event With Duration Of " + Configuration.DURATION + "Seconds.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Active = false;
                    Players.clear();
                    Bossbar.removeAll();
                    Bukkit.broadcastMessage(Common.Color(Configuration.PREFIX + Configuration.END));
                }
            }.runTaskLater(Plugin, Configuration.DURATION * 20L);
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (!Active) {
                Common.SendMessage(sender,Configuration.PREFIX + Configuration.NO_EVENT);
                return true;
            }
            Player Player = (org.bukkit.entity.Player) sender;
            if (Players.contains(Player)) {
                Common.SendMessage(Player, Configuration.PREFIX + "&cAlready Connected.");
            } else {
                Players.add(Player);
                Common.SendToAnotherServer(Player, Configuration.SERVER_NAME);
                Bukkit.broadcastMessage(Common.Color(Configuration.PREFIX + "&b" + Player.getName() + "&f Has Joined The Event."));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("end")) {
            if (!sender.hasPermission("eventcore.admin")) {
                Common.SendMessage(sender, Configuration.PREFIX + Configuration.NO_PERMISSION);
                return true;
            }
            if (!Active) {
                Common.SendMessage(sender, Configuration.PREFIX + Configuration.NO_EVENT);
                return true;
            }
            Active = false;
            Players.clear();
            Common.SendMessage(sender, Configuration.PREFIX + "&aClosed The Active Event.");
        }
        return true;
    }
}