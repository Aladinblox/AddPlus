package com.addplus.addplusplugin.items;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemRegistry {

    private final Map<String, CustomItem> registeredItems = new HashMap<>();

    public void registerItem(CustomItem item) {
        if (item == null || item.getId() == null || item.getId().isEmpty()) {
            // Log a warning or throw an exception
            return;
        }
        registeredItems.put(item.getId().toLowerCase(), item);
    }

    public Optional<CustomItem> getItem(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registeredItems.get(id.toLowerCase()));
    }

    public Collection<CustomItem> getAllItems() {
        return registeredItems.values();
    }

    public void clearItems() {
        registeredItems.clear();
    }
}
