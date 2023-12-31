package ir.mehran1022.eventcore.Listeners;

import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.Managers.ConfigManager;
import ir.mehran1022.eventcore.Utils.Common;
import ir.mehran1022.eventcore.Commands.EventCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.UUID;

public final class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidString = uuid.toString();

        if (EventCommand.Active) {
            Common.sendMessage(player, ConfigManager.PREFIX + EventCommand.EventDesc);
        }

        Main.playersData.set(uuidString + ".NAME", player.getName());
        Main.playersData.set(uuidString + ".BANNED", false);

        try {
            Main.playersData.save(Main.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

