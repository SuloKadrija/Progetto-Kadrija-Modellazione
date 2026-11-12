package it.unicam.cs.mpgc.rpg129131;

/**
 * Rappresenta un eroe con inventario personale e stato di sblocco.
 * Eredita statistiche e gestione della salute da Entity; la composizione dei gruppi
 * e il momento dello sblocco sono gestiti dalle altre classi del gioco.
 */
public abstract class Hero extends Entity {

    /**
     * Indica se l’eroe è stato sbloccato dalla progressione.
     */
    private boolean unlocked;
    /**
     * Inventario personale creato insieme all’eroe.
     */
    private final Inventory inventory;

    /**
     * Crea un eroe con statistiche, sblocco iniziale e inventario vuoto.
     *
     * @param name nome dell’eroe
     * @param health salute iniziale e massima
     * @param attack attacco dell’eroe
     * @param defense difesa dell’eroe
     * @param unlocked stato iniziale di sblocco
     * @throws IllegalArgumentException se le statistiche non rispettano i vincoli di Entity
     */
    protected Hero(String name, int health, int attack, int defense, boolean unlocked) {
        super(name, health, attack, defense);

        this.unlocked = unlocked;
        this.inventory = new Inventory();
    }

    /**
     * Indica se l’eroe è stato sbloccato.
     *
     * @return stato di sblocco
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Imposta lo stato di sblocco a true; chiamate ripetute non cambiano il risultato.
     */
    public void unlock() {
        unlocked = true;
    }

    /**
     * Restituisce l’inventario personale dell’eroe.
     *
     * @return inventario associato a questa istanza
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Riporta la salute al massimo, ad esempio dopo un incontro vinto.
     * Non va confuso con restoreHealth(int), ereditato da Entity,
     * che ripristina un valore preciso durante il caricamento.
     */
    public void restoreHealth() {
        healToMax();
    }
}
