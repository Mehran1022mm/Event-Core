package ir.mehran1022.eventcore.util;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.manager.ConfigManager;
import lombok.experimental.UtilityClass;
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

import java.util.Objects;

@UtilityClass
@SuppressWarnings("deprecation")
public final class Common {

    private final Main mainInstance = Main.getInstance();
    private final Server server = mainInstance.getServer();
    private final PluginManager pluginManager = server.getPluginManager();

    public String color(String message) {
        return IridiumColorAPI.process(message);
    }

    public void log(String message) {
        server.getConsoleSender().sendMessage("[Event-Core] " + color(message));
    }

    public void debug(String message) {
        if (!ConfigManager.DEBUG) return;
        server.getConsoleSender().sendMessage("[Event-Core] " + color(message));
    }

    public void registerEvent(Listener listenerClass, Plugin pluginClass) {
        pluginManager.registerEvents(listenerClass, pluginClass);
    }

    public void registerCommand(String commandName, CommandExecutor commandClass) {
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setExecutor(commandClass);
    }

    public void registerTabCompleter(TabCompleter tabCompleterClass, String commandName) {
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setTabCompleter(tabCompleterClass);
    }

    public void sendMessage(CommandSender player, String message) {
        player.sendMessage(color(message));
    }

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(message)));
    }

    public void confirmation(Player player, String message, String command) {
        TextComponent confirm = new TextComponent(color(" &a[✔]"));
        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Confirm.").create()));
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        BaseComponent[] text = new ComponentBuilder("").append(message)
                .append(confirm)
                .create();
        player.spigot().sendMessage(text);
    }
}
