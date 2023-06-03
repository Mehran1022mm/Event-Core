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

package ir.Mehran1022.EventCore.Utils;

import ir.Mehran1022.EventCore.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Common {
    public static String Color (String Message) {
        return ChatColor.translateAlternateColorCodes('&', Message);
    }
    // public static void Log (String Message) { Main.getInstance().getLogger().info(Color(Message)); }
    public static void RegisterEvent (Listener ListenerClass, Plugin PluginClass) {
        Main.getInstance().getServer().getPluginManager().registerEvents(ListenerClass, PluginClass);
    }
    public static void RegisterCommand (String CommandName, CommandExecutor CommandClass) {
        Objects.requireNonNull(Main.getInstance().getCommand(CommandName)).setExecutor(CommandClass);
    }
    public static void RegisterTabCompleter (TabCompleter Class, String CommandName) {
        Objects.requireNonNull(Main.getInstance().getCommand(CommandName)).setTabCompleter(Class);
    }
    public static void SendMessage (CommandSender Player, String Message) {
        Player.sendMessage(Color(Message));
    }
    public static void SendTitle (Player Player, String Title, String SubTitle, int FadeIn,int Stay, int FadeOut) {
        Player.sendTitle(Color(Title), Color(SubTitle), FadeIn, Stay, FadeOut);
    }
     public static void Ban (Player Player, String Reason) {
        String UserName = Player.getName();
        Bukkit.getBanList(BanList.Type.NAME).addBan(UserName, Color(Reason), null, "Administrator");
        Player.kickPlayer(Color(Reason));
    }
     public static void Freeze (Player Player, Boolean ClearINV) {
        Player.setWalkSpeed(0f);
        Player.setFlySpeed(0f);
        Player.setAllowFlight(false);
        Player.setCollidable(false);
        if (ClearINV) {
            Player.getInventory().clear();
        }
        Player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    }
     public static void UnFreeze (Player Player) {
        Player.setWalkSpeed((float) 0.2);
        Player.setFlySpeed((float) 0.2);
        Player.setAllowFlight(true);
        Player.setCollidable(true);
        Player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
    public static void SendActionBar (Player Player, String Message) {
        Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Color(Message)));
    }
    public static void SendToAnotherServer (Player Player, String ServerName) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(ServerName);
            Player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        } catch (Exception e) {
            Common.SendMessage(Player, "&cError When Trying To Send You To " + ServerName);
        }
    }
    public static void Confirmation (Player Player, String Message, String Command) {
        TextComponent Confirm = new TextComponent(Common.Color(" &a[✔]"));
        Confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Confirm.").create()));
        Confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Command));
        BaseComponent[] Text = new ComponentBuilder("").append(Message)
                .append(Confirm)
                .create();
        Player.spigot().sendMessage(Text);
    }
}
