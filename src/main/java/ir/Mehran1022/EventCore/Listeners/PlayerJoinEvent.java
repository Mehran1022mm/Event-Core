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

package ir.Mehran1022.EventCore.Listeners;

import ir.Mehran1022.EventCore.Configuration;
import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Utils.Common;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import sun.jvm.hotspot.debugger.MachineDescriptionIntelX86;

import java.io.IOException;
import java.util.UUID;

import static ir.Mehran1022.EventCore.Commands.EventCommand.Active;
import static ir.Mehran1022.EventCore.Commands.EventCommand.EventDesc;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void EventNotification (org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Active) {
            Common.SendMessage(player, Configuration.PREFIX + EventDesc);
        }
    }
    @EventHandler
    public void DataSave (org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Main.Config.set(uuid.toString() + ".NAME", player.getName());
        Main.Config.set(uuid.toString() + ".BANNED", false);
        try {
            Main.Config.save(Main.File);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
