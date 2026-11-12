package it.unicam.cs.mpgc.rpg129131.model.enemy;

import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;

/**
 * Nemico del terzo livello, classificato come LEVIATHAN.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Leviathan extends Enemy {

    /**
     * Crea un nuovo Leviathan con salute massima 240, attacco 44
     * e difesa 16, associato al tipo LEVIATHAN e al livello THREE.
     */
    public Leviathan() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Leviathan",
                240,
                44,
                16,
                EnemyType.LEVIATHAN,
                LevelNumber.THREE
        );
    }
}
