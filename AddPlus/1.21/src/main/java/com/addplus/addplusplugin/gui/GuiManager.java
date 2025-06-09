package com.addplus.addplusplugin.gui;

import com.addplus.addplusplugin.AddPlusPlugin;
import com.addplus.addplusplugin.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuiManager implements Listener {

    private final AddPlusPlugin plugin;
    private static final String GUI_TITLE_PREFIX = "AddPlus Items - Page ";
    private static final int ITEMS_PER_GUI_PAGE = 45; // 5 rows of 9 slots for items
    private static final int GUI_SIZE = 54; // 6 rows

    // Store current page for each player viewing a GUI
    private final Map<UUID, Integer> playerCurrentPage = new HashMap<>();
    // Store the list of items being browsed by the player (to handle sorting/filtering later if needed)
    private final Map<UUID, List<CustomItem>> playerViewingItems = new HashMap<>();

    public GuiManager(AddPlusPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openPrimaryGui(Player player, int page) {
        List<CustomItem> items = new ArrayList<>(plugin.getItemRegistry().getAllItems());
        if (items.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "There are no custom items to display in the GUI.");
            return;
        }
        Collections.sort(items, (i1, i2) -> i1.getId().compareToIgnoreCase(i2.getId()));
        playerViewingItems.put(player.getUniqueId(), items);
        openPaginatedGui(player, page);
    }

    private void openPaginatedGui(Player player, int page) {
        UUID playerUuid = player.getUniqueId();
        List<CustomItem> items = playerViewingItems.get(playerUuid);

        if (items == null || items.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No items to display.");
            playerCurrentPage.remove(playerUuid); // Clean up
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_GUI_PAGE);
        if (totalPages == 0) totalPages = 1; // Ensure at least one page even if empty after filtering (future)

        page = Math.max(1, Math.min(page, totalPages)); // Clamp page number

        playerCurrentPage.put(playerUuid, page);

        Inventory gui = Bukkit.createInventory(player, GUI_SIZE, GUI_TITLE_PREFIX + page + "/" + totalPages);

        int startIndex = (page - 1) * ITEMS_PER_GUI_PAGE;
        for (int i = 0; i < ITEMS_PER_GUI_PAGE; i++) {
            int itemIndex = startIndex + i;
            if (itemIndex < totalItems) {
                CustomItem customItem = items.get(itemIndex);
                gui.setItem(i, customItem.createItemStack(1));
            } else {
                // Fill empty slots if items are less than ITEMS_PER_GUI_PAGE
                // gui.setItem(i, new ItemStack(Material.AIR)); // Not strictly necessary
            }
        }

        // Navigation items
        if (page > 1) {
            gui.setItem(GUI_SIZE - 9, createNavItem("&cPrevious Page", Material.ARROW)); // Bottom-left
        }
        if (page < totalPages) {
            gui.setItem(GUI_SIZE - 1, createNavItem("&aNext Page", Material.ARROW)); // Bottom-right
        }
        gui.setItem(GUI_SIZE - 5, createNavItem("&eClose GUI", Material.BARRIER)); // Bottom-center

        player.openInventory(gui);
    }

    private ItemStack createNavItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUuid = player.getUniqueId();

        if (!playerCurrentPage.containsKey(playerUuid)) { // Not an AddPlus GUI
            return;
        }

        String title = event.getView().getTitle();
        if (!title.startsWith(GUI_TITLE_PREFIX)) {
             // It might be an AddPlus GUI but something went wrong, or it's another plugin's GUI.
            // If playerCurrentPage contains UUID but title doesn't match, it's safer to remove them.
            if(playerCurrentPage.containsKey(playerUuid)){
                 playerCurrentPage.remove(playerUuid);
                 playerViewingItems.remove(playerUuid);
            }
            return;
        }
        event.setCancelled(true); // Prevent taking items from the GUI directly

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int currentPage = playerCurrentPage.get(playerUuid);

        // Handle navigation clicks
        if (event.getSlot() == GUI_SIZE - 9 && currentPage > 1) { // Previous Page
            openPaginatedGui(player, currentPage - 1);
            return;
        }
        if (event.getSlot() == GUI_SIZE - 1) { // Next Page (check if exists done by openPaginatedGui)
            List<CustomItem> items = playerViewingItems.get(playerUuid);
            int totalItems = items.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_GUI_PAGE);
             if (totalPages == 0) totalPages = 1;
            if(currentPage < totalPages){
                openPaginatedGui(player, currentPage + 1);
            }
            return;
        }
        if (event.getSlot() == GUI_SIZE - 5) { // Close GUI
            player.closeInventory();
            return;
        }

        // Handle item click (give item to player)
        // Ensure the click is in the item display area (0 to ITEMS_PER_GUI_PAGE - 1)
        if (event.getSlot() < ITEMS_PER_GUI_PAGE) {
            List<CustomItem> items = playerViewingItems.get(playerUuid);
            int itemIndex = (currentPage - 1) * ITEMS_PER_GUI_PAGE + event.getSlot();
            if (itemIndex < items.size()) {
                CustomItem customItem = items.get(itemIndex);
                ItemStack toGive = customItem.createItemStack(1);
                if (!player.getInventory().addItem(toGive).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), toGive);
                    player.sendMessage(ChatColor.YELLOW + "Your inventory was full, so " + customItem.getDisplayName() + ChatColor.YELLOW + " was dropped nearby.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "You received 1x " + customItem.getDisplayName() + ChatColor.GREEN + "!");
                }
                // player.closeInventory(); // Optional: close GUI after giving item
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        // Check if the closed inventory was one of ours by title, just in case.
        // This helps ensure we only remove player data if they were indeed using our GUI.
        if (event.getView().getTitle().startsWith(GUI_TITLE_PREFIX)) {
            playerCurrentPage.remove(playerUuid);
            playerViewingItems.remove(playerUuid);
        }
    }
}
