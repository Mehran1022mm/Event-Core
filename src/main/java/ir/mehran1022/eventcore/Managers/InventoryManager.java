package ir.mehran1022.eventcore.Managers;

import ir.mehran1022.eventcore.Utils.Common;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class InventoryManager implements Listener, InventoryHolder {

    @Getter
    private Inventory inventory;

    public enum Role {
        ADMIN,
        PLAYER
    }

    public void openInventory(Player player, Role role) {
        int size = 45;
        String title = getInventoryTitle(role);
        this.inventory = Bukkit.createInventory(this, size, title);
        fillBorder(createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ", "&8Don't mind me."));

        if (role.equals(Role.ADMIN)) {
            setAdminItems();
        } else if (role.equals(Role.PLAYER)) {
            setPlayerItems();
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void OnInventoryClick (InventoryClickEvent event) {

            Player player = (Player) event.getWhoClicked();
            ItemStack currentItem = event.getCurrentItem();

            if (currentItem == null) {
                return;
            }

            String itemType = currentItem.getType().name();

            if (event.getInventory().getHolder() != this) {
                player.sendMessage("holder is " + event.getInventory().getHolder().toString());
            }

            event.setCancelled(true);
            player.closeInventory();

            switch (itemType) {
                case "EMERALD":
                    Common.confirmation(player, ConfigManager.PREFIX + "You Are Going To Join An Event. Are you Sure??", "/event join");
                    break;
                case "EMERALD_BLOCK":
                case "REDSTONE_BLOCK":
                case "GRINDSTONE":
                    if (!player.hasPermission("eventcore.admin")) {
                        Common.sendMessage(player, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                        return;
                    }
                    switch (itemType) {
                        case "EMERALD_BLOCK":
                            Common.confirmation(player, ConfigManager.PREFIX + "You Are Going To Start An Event. Are you Sure??", "/event start");
                            break;
                        case "REDSTONE_BLOCK":
                            Common.confirmation(player, ConfigManager.PREFIX + "You Are Going To Close An Event. Are you Sure??", "/event end");
                            break;
                        case "GRINDSTONE":
                            Common.confirmation(player, ConfigManager.PREFIX + "You Are Going To Reload The Plugin. Are you Sure??", "/event reload");
                            break;
                    }
                    break;
            }
    }

    private void fillBorder(ItemStack borderItem) {
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, borderItem);
            }
        }
    }

    private ItemStack createItemStack(Material material, String displayName, String loreText) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = Collections.singletonList(Common.color(loreText));
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(Common.color(displayName));
        item.setItemMeta(meta);
        return item;
    }

    private String getInventoryTitle(Role role) {
        return role.equals(Role.ADMIN) ? Common.color("Events - &lADMIN") : "Events";
    }

    private void setAdminItems() {
        inventory.setItem(20, createItemStack(Material.EMERALD_BLOCK, "&aStart", "&7Starts An Event With The Default Description."));
        inventory.setItem(24, createItemStack(Material.REDSTONE_BLOCK, "&cEnd", "&7Closes The Active Event."));
        inventory.setItem(31, createItemStack(Material.GRINDSTONE, "&dReload", "&7Reloads The Configuration Files."));
    }

    private void setPlayerItems() {
        inventory.setItem(22, createItemStack(Material.EMERALD, "&aJoin", "&7Click To Join."));
    }
}
