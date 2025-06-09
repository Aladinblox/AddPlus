# AddPlus Developer Guide

This guide provides an overview of the AddPlus codebase (specifically the 1.21 module) for developers looking to contribute or understand its structure.

## 1. Project Structure (1.21 Module)

The 1.21 module is located under `AddPlus/1.21/`. It's a standard Maven project.

*   **`pom.xml`**: Defines project dependencies (Paper API, JUnit), build configuration, and plugin metadata.
*   **`src/main/java/com/addplus/addplusplugin/`**: Main package for the plugin's Java code.
    *   **`AddPlusPlugin.java`**: The main plugin class, extending `JavaPlugin`. Handles enabling/disabling logic, initializes managers, and registers commands/listeners.
    *   **`config/`**:
        *   `ItemConfigLoader.java`: Responsible for loading and parsing item definition YAML files from the `plugins/AddPlus/items/` directory.
    *   **`items/`**:
        *   `CustomItem.java`: Represents a custom item with its properties (ID, material, display name, lore, custom model data, etc.) and a method to create its `ItemStack`.
        *   `ItemRegistry.java`: A singleton-like registry holding all loaded `CustomItem` instances, accessible by their unique IDs.
    *   **`commands/`**:
        *   `CommandManager.java`: Implements `CommandExecutor` and `TabCompleter`. Handles all `/addplus` subcommands (`help`, `give`, `reload`, `list`, `gui`), permission checks, and tab completion logic.
    *   **`gui/`**:
        *   `GuiManager.java`: Implements `Listener`. Manages the paginated GUI for browsing items. Handles inventory creation, item display, click events (for navigation and giving items), and cleanup.
*   **`src/main/resources/`**:
    *   `plugin.yml`: The plugin description file required by Bukkit/Spigot/Paper. Defines the main class, commands, permissions, API version, etc.
*   **`src/test/java/`**: Contains JUnit tests (currently planned, initial tests to be added).

## 2. Core Components Interaction

1.  **On Enable (`AddPlusPlugin.onEnable()`):**
    *   `ItemRegistry` is initialized.
    *   `ItemConfigLoader` is initialized and `loadItems()` is called to populate the `ItemRegistry`.
    *   `CommandManager` is initialized and registered for the `/addplus` command.
    *   `GuiManager` is initialized (which also registers its event listeners).
2.  **Item Loading (`ItemConfigLoader`):**
    *   Scans the `plugins/AddPlus/items/` directory for `.yml` files.
    *   For each file, parses the YAML structure. Each top-level key is an item ID.
    *   Creates `CustomItem` objects from the parsed data.
    *   Registers each `CustomItem` with the `ItemRegistry`.
3.  **Command Handling (`CommandManager`):**
    *   Receives command invocations.
    *   Delegates to specific methods based on the subcommand (e.g., `handleGiveCommand`).
    *   Interacts with `ItemRegistry` to fetch item details (for `give`, `list`, `gui`).
    *   Interacts with `ItemConfigLoader` for the `reload` command.
    *   Interacts with `GuiManager` for the `gui` command.
4.  **GUI Interaction (`GuiManager`):**
    *   Opens an inventory for the player.
    *   Populates the inventory with `ItemStack`s created from `CustomItem` objects (obtained from `ItemRegistry`).
    *   Listens for `InventoryClickEvent` to handle item giving and page navigation.

## 3. Adding Support for New Minecraft Versions (Conceptual)

While currently focused on 1.21:
*   **New Module:** A new Maven module would be created for the target version (e.g., `AddPlus/1.X.X/`).
*   **API Dependency:** The `pom.xml` of the new module would depend on the specific Paper/Spigot API for that Minecraft version.
*   **Core Logic:** The aim is to keep most logic (item representation, configuration parsing, command structure) as "core" or common. This might involve:
    *   Moving common classes to a shared `addplus-core` module in the future.
    *   Defining interfaces in the core for any version-specific functionalities (e.g., handling NBT, specific material names if mapping is needed, advanced particle/sound APIs). The version-specific modules would then implement these interfaces.
*   **Version Stubs:** Placeholder `README.md` files in each version directory (`AddPlus/1.X/`) outline this intent.

## 4. Extending the Plugin

*   **Custom Events:** (Future) Custom events could be added (e.g., `PreCustomItemGiveEvent`, `CustomItemLoadEvent`) to allow other plugins to interact or modify behavior.
*   **Listeners:** Add new classes implementing `Listener` in a `listeners/` package and register them in `AddPlusPlugin.onEnable()` to respond to more Bukkit events.

This guide will be updated as the plugin evolves.
