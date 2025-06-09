# AddPlus Modeler Guide

This guide explains how to create and integrate custom models for items using AddPlus, leveraging Minecraft's resource pack system. AddPlus aims for compatibility with common modeling practices, similar to plugins like ItemAdder.

## 1. Resource Pack Setup

Custom item models in Minecraft require a server-side resource pack that players will download and use.

*   **Standard Structure:** Your resource pack should follow the standard Minecraft resource pack structure.
    ```
    YourResourcePack/
    ├── pack.mcmeta
    └── assets/
        └── minecraft/  (or your own namespace, e.g., "addplus")
            ├── models/
            │   ├── item/          (for items that directly replace vanilla items or simple custom items)
            │   └── custom/        (a common convention for custom item models)
            │       └── example_sword.json
            │       └── magic_apple.json
            ├── textures/
            │   ├── item/
            │   └── custom/
            │       └── example_sword_texture.png
            │       └── magic_apple_texture.png
            └── ... (sounds.json, etc.)
    ```

*   **`pack.mcmeta`:** This file describes your resource pack.
    ```json
    {
      "pack": {
        "pack_format": 15, // For Minecraft 1.20.1 (Adjust for your target MC version)
                         // 1.21.x might use a higher pack_format, check Minecraft Wiki
        "description": "Custom models for My Server using AddPlus"
      }
    }
    ```
    *Note: Check the Minecraft Wiki for the correct `pack_format` for your target Minecraft version (e.g., 1.21.x).*

## 2. Creating Item Models (`.json` files)

Minecraft item models are defined in JSON format. These models determine the item's appearance in hand, in inventory, and on the ground.

*   **Location:** Place your model JSON files typically under `assets/minecraft/models/item/` or a custom path like `assets/minecraft/models/custom/your_item_name.json`. If using a custom namespace, it would be `assets/your_namespace/models/item/your_item_name.json`.

*   **Basic Custom Model (using `custom_model_data`):**
    This is the most common method for custom items that don't replace existing vanilla items. You define a base vanilla item (e.g., `diamond_hoe`) and then use `custom_model_data` to specify different models for it.

    **Example (`assets/minecraft/models/item/diamond_hoe.json` - or any other item you use as a base):**
    ```json
    {
      "parent": "minecraft:item/handheld",
      "textures": {
        "layer0": "minecraft:item/diamond_hoe"
      },
      "overrides": [
        { "predicate": { "custom_model_data": 10001 }, "model": "minecraft:item/custom/example_sword" },
        { "predicate": { "custom_model_data": 10002 }, "model": "minecraft:item/custom/magic_apple_model" }
        // Add more overrides for other custom items based on diamond_hoe
      ]
    }
    ```
    *   In this example, `diamond_hoe` is the base item.
    *   `custom_model_data` is a unique integer you assign to your custom item.
    *   `"model": "minecraft:item/custom/example_sword"` points to another JSON file that defines the actual shape and texture of your custom sword.

*   **Actual Custom Item Model File (`assets/minecraft/models/custom/example_sword.json`):**
    ```json
    {
      "parent": "minecraft:item/handheld", // Or "minecraft:item/generated" for flat items
      "textures": {
        "layer0": "minecraft:item/custom/example_sword_texture" // Path to your texture file (e.g., assets/minecraft/textures/custom/example_sword_texture.png)
      }
      // You can add display transformations here for how it looks in hand, GUI, ground, etc.
      // "display": { ... }
    }
    ```

## 3. Linking Models in AddPlus Item Configuration

In your AddPlus item YAML files (`plugins/AddPlus/items/your_items.yml`), you link an item definition to a custom model using the `custom-model-data` property.

**Example (`plugins/AddPlus/items/my_items.yml`):**
```yaml
my_awesome_sword:
  material: DIAMOND_HOE # The base item used in the override model (diamond_hoe.json)
  display-name: "&bAwesome Sword"
  lore:
    - "&7A truly awesome blade."
  custom-model-data: 10001 # This number MUST match the predicate in diamond_hoe.json

magic_apple_id:
  material: DIAMOND_HOE # Or whatever base item you used for its override
  display-name: "&dMagic Apple"
  custom-model-data: 10002
```

**Explanation:**
1.  The player receives an item of type `DIAMOND_HOE`.
2.  Because it has `CustomModelData: 10001`, the client's resource pack checks `diamond_hoe.json` for an override matching this predicate.
3.  It finds `{"predicate": {"custom_model_data": 10001}, "model": "minecraft:item/custom/example_sword"}`.
4.  The client then renders the item using the `custom/example_sword.json` model.

## 4. Tips and Automation

*   **Tools:** Use tools like Blockbench (free, powerful model editor) to create your `.json` models and textures.
*   **Namespaces:** Consider using your own namespace (e.g., `assets/addplus/models/...`) instead of `assets/minecraft/` to avoid conflicts with vanilla items or other plugins, especially if you are creating many models. If you use a custom namespace, ensure your model paths in the JSON files reflect this (e.g., `"model": "addplus:item/custom/example_sword"`).
*   **Consistency:** Keep your `custom_model_data` values unique across all items that share the same base material model.
*   **Testing:** Test your resource pack thoroughly in-game. Use F3+T to reload resource packs client-side quickly.
*   **ItemAdder Compatibility:** AddPlus aims to use the same fundamental resource pack and `custom_model_data` mechanics as ItemAdder. This means models designed for ItemAdder (that use `custom_model_data` predicates on vanilla items) should generally be compatible with AddPlus if the item definition in AddPlus (`material` and `custom-model-data`) is configured correctly to point to them.

This guide will be expanded with more advanced examples and troubleshooting tips as AddPlus develops.
