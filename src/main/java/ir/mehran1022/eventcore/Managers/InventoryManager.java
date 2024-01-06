package ir.Mehran1022.EventCore.Managers;

import ir.Mehran1022.EventCore.Utils.Common;
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

    /**
     * Opens the inventory for the specified player based on their role.
     *
     * @param player the player to open the inventory for
     * @param role   the role of the player
     */
    public void openInventory(Player player, Role role) {
        // Create the inventory with the appropriate title
        this.inventory = Bukkit.createInventory(this, 45, getInventoryTitle(role));

        // Fill the border of the inventory with black stained glass panes
        fillBorder(createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ", "&8Don't mind me."));

        // Set the items in the inventory based on the player's role
        switch (role) {
            case ADMIN:
                setAdminItems();
                break;
            case PLAYER:
                setPlayerItems();
                break;
        }

        // Open the inventory for the player
        player.openInventory(inventory);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        // Get the player who clicked the inventory
        final Player player = (Player) event.getWhoClicked();

        // Get the clicked item
        final ItemStack currentItem = event.getCurrentItem();

        // If no item was clicked, return
        if (currentItem == null)
            return;

        // Cancel the event and close the inventory
        event.setCancelled(true);
        player.closeInventory();

        // Handle different types of clicked items
        switch (currentItem.getType().name()) {
            case "EMERALD":
                // If the clicked item is an emerald, show confirmation message and command for joining
                if (ConfigManager.DEBUG) {
                    Common.log(String.format("[Debug] %s clicked join button in the GUI", player.getName()));
                }
                Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "join"), "/event join");
                break;
            case "EMERALD_BLOCK":
            case "REDSTONE_BLOCK":
            case "GRINDSTONE":
                // If the clicked item is an emerald block, redstone block, or grindstone, check for admin permission
                if (!player.hasPermission("eventcore.admin")) {
                    // If player doesn't have admin permission, show no permission message and return
                    Common.sendMessage(player, ConfigManager.PREFIX + ConfigManager.NO_PERMISSION);
                    return;
                }

                // Handle different types of admin actions
                switch (currentItem.getType().name()) {
                    case "EMERALD_BLOCK":
                        // If the clicked item is an emerald block, show confirmation message and command for creating an event
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "create an event"), "/event start");
                        Common.logDebug(String.format("[Debug] %s clicked create button in the GUI", player.getName()));
                        break;
                    case "REDSTONE_BLOCK":
                        // If the clicked item is a redstone block, show confirmation message and command for closing an event
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "close any open event"), "/event end");
                        Common.logDebug(String.format("[Debug] %s clicked close button in the GUI", player.getName()));
                        break;
                    case "GRINDSTONE":
                        // If the clicked item is a grindstone, show confirmation message and command for reloading configurations
                        Common.confirmation(player, ConfigManager.PREFIX + ConfigManager.CONFIRMATION.replace("[Action]", "reload the configurations"), "/event reload");
                        Common.logDebug("[Debug] " + player.getName() + " clicked reload button in the GUI");
                        break;
                }
                break;
        }
    }

    /**
     * Fills the border of the inventory with the specified border item.
     *
     * @param borderItem the item to fill the border with
     */
    private void fillBorder(ItemStack borderItem) {
        // Get the size of the inventory
        final int size = inventory.getSize();

        // Loop through each slot in the inventory
        for (int i = 0; i < size; i++) {
            // Check if the slot is in the border
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                // Set the border item in the slot
                inventory.setItem(i, borderItem);
            }
        }
    }

    /**
     * Creates an ItemStack with the given material, display name, and lore text.
     *
     * @param material    the material of the ItemStack
     * @param displayName the display name of the ItemStack
     * @param loreText    the lore text of the ItemStack
     * @return the created ItemStack
     */
    private ItemStack createItemStack(Material material, String displayName, String loreText) {
        // Create a new ItemStack with the given material
        final ItemStack item = new ItemStack(material);

        // Get the ItemMeta of the ItemStack
        final ItemMeta meta = item.getItemMeta();

        // Create a list with the lore text and set it as the lore of the ItemMeta
        final List<String> lore = Collections.singletonList(Common.color(loreText));
        Objects.requireNonNull(meta).setLore(lore);

        // Set the display name of the ItemMeta
        meta.setDisplayName(Common.color(displayName));

        // Set the modified ItemMeta back to the ItemStack
        item.setItemMeta(meta);

        // Return the created ItemStack
        return item;
    }

    /**
     * Generates the title for the inventory based on the specified role.
     *
     * @param role the role of the user
     * @return the title for the inventory
     */
    private String getInventoryTitle(Role role) {
        return role.equals(Role.ADMIN) ? Common.color("Events - &lADMIN") : "Events";
    }

    /**
     * Sets the admin items in the inventory.
     */
    private void setAdminItems() {
        // Create and set the "Start" item
        inventory.setItem(20, createItemStack(Material.EMERALD_BLOCK, "&aStart", "&7Starts an event with the default description."));

        // Create and set the "End" item
        inventory.setItem(24, createItemStack(Material.REDSTONE_BLOCK, "&cEnd", "&7Closes the active event."));

        // Create and set the "Reload" item
        inventory.setItem(31, createItemStack(Material.GRINDSTONE, "&dReload", "&7Reloads the configuration files."));
    }

    /**
     * Sets the player items.
     * <p>
     * This function is responsible for setting the items for the player.
     * It creates an item stack with the material EMERALD and sets it
     * in the inventory slot 22. The item stack is created with the name
     * "Join" in green color and the lore "Click to join" in gray color.
     */
    private void setPlayerItems() {
        inventory.setItem(22, createItemStack(Material.EMERALD, "&aJoin", "&7Click to join."));
    }
}
