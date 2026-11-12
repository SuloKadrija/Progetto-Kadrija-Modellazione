package it.unicam.cs.mpgc.rpg129131;

/**
 * Nemico del secondo livello, classificato come LUCIFER.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Lucifer extends Enemy {

    /**
     * Crea un nuovo Lucifer con salute massima 550, attacco 48
     * e difesa 18, associato al tipo LUCIFER e al livello TWO.
     */
    public Lucifer() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Lucifer",
                550,
                48,
                18,
                EnemyType.LUCIFER,
                LevelNumber.TWO
        );
    }
}
