package it.unicam.cs.mpgc.rpg129131;

/**
 * Nemico del terzo livello, classificato come GOD.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class God extends Enemy {

    /**
     * Crea un nuovo God con salute massima 600, attacco 55
     * e difesa 20, associato al tipo GOD e al livello THREE.
     */
    public God() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "God",
                600,
                55,
                20,
                EnemyType.GOD,
                LevelNumber.THREE
        );
    }
}
