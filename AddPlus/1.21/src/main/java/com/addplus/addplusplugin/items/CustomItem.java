package com.addplus.addplusplugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class CustomItem {

    private final String id;
    private final String materialName;
    private final String displayName;
    private final List<String> lore;
    // More fields to be added: custom model data, enchantments, attributes, etc.

    public CustomItem(String id, String materialName, String displayName, List<String> lore) {
        this.id = id;
        this.materialName = materialName;
        this.displayName = displayName;
        this.lore = lore;
    }

    public String getId() {
        return id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public Material getMaterial() {
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback material or log error
            return Material.STONE;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack createItemStack(int amount) {
        ItemStack itemStack = new ItemStack(getMaterial(), amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            if (displayName != null && !displayName.isEmpty()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore.stream()
                                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                                .collect(Collectors.toList()));
            }
            // Example for custom model data - to be properly implemented later
            // if (this.customModelData > 0) {
            //     meta.setCustomModelData(this.customModelData);
            // }

            // Optional: Add ItemFlags, e.g. to hide attributes
            // meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
