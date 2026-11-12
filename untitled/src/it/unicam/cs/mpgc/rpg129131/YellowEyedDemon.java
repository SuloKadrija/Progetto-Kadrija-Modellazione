package it.unicam.cs.mpgc.rpg129131;

/**
 * Nemico del secondo livello, classificato come YELLOW_EYED_DEMON.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class YellowEyedDemon extends Enemy {

    /**
     * Crea un nuovo Yellow-Eyed Demon con salute massima 400, attacco 45
     * e difesa 15, associato al tipo YELLOW_EYED_DEMON e al livello TWO.
     */
    public YellowEyedDemon() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Yellow-Eyed Demon",
                400,
                45,
                15,
                EnemyType.YELLOW_EYED_DEMON,
                LevelNumber.TWO
        );
    }
}
