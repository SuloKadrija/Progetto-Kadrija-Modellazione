package it.unicam.cs.mpgc.rpg129131.model.hero;

/**
 * Eroe disponibile dall’inizio e presente nei gruppi dei tre livelli.
 * Definisce soltanto le statistiche iniziali; inventario e sblocco
 * sono gestiti dalla classe Hero e dalla preparazione dei livelli.
 */
public class Dean extends Hero {

    /**
     * Crea Dean con salute massima 130, attacco 23 e difesa 15.
     * L’inventario nasce vuoto e lo stato iniziale di sblocco è true.
     */
    public Dean() {
        // Ordine dei parametri: nome, salute, attacco, difesa, stato di sblocco.
        super("Dean", 130, 23, 15, true);
    }
}
