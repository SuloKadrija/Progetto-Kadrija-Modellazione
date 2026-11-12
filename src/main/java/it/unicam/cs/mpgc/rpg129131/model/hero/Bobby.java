package it.unicam.cs.mpgc.rpg129131.model.hero;

/**
 * Eroe inizialmente bloccato, incluso nel gruppo del secondo livello.
 * Definisce soltanto le statistiche iniziali; inventario e sblocco
 * sono gestiti dalla classe Hero e dalla preparazione dei livelli.
 */
public class Bobby extends Hero {

    /**
     * Crea Bobby con salute massima 115, attacco 25 e difesa 10.
     * L’inventario nasce vuoto e lo stato iniziale di sblocco è false.
     */
    public Bobby() {
        // Ordine dei parametri: nome, salute, attacco, difesa, stato di sblocco.
        super("Bobby", 115, 25, 10, false);
    }
}
