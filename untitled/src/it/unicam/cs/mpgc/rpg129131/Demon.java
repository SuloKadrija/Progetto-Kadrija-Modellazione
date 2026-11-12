package it.unicam.cs.mpgc.rpg129131;

/**
 * Nemico del secondo livello, classificato come DEMON.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Demon extends Enemy {

    /**
     * Crea un nuovo Demon con salute massima 220, attacco 40
     * e difesa 12, associato al tipo DEMON e al livello TWO.
     */
    public Demon() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Demon",
                220,
                40,
                12,
                EnemyType.DEMON,
                LevelNumber.TWO
        );
    }
}
