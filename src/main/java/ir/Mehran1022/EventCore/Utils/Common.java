package ir.Mehran1022.EventCore.Utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import ir.Mehran1022.EventCore.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Objects;

@SuppressWarnings("Deprecation")
public final class Common {
    private static final Main mainInstance = Main.getInstance();
    private static final Server server = mainInstance.getServer();
    private static final PluginManager pluginManager = server.getPluginManager();
//    private static final BanList banList = Bukkit.getBanList(BanList.Type.NAME);

    public static String color(String message) {
        return IridiumColorAPI.process(message);
    }

/*
    public static List<String> color(List<String> messages) {
        return IridiumColorAPI.process(messages);
    }
*/

    public static void log(String message) {
        server.getConsoleSender().sendMessage("[Event-Core] " + color(message));
    }

    public static void registerEvent(Listener listenerClass, Plugin pluginClass) {
        pluginManager.registerEvents(listenerClass, pluginClass);
    }

    public static void registerCommand(String commandName, CommandExecutor commandClass) {
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setExecutor(commandClass);
    }

    public static void registerTabCompleter(TabCompleter tabCompleterClass, String commandName) {
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setTabCompleter(tabCompleterClass);
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(color(message));
    }

/*
    public static void ban(Player player, String reason) {
        String userName = player.getName();
        String coloredReason = color(reason);
        banList.addBan(userName, coloredReason, null, "Administrator");
        player.kickPlayer(coloredReason);
    }
*/

/*
    public static void freeze(Player player, boolean clearInventory) {
        player.setWalkSpeed(0f);
        player.setFlySpeed(0f);
        player.setAllowFlight(false);
        player.setCollidable(false);
        if (clearInventory) {
            player.getInventory().clear();
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    }
*/

/*
    public static void unFreeze(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.2f);
        player.setAllowFlight(true);
        player.setCollidable(true);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
*/

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(message)));
    }

    public static void sendToAnotherServer(Player player, String serverName) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(mainInstance, "EventCore", b.toByteArray());
        } catch (Exception e) {
            sendMessage(player, "&cError When Trying To Send You To " + serverName);
            e.printStackTrace();
        }
    }

    public static void sendMessageToBungee(String message) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF(color(message));
            server.sendPluginMessage(mainInstance, "EventCoreMessage", b.toByteArray());
        } catch (Exception e) {
            log("&cError When Trying To Send Message To Bungee");
            e.printStackTrace();
        }
    }

    public static void confirmation(Player player, String message, String command) {
        TextComponent confirm = new TextComponent(color(" &a[✔]"));
        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Confirm.").create()));
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        BaseComponent[] text = new ComponentBuilder("").append(message)
                .append(confirm)
                .create();
        player.spigot().sendMessage(text);
    }
}
