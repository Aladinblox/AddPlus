package com.addplus.addplusplugin;

import com.addplus.addplusplugin.commands.CommandManager;
import com.addplus.addplusplugin.config.ItemConfigLoader;
import com.addplus.addplusplugin.gui.GuiManager;
import com.addplus.addplusplugin.items.ItemRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class AddPlusPlugin extends JavaPlugin {

    private ItemRegistry itemRegistry;
    private ItemConfigLoader itemConfigLoader;
    private CommandManager commandManager;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        getLogger().info("AddPlus plugin enabling for version " + getServer().getBukkitVersion() + "...");

        // Initialize components
        this.itemRegistry = new ItemRegistry();
        this.itemConfigLoader = new ItemConfigLoader(this, this.itemRegistry);
        this.commandManager = new CommandManager(this);
        this.guiManager = new GuiManager(this); // Initialize GuiManager

        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            if(!getDataFolder().mkdirs()){
                getLogger().severe("Could not create plugin data folder!");
            }
        }

        // Load custom items
        this.itemConfigLoader.loadItems();
        checkServerVersion();

        // Register commands
        Objects.requireNonNull(getCommand("addplus"), "Command 'addplus' is not defined in plugin.yml!")
            .setExecutor(commandManager);
        Objects.requireNonNull(getCommand("addplus"), "Command 'addplus' is not defined in plugin.yml!")
            .setTabCompleter(commandManager);

        getLogger().info("AddPlus plugin has been enabled with " + itemRegistry.getAllItems().size() + " custom item(s) loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AddPlus plugin has been disabled.");
        if (itemRegistry != null) {
            itemRegistry.clearItems();
        }
        // GuiManager related cleanup, if any, could go here (e.g., closing open GUIs forcefully)
        // For now, playerCurrentPage in GuiManager will clear on inventory close event or when player logs off.
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public ItemConfigLoader getItemConfigLoader() {
        return itemConfigLoader;
    }

    public GuiManager getGuiManager() { // Getter for GuiManager
        return guiManager;
    }

    private void checkServerVersion() {
        // Basic check against api-version, plugin.yml should handle most of this.
        // This is more of a developer note or an extra safeguard.
        String serverApiVersion = getServer().getBukkitVersion().split("-")[0]; // e.g., "1.21" from "1.21-R0.1-SNAPSHOT"
        String pluginApiVersion = getDescription().getAPIVersion(); // from plugin.yml

        if (pluginApiVersion != null && !serverApiVersion.startsWith(pluginApiVersion)) {
            getLogger().warning("----------------------------------------------------");
            getLogger().warning("AddPlus was built for API version " + pluginApiVersion +
                                ", but server is running " + serverApiVersion + ".");
            getLogger().warning("While it may work, full compatibility is not guaranteed.");
            getLogger().warning("Consider using the AddPlus version built for your server's Minecraft version.");
            getLogger().warning("----------------------------------------------------");
        }
    }
}
