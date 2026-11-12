package it.unicam.cs.mpgc.rpg129131;

/**
 * Eroe disponibile dall’inizio e presente nei gruppi dei tre livelli.
 * Definisce soltanto le statistiche iniziali; inventario e sblocco
 * sono gestiti dalla classe Hero e dalla preparazione dei livelli.
 */
public class Sam extends Hero {

    /**
     * Crea Sam con salute massima 120, attacco 24 e difesa 12.
     * L’inventario nasce vuoto e lo stato iniziale di sblocco è true.
     */
    public Sam() {
        // Ordine dei parametri: nome, salute, attacco, difesa, stato di sblocco.
        super("Sam", 120, 24, 12, true);
    }
}
