package ir.mehran1022.eventcore.manager;

import ir.mehran1022.eventcore.util.Common;
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
        String title = getInventoryTitle(role);
        this.inventory = Bukkit.createInventory(this, 45, title);
        fillBorder(createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ", "&8Don't mind me."));

        switch (role) {
            case ADMIN -> setAdminItems();
            case PLAYER -> setPlayerItems();
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

        event.setCancelled(true);
        player.closeInventory();

        switch (itemType) {
            case "EMERALD" -> {
                Common.debug("[Debug] " + player.getName() + " clicked join button in the GUI");
                Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "join"), "/event join");
            }
            case "GRINDSTONE", "EMERALD_BLOCK", "REDSTONE_BLOCK" -> {
                if (!player.hasPermission("eventcore.admin")) {
                    Common.sendMessage(player, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return;
                }
                switch (itemType) {
                    case "EMERALD_BLOCK" -> {
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "create an event"), "/event start");
                        Common.debug("[Debug] " + player.getName() + " clicked create button in the GUI");
                    }
                    case "REDSTONE_BLOCK" -> {
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "close any open event"), "/event end");
                        Common.debug("[Debug] " + player.getName() + " clicked close button in the GUI");
                    }
                    case "GRINDSTONE" -> {
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "reload the configurations"), "/event reload");
                        Common.debug("[Debug] " + player.getName() + " clicked reload button in the GUI");
                    }
                }
            }
        }
    }

    private void fillBorder(ItemStack borderItem) {
        final int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, borderItem);
            }
        }
    }

    private ItemStack createItemStack(Material material, String displayName, String loreText) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = Collections.singletonList(Common.color(loreText));
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(Common.color(displayName));
        item.setItemMeta(meta);
        return item;
    }

    private String getInventoryTitle(Role role) {
        return role.equals(Role.ADMIN) ? Common.color("Events - &lADMIN") : "Events";
    }

    private void setAdminItems() {
        inventory.setItem(20, createItemStack(Material.EMERALD_BLOCK, "&aStart", "&7Starts an event with the default description."));
        inventory.setItem(24, createItemStack(Material.REDSTONE_BLOCK, "&cEnd", "&7Closes the active event."));
        inventory.setItem(31, createItemStack(Material.GRINDSTONE, "&dReload", "&7Reloads the configuration files."));
    }

    private void setPlayerItems() {
        inventory.setItem(22, createItemStack(Material.EMERALD, "&aJoin", "&7Click to join."));
    }
}
