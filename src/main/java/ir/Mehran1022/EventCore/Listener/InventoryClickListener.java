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

package ir.Mehran1022.EventCore.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void OnInvClick (InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (Objects.requireNonNull(event.getCurrentItem()).getType().name().equals("EMERALD")) {
            player.performCommand("event join");
            player.closeInventory();
        }
        if (event.getCurrentItem().getType().name().equals("EMERALD_BLOCK")) {
            player.performCommand("event start");
            player.closeInventory();
        }
        if (event.getCurrentItem().getType().name().equals("REDSTONE_BLOCK")) {
            player.performCommand("event end");
            player.closeInventory();
        }
        if (event.getCurrentItem().getType().name().equals("GRINDSTONE")) {
            player.performCommand("event reload");
            player.closeInventory();
        }
    }
}
