package it.unicam.cs.mpgc.rpg129131.model.enemy;

import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;

/**
 * Nemico del terzo livello, classificato come ANGEL.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Angel extends Enemy {

    /**
     * Crea un nuovo Angel con salute massima 260, attacco 48
     * e difesa 18, associato al tipo ANGEL e al livello THREE.
     */
    public Angel() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Angel",
                260,
                48,
                18,
                EnemyType.ANGEL,
                LevelNumber.THREE
        );
    }
}
