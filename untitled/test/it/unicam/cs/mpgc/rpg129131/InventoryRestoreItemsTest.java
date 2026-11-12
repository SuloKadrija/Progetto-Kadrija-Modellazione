package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la sostituzione dell’inventario, le copie delle liste e i controlli
 * che preservano il contenuto precedente in caso di dati non validi.
 */
class InventoryRestoreItemsTest {

    /**
     * Verifica che il ripristino sostituisca gli oggetti precedenti invece di aggiungerli.
     */
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

    /**
     * Verifica che una lista vuota svuoti correttamente l’inventario.
     */
    @Test
    void acceptsEmptyInventory() {
        Inventory inventory = new Inventory();
        inventory.addItem(new Potion());

        inventory.restoreItems(List.of());

        assertTrue(inventory.getItems().isEmpty());
    }

    /**
     * Verifica il rifiuto della lista null preservando gli oggetti già presenti.
     */
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

    /**
     * Verifica che un elemento null sia individuato prima di svuotare l’inventario.
     */
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

    /**
     * Verifica che modificare la lista passata dopo il ripristino non modifichi l’inventario.
     */
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

    /**
     * Verifica che il ripristino funzioni anche ricevendo la vista dello stesso inventario.
     */
    @Test
    void canRestoreFromItsOwnItemsView() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion();
        inventory.addItem(potion);

        inventory.restoreItems(inventory.getItems());

        assertEquals(List.of(potion), inventory.getItems());
    }
}
