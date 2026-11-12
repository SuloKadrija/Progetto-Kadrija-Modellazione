package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRestoreItemsTest {

    @Test
    void replacesPreviousItemsWithSavedItems() {
        Inventory inventory = new Inventory();
        Potion previousPotion = new Potion();
        Potion savedPotion = new Potion();

        inventory.addItem(previousPotion);

        inventory.restoreItems(List.of(savedPotion));

        assertEquals(List.of(savedPotion), inventory.getItems());
        assertFalse(inventory.contains(previousPotion));
    }

    @Test
    void acceptsEmptyInventory() {
        Inventory inventory = new Inventory();
        inventory.addItem(new Potion());

        inventory.restoreItems(List.of());

        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void rejectsNullListWithoutChangingInventory() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion();
        inventory.addItem(potion);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.restoreItems(null)
        );

        assertEquals(List.of(potion), inventory.getItems());
    }

    @Test
    void rejectsNullItemWithoutChangingInventory() {
        Inventory inventory = new Inventory();
        Potion previousPotion = new Potion();
        inventory.addItem(previousPotion);

        List<Item> savedItems = new ArrayList<>();
        savedItems.add(new Potion());
        savedItems.add(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.restoreItems(savedItems)
        );

        assertEquals(
                List.of(previousPotion),
                inventory.getItems()
        );
    }

    @Test
    void doesNotKeepReferenceToInputList() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion();

        List<Item> savedItems = new ArrayList<>();
        savedItems.add(potion);

        inventory.restoreItems(savedItems);
        savedItems.clear();

        assertEquals(List.of(potion), inventory.getItems());
    }

    @Test
    void canRestoreFromItsOwnItemsView() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion();
        inventory.addItem(potion);

        inventory.restoreItems(inventory.getItems());

        assertEquals(List.of(potion), inventory.getItems());
    }
}