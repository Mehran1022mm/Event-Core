package ir.Mehran1022.EventCore.Utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Server;
import org.bukkit.command.Command;
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

@UtilityClass
@SuppressWarnings("Deprecation")
public final class Common {
    private final Main mainInstance = Main.getInstance();
    private final Server server = mainInstance.getServer();
    private final PluginManager pluginManager = server.getPluginManager();

    public String color(String message) {
        return IridiumColorAPI.process(message);
    }

    /**
     * Logs a message to the console.
     *
     * @param  message  the message to be logged
     */
    public void log(String message) {
        server.getConsoleSender().sendMessage(String.format("[Event-Core] %s", color(message)));
    }
    /**
     * Logs a debug message.
     *
     * @param  message  the message to be logged
     */
    public void logDebug(String message) {
        if(ConfigManager.DEBUG)
            return;
        server.getConsoleSender().sendMessage(String.format("[Event-Core] %s", color(message)));
    }

    /**
     * Registers an event listener with the plugin manager.
     *
     * @param listenerClass The listener class to register.
     * @param pluginClass The plugin class to register.
     */
    public void registerEvent(Listener listenerClass, Plugin pluginClass) {
        pluginManager.registerEvents(listenerClass, pluginClass);
    }

    /**
     * Registers a command with the given name and command executor.
     *
     * @param commandName The name of the command to register.
     * @param commandClass The command executor to associate with the command.
     */
    public void registerCommand(String commandName, CommandExecutor commandClass) {
        // Retrieve the command from the main instance using the command name
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setExecutor(commandClass);
    }

    /**
     * Registers a TabCompleter for a command.
     *
     * @param tabCompleterClass The TabCompleter class to register.
     * @param commandName The name of the command.
     */
    public void registerTabCompleter(TabCompleter tabCompleterClass, String commandName) {
        Objects.requireNonNull(mainInstance.getCommand(commandName)).setTabCompleter(tabCompleterClass);
    }

    /**
     * Sends a message to a command sender.
     *
     * @param player   the command sender to send the message to
     * @param message  the message to send
     */
    public void sendMessage(CommandSender player, String message) {
        player.sendMessage(color(message));
    }

    /**
     * Sends an action bar message to a player.
     *
     * @param  player   the player to send the action bar message to
     * @param  message  the message to be sent
     */
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(message)));
    }

    /**
     * Sends the player to another server.
     *
     * @param player     The player to send.
     * @param serverName The name of the server to connect to.
     */
    public void sendToAnotherServer(Player player, String serverName) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {

            // Write the command and server name to the output stream
            out.writeUTF("Connect");
            out.writeUTF(serverName);

            // Send the plugin message to the player
            player.sendPluginMessage(mainInstance, "EventCore", b.toByteArray());
        } catch (Exception e) {

            // Display error message to player
            sendMessage(player, "&cError When Trying To Send You To " + serverName);

            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the Bungee server.
     *
     * @param message The message to send.
     */
    public void sendMessageToBungee(String message) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF(color(message)); // Convert the message to colored text
            server.sendPluginMessage(mainInstance, "EventCoreMessage", b.toByteArray()); // Send the message to the Bungee server
        } catch (Exception e) {
            log("&cError When Trying To Send Message To Bungee"); // Log an error message
            e.printStackTrace(); // Print the stack trace of the exception
        }
    }

    /**
     * Sends a confirmation message to the player with a clickable text component.
     *
     * @param player  The player to send the confirmation message to.
     * @param message The message to display before the confirmation text.
     * @param command The command to execute when the confirmation text is clicked.
     */
    public void confirmation(Player player, String message, String command) {
        // Create the confirmation text component
        val confirm = new TextComponent(color(" &a[✔]"));

        // Set the hover event for the confirmation text
        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To Confirm.").create()));

        // Set the click event for the confirmation text
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        // Send the message to the player
        player.spigot().sendMessage(new ComponentBuilder("").append(message)
                .append(confirm)
                .create());
    }
}
