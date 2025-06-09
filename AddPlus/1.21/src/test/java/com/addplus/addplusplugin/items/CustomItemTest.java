package com.addplus.addplusplugin.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomItemTest {

    @Test
    void testCustomItemCreation_validMaterial() {
        CustomItem item = new CustomItem("test_id", "DIAMOND_SWORD", "Test Sword", Arrays.asList("Line 1", "Line 2"));
        assertEquals("test_id", item.getId());
        assertEquals("DIAMOND_SWORD", item.getMaterialName());
        assertEquals(Material.DIAMOND_SWORD, item.getMaterial());
        assertEquals("Test Sword", item.getDisplayName());
        assertEquals(2, item.getLore().size());
    }

    @Test
    void testCustomItemCreation_invalidMaterial() {
        CustomItem item = new CustomItem("test_invalid", "INVALID_MATERIAL_NAME", "Invalid Item", Collections.emptyList());
        assertEquals("INVALID_MATERIAL_NAME", item.getMaterialName());
        assertEquals(Material.STONE, item.getMaterial(), "Should fallback to STONE for invalid material names.");
    }

    @Test
    void testCustomItemCreation_materialCaseInsensitive() {
        CustomItem item = new CustomItem("test_case", "diamond_pickaxe", "Test Pick", Collections.emptyList());
        assertEquals(Material.DIAMOND_PICKAXE, item.getMaterial(), "Material name should be case insensitive.");
    }


    @Test
    void testCreateItemStack_basic() {
        CustomItem customItem = new CustomItem("basic_stone", "STONE", "&aBasic Stone", Collections.singletonList("&7Just a rock"));
        ItemStack itemStack = customItem.createItemStack(1);

        assertNotNull(itemStack);
        assertEquals(Material.STONE, itemStack.getType());
        assertEquals(1, itemStack.getAmount());
        assertTrue(itemStack.hasItemMeta());

        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasDisplayName());
        assertEquals("§aBasic Stone", meta.getDisplayName()); // Note: ChatColor translates '&' to '§'
        assertTrue(meta.hasLore());
        assertEquals(1, meta.getLore().size());
        assertEquals("§7Just a rock", meta.getLore().get(0));
    }

    @Test
    void testCreateItemStack_noDisplayName() {
        CustomItem customItem = new CustomItem("nameless", "DIRT", null, Collections.emptyList());
        ItemStack itemStack = customItem.createItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertFalse(meta.hasDisplayName()); // Bukkit behavior: no display name if null/empty
    }

    @Test
    void testCreateItemStack_emptyDisplayName() {
        CustomItem customItem = new CustomItem("emptyname", "GRASS_BLOCK", "", Collections.emptyList());
        ItemStack itemStack = customItem.createItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertFalse(meta.hasDisplayName()); // Bukkit behavior: no display name if null/empty
    }

    @Test
    void testCreateItemStack_noLore() {
        CustomItem customItem = new CustomItem("loreless", "SAND", "Sandy", null);
        ItemStack itemStack = customItem.createItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertFalse(meta.hasLore());
    }

    @Test
    void testCreateItemStack_emptyLoreList() {
        CustomItem customItem = new CustomItem("emptylore", "GRAVEL", "Gravy", Collections.emptyList());
        ItemStack itemStack = customItem.createItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        assertNotNull(meta);
        assertFalse(meta.hasLore()); // Bukkit behavior: setting empty list results in no lore
    }

    @Test
    void testCreateItemStack_amount() {
        CustomItem customItem = new CustomItem("multi_item", "COBBLESTONE", "Cobble", Collections.emptyList());
        ItemStack itemStack = customItem.createItemStack(16);
        assertEquals(16, itemStack.getAmount());
    }
}
