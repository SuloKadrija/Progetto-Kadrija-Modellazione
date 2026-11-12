package it.unicam.cs.mpgc.rpg129131;

/**
 * Eroe inizialmente bloccato, incluso nel gruppo del terzo livello.
 * Definisce soltanto le statistiche iniziali; inventario e sblocco
 * sono gestiti dalla classe Hero e dalla preparazione dei livelli.
 */
public class Castiel extends Hero {

    /**
     * Crea Castiel con salute massima 125, attacco 24 e difesa 13.
     * L’inventario nasce vuoto e lo stato iniziale di sblocco è false.
     */
    public Castiel() {
        // Ordine dei parametri: nome, salute, attacco, difesa, stato di sblocco.
        super("Castiel", 125, 24, 13, false);
    }
}
