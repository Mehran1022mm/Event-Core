package ir.Mehran1022.FlameAPI;

import ir.Mehran1022.EventCore.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Objects;

public class Core {
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
    public static void SendMessage (CommandSender Player, String Message) {
        Player.sendMessage(Color(Message));
    }
    /* public static void Ban (Player Player, String Reason) {
        String UserName = Player.getName();
        Bukkit.getBanList(BanList.Type.NAME).addBan(UserName, Color(Reason), null, "Administrator");
        Player.kickPlayer(Color(Reason));
    } */
    /* public static void Freeze (Player Player, Boolean ClearINV) {
        Player.setWalkSpeed(0f);
        Player.setFlySpeed(0f);
        Player.setAllowFlight(false);
        Player.setCollidable(false);
        if (ClearINV) {
            Player.getInventory().clear();
        }
        Player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    } */
    /* public static void UnFreeze (Player Player) {
        Player.setWalkSpeed((float) 0.2);
        Player.setFlySpeed((float) 0.2);
        Player.setAllowFlight(true);
        Player.setCollidable(true);
        Player.removePotionEffect(PotionEffectType.BLINDNESS);
    } */
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
            Core.SendMessage(Player, "&cError When Trying To Send You To " + ServerName);
        }
    }
    // public static void ShutDown () { Bukkit.shutdown(); }
}
