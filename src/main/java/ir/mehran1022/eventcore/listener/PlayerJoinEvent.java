package ir.mehran1022.eventcore.listener;

import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.manager.ConfigManager;
import ir.mehran1022.eventcore.util.Common;
import ir.mehran1022.eventcore.command.EventCommand;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.UUID;

public final class PlayerJoinEvent implements Listener {

    @SneakyThrows
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String uuidString = player.getUniqueId().toString();

        if (EventCommand.Active) {
            Common.sendMessage(player, ConfigManager.PREFIX + EventCommand.EventDesc);
        }

        Main.playersData.set(uuidString + ".NAME", player.getName());
        Main.playersData.set(uuidString + ".BANNED", false);
        Main.playersData.save(Main.file);

        Common.debug("[Debug] Added " + player.getName() + " to playersData.yml (PlayerJoin)");
    }
}

