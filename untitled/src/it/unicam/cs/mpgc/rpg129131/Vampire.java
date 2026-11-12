package it.unicam.cs.mpgc.rpg129131;

/**
 * Nemico del primo livello, classificato come VAMPIRE.
 * Definisce le statistiche iniziali e i dati di appartenenza;
 * azioni e bersagli sono gestiti dal sistema di combattimento.
 */
public class Vampire extends Enemy {

    /**
     * Crea un nuovo Vampire con salute massima 240, attacco 34
     * e difesa 10, associato al tipo VAMPIRE e al livello ONE.
     */
    public Vampire() {
        // Ordine: nome, salute, attacco, difesa, tipo di nemico, livello.
        super(
                "Vampire",
                240,
                34,
                10,
                EnemyType.VAMPIRE,
                LevelNumber.ONE
        );
    }
}
