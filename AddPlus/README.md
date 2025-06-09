# AddPlus - Custom Item Adder for Minecraft

**AddPlus** is an open-source Minecraft plugin for PaperMC (and Spigot) servers, designed to provide a robust, user-friendly, and feature-rich solution for adding custom items with unique models, textures, and attributes.

**Supported Minecraft Versions:** 1.17 - 1.21.5 (Initial focus and active development on 1.21.x)

## Features (Version 1.21.x - Core Implementation)

*   **Custom Item Definitions:** Define items via simple YAML configuration files.
*   **Resource Pack Integration:** Full support for custom textures and models using server-side resource packs and Minecraft's `CustomModelData` system.
*   **In-Game Commands:**
    *   `/addplus help`: Displays help information.
    *   `/addplus give <player> <item_id> [amount]`: Gives a custom item to a player.
    *   `/addplus reload`: Hot-reloads item configurations without a server restart.
    *   `/addplus list [page]`: Shows a paginated list of all registered custom items.
    *   `/addplus gui`: Opens an in-game GUI to browse and retrieve custom items.
*   **Permissions:** Fine-grained control over command usage (`addplus.give`, `addplus.reload`, `addplus.list`, `addplus.gui`).
*   **Open Source:** Licensed under the MIT License.

## Installation (for Minecraft 1.21.x)

1.  **Download:** Grab the latest `AddPlus-1.21-X.Y.Z.jar` from the [Releases page](https://github.com/your-repo/AddPlus/releases) (link to be updated when repo is public). For now, it's built in `AddPlus/1.21/target/`.
2.  **Server:** Ensure you have a PaperMC server running Minecraft 1.21.x. Spigot 1.21.x may also work but PaperMC is recommended.
3.  **Plugins Folder:** Place the downloaded `AddPlus-1.21-...jar` file into your server's `plugins/` directory.
4.  **Resource Pack (Optional but Recommended):**
    *   If you plan to use custom models, you'll need to set up a server resource pack. A `sample-resource-pack/` directory is included in this repository to guide you.
    *   Upload your prepared resource pack to a hosting service (e.g., Dropbox, a web server) and configure the link in your `server.properties` file (`resource-pack=YOUR_URL_HERE`).
5.  **Start/Restart Server:** Start or restart your server. AddPlus will generate its configuration folder at `plugins/AddPlus/`.

## Basic Usage

1.  **Define Custom Items:**
    *   Navigate to the `plugins/AddPlus/items/` directory on your server.
    *   Create `.yml` files here to define your items (e.g., `magic_wands.yml`, `custom_swords.yml`).
    *   An `example_item.yml` is created by default with `my_custom_sword`.
    *   Refer to the [User Guide](docs/UserGuide.md) and [Modeler Guide](docs/ModelerGuide.md) for details on item syntax and resource pack setup.
    *   **Example (`plugins/AddPlus/items/my_items.yml`):**
        ```yaml
        healing_staff:
          material: BLAZE_ROD # Base Minecraft item
          display-name: "&aStaff of Healing"
          lore:
            - "&7Heals the wielder slightly."
          custom-model-data: 10010 # Links to model in your resource pack

        power_gem:
          material: EMERALD
          display-name: "&cGem of Power"
          custom-model-data: 10011
        ```

2.  **Reload Configuration:**
    *   After adding or modifying item files, use the command `/addplus reload` in-game (requires `addplus.reload` permission or OP).

3.  **Get Items:**
    *   Use `/addplus give YourPlayerName healing_staff` (requires `addplus.give` or OP).
    *   Or use `/addplus gui` to browse and click items (requires `addplus.gui` or OP).

4.  **List Items:**
    *   Use `/addplus list` to see all loaded custom items.

## Documentation

Detailed guides are available in the `docs/` directory:
*   **[User Guide](docs/UserGuide.md):** Installation, configuration, commands, permissions.
*   **[Modeler Guide](docs/ModelerGuide.md):** Creating custom models and resource packs.
*   **[Developer Guide](docs/DeveloperGuide.md):** Codebase overview, contributing.

## Contributing

Contributions are welcome! Whether it's reporting bugs, suggesting features, writing documentation, or submitting code, your help is appreciated.

1.  **Issues:** Check the [Issues tab](https://github.com/your-repo/AddPlus/issues) for existing bugs or feature requests. Feel free to open a new issue.
2.  **Fork & Branch:** Fork the repository and create a new branch for your changes (e.g., `feature/new-command` or `fix/item-loading-bug`).
3.  **Develop:** Make your changes. Adhere to the existing code style. If adding new features, include relevant documentation and unit tests where possible.
4.  **Test:** Test your changes thoroughly on a local server. Ensure existing unit tests pass (`mvn test` in the relevant module directory).
5.  **Pull Request:** Submit a pull request to the main branch of this repository with a clear description of your changes.

## License

AddPlus is open-source and released under the [MIT License](LICENSE.txt). (Note: LICENSE.txt file to be added).

---
*This README provides an overview. For detailed information, please consult the respective guides in the `/docs` folder.*
