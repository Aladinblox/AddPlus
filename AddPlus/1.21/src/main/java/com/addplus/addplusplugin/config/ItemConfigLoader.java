package com.addplus.addplusplugin.config;

import com.addplus.addplusplugin.AddPlusPlugin;
import com.addplus.addplusplugin.items.CustomItem;
import com.addplus.addplusplugin.items.ItemRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ItemConfigLoader {

    private final AddPlusPlugin plugin;
    private final ItemRegistry itemRegistry;
    private final File itemsFolder;

    public ItemConfigLoader(AddPlusPlugin plugin, ItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
        this.itemsFolder = new File(plugin.getDataFolder(), "items");
    }

    public void loadItems() {
        if (!itemsFolder.exists()) {
            if (itemsFolder.mkdirs()) {
                plugin.getLogger().info("Created items folder: " + itemsFolder.getPath());
                // Maybe save a default example item here
                createDefaultItemConfig();
            } else {
                plugin.getLogger().severe("Could not create items folder: " + itemsFolder.getPath());
                return;
            }
        }

        File[] itemFiles = itemsFolder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));

        if (itemFiles == null || itemFiles.length == 0) {
            plugin.getLogger().info("No item configuration files found in " + itemsFolder.getPath() + ". Creating a default example.");
            createDefaultItemConfig(); // Create example if no files are found
            itemFiles = itemsFolder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
            if (itemFiles == null) { // Check again if creation failed
                 plugin.getLogger().severe("Still no item files found after attempting to create default.");
                 return;
            }
        }

        itemRegistry.clearItems(); // Clear previous items before loading new ones

        for (File itemFile : itemFiles) {
            FileConfiguration itemConfig = YamlConfiguration.loadConfiguration(itemFile);
            Set<String> itemIds = itemConfig.getKeys(false);

            for (String itemId : itemIds) {
                ConfigurationSection itemSection = itemConfig.getConfigurationSection(itemId);
                if (itemSection == null) {
                    plugin.getLogger().warning("Item section for ID '" + itemId + "' in file " + itemFile.getName() + " is null. Skipping.");
                    continue;
                }

                String material = itemSection.getString("material");
                String displayName = itemSection.getString("display-name", "Custom Item"); // Default display name
                List<String> lore = itemSection.getStringList("lore");

                if (material == null || material.isEmpty()) {
                    plugin.getLogger().warning("Material for item ID '" + itemId + "' in file " + itemFile.getName() + " is missing or empty. Skipping.");
                    continue;
                }

                // For now, CustomItem only takes id, material, display name, and lore.
                // More attributes (model data, enchantments, etc.) will be added later.
                CustomItem customItem = new CustomItem(itemId, material, displayName, lore);
                itemRegistry.registerItem(customItem);
                plugin.getLogger().info("Loaded item: " + itemId + " from " + itemFile.getName());
            }
        }
        plugin.getLogger().info("Finished loading " + itemRegistry.getAllItems().size() + " items.");
    }

    private void createDefaultItemConfig() {
        File defaultItemFile = new File(itemsFolder, "example_item.yml");
        if (!defaultItemFile.exists()) {
            try {
                if (defaultItemFile.createNewFile()) {
                    plugin.getLogger().info("Created default item config: " + defaultItemFile.getPath());
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultItemFile);
                    defaultConfig.set("my_custom_sword.material", "DIAMOND_SWORD");
                    defaultConfig.set("my_custom_sword.display-name", "&bMy Awesome Sword");
                    List<String> lore = new ArrayList<>();
                    lore.add("&7This is a very cool sword.");
                    lore.add("&eForged in the fires of AI.");
                    defaultConfig.set("my_custom_sword.lore", lore);
                    defaultConfig.save(defaultItemFile);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create default item config: " + defaultItemFile.getPath(), e);
            }
        }
    }
}
