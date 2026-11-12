package it.unicam.cs.mpgc.rpg129131;

/**
 * Eroe inizialmente bloccato, incluso nel gruppo del terzo livello.
 * Definisce soltanto le statistiche iniziali; inventario e sblocco
 * sono gestiti dalla classe Hero e dalla preparazione dei livelli.
 */
public class Jack extends Hero {

    /**
     * Crea Jack con salute massima 110, attacco 27 e difesa 9.
     * L’inventario nasce vuoto e lo stato iniziale di sblocco è false.
     */
    public Jack() {
        // Ordine dei parametri: nome, salute, attacco, difesa, stato di sblocco.
        super("Jack", 110, 27, 9, false);
    }
}
