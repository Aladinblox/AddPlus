package com.addplus.addplusplugin.config;

import com.addplus.addplusplugin.AddPlusPlugin;
import com.addplus.addplusplugin.items.CustomItem;
import com.addplus.addplusplugin.items.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ItemConfigLoaderTest {

    @TempDir
    File tempPluginDataFolder; // JUnit 5 temporary directory

    private AddPlusPlugin mockPlugin;
    private ItemRegistry itemRegistry;
    private ItemConfigLoader itemConfigLoader;
    private File itemsFolder;
    private Logger mockLogger;
    private Server mockServer;

    @BeforeEach
    void setUp() throws IOException {
        mockPlugin = mock(AddPlusPlugin.class);
        mockLogger = Logger.getLogger("ItemConfigLoaderTestLogger"); // Use a real logger or mock it
        mockServer = mock(Server.class); // Mock server

        // Mock plugin description
        PluginDescriptionFile mockDescription = new PluginDescriptionFile("AddPlus", "0.1", "com.addplus.addplusplugin.AddPlusPlugin");


        when(mockPlugin.getLogger()).thenReturn(mockLogger);
        when(mockPlugin.getDataFolder()).thenReturn(tempPluginDataFolder);
        when(mockPlugin.getServer()).thenReturn(mockServer); // For onEnable version check
        when(mockPlugin.getDescription()).thenReturn(mockDescription); // For onEnable version check


        itemsFolder = new File(tempPluginDataFolder, "items");
        if (!itemsFolder.mkdirs()) {
            throw new IOException("Could not create temp items folder: " + itemsFolder.getAbsolutePath());
        }

        itemRegistry = new ItemRegistry();
        itemConfigLoader = new ItemConfigLoader(mockPlugin, itemRegistry);
    }

    private File createTestItemFile(String fileName, String content) throws IOException {
        File itemFile = new File(itemsFolder, fileName);
        try (FileWriter writer = new FileWriter(itemFile)) {
            writer.write(content);
        }
        return itemFile;
    }

    @Test
    void testLoadItems_validItem() throws IOException {
        String yamlContent =
            "test_sword:\n" +
            "  material: DIAMOND_SWORD\n" +
            "  display-name: \"&bTest Sword\"\n" +
            "  lore:\n" +
            "    - \"&7A fine sword.\"\n";
        createTestItemFile("valid_item.yml", yamlContent);

        itemConfigLoader.loadItems();

        assertEquals(1, itemRegistry.getAllItems().size());
        Optional<CustomItem> itemOpt = itemRegistry.getItem("test_sword");
        assertTrue(itemOpt.isPresent());
        CustomItem item = itemOpt.get();
        assertEquals("test_sword", item.getId());
        assertEquals(Material.DIAMOND_SWORD, item.getMaterial());
        assertEquals("&bTest Sword", item.getDisplayName());
        assertEquals(1, item.getLore().size());
        assertEquals("&7A fine sword.", item.getLore().get(0));
    }

    @Test
    void testLoadItems_emptyFileCreatesDefault() {
        // No files created, loader should create example_item.yml
        itemConfigLoader.loadItems();

        // Check if the default item was "loaded" (i.e., registry has it after creation)
        assertTrue(itemRegistry.getItem("my_custom_sword").isPresent(), "Default item should be created and loaded.");
        assertEquals(1, itemRegistry.getAllItems().size());

        File defaultFile = new File(itemsFolder, "example_item.yml");
        assertTrue(defaultFile.exists(), "Default item file should be created.");
    }

    @Test
    void testLoadItems_noFilesAtAll() {
        // Ensure items folder is empty
        for (File file : itemsFolder.listFiles()) {
            file.delete();
        }

        itemConfigLoader.loadItems(); // Should create and load the default item

        assertEquals(1, itemRegistry.getAllItems().size(), "Registry should contain one default item.");
        assertTrue(itemRegistry.getItem("my_custom_sword").isPresent(), "Default item 'my_custom_sword' should be loaded.");
    }


    @Test
    void testLoadItems_itemMissingMaterial() throws IOException {
        String yamlContent =
            "missing_material_item:\n" +
            "  display-name: \"&cNo Material\"\n";
        createTestItemFile("missing_material.yml", yamlContent);

        itemConfigLoader.loadItems();
        // Also loads default example item
        assertEquals(1, itemRegistry.getAllItems().size(), "Only default item should be loaded if other is invalid.");
        assertFalse(itemRegistry.getItem("missing_material_item").isPresent());
    }

    @Test
    void testLoadItems_itemInvalidMaterial() throws IOException {
        String yamlContent =
            "invalid_material_item:\n" +
            "  material: THIS_IS_NOT_A_MATERIAL\n" +
            "  display-name: \"&cInvalid Material\"\n";
        createTestItemFile("invalid_material.yml", yamlContent);

        itemConfigLoader.loadItems();

        Optional<CustomItem> itemOpt = itemRegistry.getItem("invalid_material_item");
        // The item will be loaded, but its getMaterial() will return STONE (fallback)
        assertTrue(itemOpt.isPresent());
        assertEquals(Material.STONE, itemOpt.get().getMaterial());
        // Total items: this one + default one
        assertEquals(2, itemRegistry.getAllItems().size());
    }

    @Test
    void testLoadItems_multipleItemsInOneFile() throws IOException {
        String yamlContent =
            "item_one:\n" +
            "  material: IRON_INGOT\n" +
            "  display-name: \"First Item\"\n" +
            "item_two:\n" +
            "  material: GOLD_INGOT\n" +
            "  display-name: \"Second Item\"\n";
        createTestItemFile("multiple_items.yml", yamlContent);

        itemConfigLoader.loadItems();
        // multiple_items.yml (2) + default_item.yml (1)
        assertEquals(3, itemRegistry.getAllItems().size());
        assertTrue(itemRegistry.getItem("item_one").isPresent());
        assertTrue(itemRegistry.getItem("item_two").isPresent());
    }

    @Test
    void testLoadItems_multipleFiles() throws IOException {
        String yamlContent1 =
            "item_alpha:\n" +
            "  material: APPLE\n";
        createTestItemFile("file1.yml", yamlContent1);

        String yamlContent2 =
            "item_beta:\n" +
            "  material: BREAD\n";
        createTestItemFile("file2.yml", yamlContent2);

        itemConfigLoader.loadItems();
        // file1.yml (1) + file2.yml (1) + default_item.yml (1)
        assertEquals(3, itemRegistry.getAllItems().size());
        assertTrue(itemRegistry.getItem("item_alpha").isPresent());
        assertTrue(itemRegistry.getItem("item_beta").isPresent());
    }

    @Test
    void testLoadItems_reloadClearsOldItems() throws IOException {
        String yamlContent1 = "first_item: { material: DIRT }";
        createTestItemFile("first.yml", yamlContent1);
        itemConfigLoader.loadItems(); // Loads first_item + default item

        assertEquals(2, itemRegistry.getAllItems().size());
        assertTrue(itemRegistry.getItem("first_item").isPresent());

        // Delete first.yml and create second.yml
        new File(itemsFolder, "first.yml").delete();
        String yamlContent2 = "second_item: { material: STONE }";
        createTestItemFile("second.yml", yamlContent2);

        itemConfigLoader.loadItems(); // Should clear first_item, load second_item + default item (if not already there or re-created)
                                      // Default item is always checked/created if items folder is empty or no files were loaded.
                                      // In this case, second.yml is present, so default is not re-triggered from empty.
                                      // However, the default example_item.yml is still on disk from the first load.
                                      // So it will load second_item + the existing example_item.yml's content.

        assertEquals(2, itemRegistry.getAllItems().size());
        assertFalse(itemRegistry.getItem("first_item").isPresent(), "first_item should be cleared on reload");
        assertTrue(itemRegistry.getItem("second_item").isPresent(), "second_item should be loaded");
        assertTrue(itemRegistry.getItem("my_custom_sword").isPresent(), "Default item should persist if its file exists");
    }
}
