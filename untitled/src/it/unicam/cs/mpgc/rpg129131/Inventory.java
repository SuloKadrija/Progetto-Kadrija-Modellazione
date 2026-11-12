package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conserva gli oggetti di un eroe nel loro ordine di inserimento.
 * Permette aggiunta, rimozione e ripristino degli oggetti senza esporre
 * una lista direttamente modificabile ai chiamanti.
 */
public class Inventory {
    /**
     * Lista interna modificabile degli oggetti posseduti.
     */
    private final List<Item> items;

    /**
     * Crea un inventario inizialmente vuoto.
     */
    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Aggiunge un oggetto in fondo all’inventario.
     * Non esegue controlli sui duplicati.
     *
     * @param item oggetto da aggiungere
     * @throws IllegalArgumentException se l’oggetto è null
     */
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("L'oggetto non può essere null");
        }
        items.add(item);
    }

    /**
     * Rimuove la prima occorrenza dell’oggetto, se presente.
     *
     * @param item oggetto da rimuovere
     * @return true se un elemento è stato rimosso
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    /**
     * Sostituisce l’inventario con gli oggetti ricostruiti dal salvataggio.
     * Valida una copia della lista prima di modificare il contenuto attuale.
     *
     * @param savedItems oggetti da ripristinare, anche una lista vuota
     * @throws IllegalArgumentException se la lista o uno dei suoi elementi è null
     */
    public void restoreItems(List<Item> savedItems) {
        if (savedItems == null) {
            throw new IllegalArgumentException("La lista degli oggetti salvati non può essere null.");
        }

        // Copia prima dello svuotamento: savedItems può essere la vista di questo inventario.
        List<Item> restoredItems = new ArrayList<>(savedItems);

        for (Item item : restoredItems) {
            if (item == null) {
                throw new IllegalArgumentException("La lista degli oggetti salvati " + "non può contenere null.");
            }
        }

        items.clear();
        items.addAll(restoredItems);
    }

    /**
     * Restituisce una vista non modificabile della lista degli oggetti.
     * La vista riflette le modifiche successive dell’inventario e non duplica gli oggetti.
     *
     * @return vista degli oggetti nell’ordine corrente
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Verifica se l’oggetto è presente nell’inventario.
     *
     * @param item oggetto cercato
     * @return true se la lista contiene l’oggetto
     */
    public boolean contains(Item item) {
        return items.contains(item);
    }
}
