package it.unicam.cs.mpgc.rpg129131.model.enemy;

import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;

/**
 * Nemico del primo livello, classificato come GHOST.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Ghost extends Enemy {

    /**
     * Crea un nuovo Ghost con salute massima 180, attacco 28
     * e difesa 8, associato al tipo GHOST e al livello ONE.
     */
    public Ghost() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Ghost",
                180,
                28,
                8,
                EnemyType.GHOST,
                LevelNumber.ONE
        );
    }
}
