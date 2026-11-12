package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("L'oggetto non può essere null");
        }
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public void restoreItems(List<Item> savedItems) {
        if (savedItems == null) {
            throw new IllegalArgumentException("La lista degli oggetti salvati non può essere null.");
        }

        List<Item> restoredItems = new ArrayList<>(savedItems);

        for (Item item : restoredItems) {
            if (item == null) {
                throw new IllegalArgumentException("La lista degli oggetti salvati " + "non può contenere null.");
            }
        }

        items.clear();
        items.addAll(restoredItems);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }
}
