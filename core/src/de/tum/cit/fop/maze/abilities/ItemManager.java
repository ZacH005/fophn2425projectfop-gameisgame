package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Entity;
import de.tum.cit.fop.maze.entity.EntityUtils;
import de.tum.cit.fop.maze.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemManager {
    private final List<Item> allItems;
    private Set<Item> unlockedItems;

    public ItemManager() {
        this.allItems = new ArrayList<>();
        this.unlockedItems = new HashSet<>();
    }

    public void addItem(Item item) {
        //,maybe should save all items to some file, initialization only needs (String name, String description, String texturePath); i lied every item will be its own class
        allItems.add(item);
    }

    public Set<Item> getUnlockedItems() {
        for (Item item : allItems) {
            if (item.isUnlocked()) {
                unlockedItems.add(item);
            }
        }
        return unlockedItems;
    }

    public void unlockItem(String itemName) {
        for (Item item : allItems) {
            if (item.getName().equals(itemName)) {
                item.unlock();
                unlockedItems.add(item);
                break;
            }
        }
    }
}

