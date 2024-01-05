package ir.Mehran1022.EventCore.Listeners;

import com.google.gson.annotations.SerializedName;
import ir.Mehran1022.EventCore.Commands.EventCommand;
import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Utils.Common;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.UUID;

public final class PlayerJoinEvent implements Listener {
    /**
     * Handles the event when a player joins the server.
     *
     * @param event The PlayerJoinEvent
     */
    @SneakyThrows
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String uuidString = player.getUniqueId().toString();

        // Send event command message to player if Active
        if (EventCommand.Active) {
            Common.sendMessage(player, ConfigManager.PREFIX + EventCommand.EventDesc);
        }

        // Update player data in playersData.yml
        Main.playersData.set(uuidString + ".NAME", player.getName());
        Main.playersData.set(uuidString + ".BANNED", false);
        Main.playersData.save(Main.file);

        // Log debug message
        Common.logDebug(String.format("[Debug] Added %s to playersData.yml (PlayerJoin)", player.getName()));
    }
}

