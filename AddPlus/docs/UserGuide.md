# AddPlus User Guide

Welcome to AddPlus! This guide will help you install, configure, and use the AddPlus plugin on your PaperMC server.

## 1. Installation

1.  **Download:** Obtain the latest `AddPlus-1.21.jar` file (or the JAR specific to your Minecraft version when available).
2.  **Server Setup:** Ensure you are running a PaperMC server (or Spigot, though PaperMC is recommended) compatible with the plugin version (e.g., Minecraft 1.21.x for AddPlus-1.21.jar).
3.  **Add to Plugins Folder:** Place the downloaded `.jar` file into your server's `plugins/` directory.
4.  **Start/Restart Server:** Start or restart your Minecraft server. AddPlus will generate its default configuration folder at `plugins/AddPlus/`.

## 2. Configuration

AddPlus primarily uses YAML files for defining custom items.

### Item Definition Files

*   **Location:** Custom item definitions are stored in `.yml` files within the `plugins/AddPlus/items/` directory. You can create multiple `.yml` files here (e.g., `swords.yml`, `tools.yml`, `magic_items.yml`).
*   **Structure:** Each item within a YAML file is defined by a unique ID, followed by its properties.

    **Example (`plugins/AddPlus/items/example_item.yml`):**
    ```yaml
    my_custom_sword:
      material: DIAMOND_SWORD
      display-name: "&bMy Awesome Sword" # Uses standard Minecraft color codes with '&'
      lore:
        - "&7This is a very cool sword."
        - "&eForged in the fires of AI."
      # More properties like custom-model-data, enchantments, attributes will be documented as implemented.
      # Example for a custom model (requires a corresponding resource pack model):
      # custom-model-data: 10001

    another_item:
      material: APPLE
      display-name: "&cMagic Apple"
      lore:
        - "&5A juicy, enchanted apple."
      # custom-model-data: 10002
    ```

*   **Key Properties (Initial):**
    *   `material`: The base Minecraft material for the item (e.g., `DIAMOND_SWORD`, `STONE`, `APPLE`). Use valid Spigot Material enum names.
    *   `display-name`: The custom name of the item. Supports color codes using `&`.
    *   `lore`: A list of strings for the item's lore. Supports color codes.
    *   `custom-model-data` (Optional): An integer value that links this item to a custom model in a resource pack. (See Modeler Guide).

### Main Configuration (`plugins/AddPlus/config.yml`)
*(Currently, AddPlus does not have a main `config.yml`. Settings like items per page for commands/GUI are hardcoded but may become configurable in the future.)*

## 3. Commands

All AddPlus commands start with `/addplus` (or alias `/ap`).

*   **/addplus help**
    *   Description: Shows the help message with a list of available commands.
    *   Usage: `/addplus help`
    *   Permission: None (or `addplus.help` if explicitly set).

*   **/addplus give <player> <item_id> [amount]**
    *   Description: Gives a specified custom item to a player.
    *   Usage: `/addplus give Notch my_custom_sword 1`
    *   Permission: `addplus.give` (default: op)
    *   Arguments:
        *   `<player>`: The name of the player to receive the item.
        *   `<item_id>`: The unique ID of the custom item (as defined in your item YAML files).
        *   `[amount]` (Optional): The number of items to give. Defaults to 1.

*   **/addplus reload**
    *   Description: Reloads all item configurations from the `items/` folder. Useful for applying changes without restarting the server.
    *   Usage: `/addplus reload`
    *   Permission: `addplus.reload` (default: op)

*   **/addplus list [page]**
    *   Description: Displays a paginated list of all registered custom items.
    *   Usage: `/addplus list` or `/addplus list 2`
    *   Permission: `addplus.list` (default: op)
    *   Arguments:
        *   `[page]` (Optional): The page number to display.

*   **/addplus gui**
    *   Description: Opens a graphical user interface (GUI) to browse and obtain custom items. Clicking an item in the GUI gives one of that item to the player.
    *   Usage: `/addplus gui`
    *   Permission: `addplus.gui` (default: op)

## 4. Permissions

*   `addplus.give`: Allows use of the `/addplus give` command. (Default: op)
*   `addplus.reload`: Allows use of the `/addplus reload` command. (Default: op)
*   `addplus.list`: Allows use of the `/addplus list` command. (Default: op)
*   `addplus.gui`: Allows use of the `/addplus gui` command. (Default: op)
*   *(`addplus.use` is a placeholder and might be used for general plugin interaction features in the future).*

Operators (OPs) have these permissions by default. You can use a permissions plugin (e.g., LuckPerms) to grant these permissions to other players or groups.
