package ir.Mehran1022.EventCore.Listeners;

import ir.Mehran1022.EventCore.Commands.EventCommand;
import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Managers.ConfigManager;
import ir.Mehran1022.EventCore.Utils.Common;
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

        if (ConfigManager.DEBUG) {
            Common.log("[Debug] Added " + player.getName() + " to playersData.yml (PlayerJoin)");
        }
    }
}

