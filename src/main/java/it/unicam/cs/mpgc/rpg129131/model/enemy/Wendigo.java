package it.unicam.cs.mpgc.rpg129131.model.enemy;

import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;

/**
 * Nemico del primo livello, classificato come WENDIGO.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Wendigo extends Enemy {

    /**
     * Crea un nuovo Wendigo con salute massima 280, attacco 40
     * e difesa 12, associato al tipo WENDIGO e al livello ONE.
     */
    public Wendigo() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Wendigo",
                280,
                40,
                12,
                EnemyType.WENDIGO,
                LevelNumber.ONE
        );
    }
}
