package it.unicam.cs.mpgc.rpg129131;

/**
 * Rappresenta un nemico, classificato per tipo e livello di appartenenza.
 * Il tipo permette alle armi di determinare la propria efficacia; il livello
 * consente di verificare la corretta composizione degli incontri.
 */
public abstract class Enemy extends Entity {

    /**
     * Categoria del nemico usata per l’efficacia delle armi.
     */
    private final EnemyType type;
    /**
     * Livello al quale il nemico può appartenere.
     */
    private final LevelNumber requiredLevel;

    /**
     * Crea un nemico con statistiche, categoria e livello di appartenenza.
     *
     * @param name nome del nemico
     * @param health salute iniziale e massima
     * @param attack attacco del nemico
     * @param defense difesa del nemico
     * @param type categoria utilizzata dalle armi
     * @param requiredLevel livello al quale appartiene il nemico
     * @throws IllegalArgumentException se statistiche, tipo o livello non sono validi
     */
    protected Enemy(String name, int health, int attack, int defense, EnemyType type, LevelNumber requiredLevel) {
        super(name, health, attack, defense);

        if (type == null) {
            throw new IllegalArgumentException("Il tipo di nemico non può essere null.");
        }

        if (requiredLevel == null) {
            throw new IllegalArgumentException("Il livello richiesto non può essere null.");
        }

        this.type = type;
        this.requiredLevel = requiredLevel;
    }

    /**
     * Restituisce la categoria del nemico.
     *
     * @return tipo utilizzato per i moltiplicatori delle armi
     */
    public EnemyType getType() {
        return type;
    }

    /**
     * Restituisce il livello di appartenenza del nemico.
     *
     * @return livello richiesto per inserirlo in un incontro
     */
    public LevelNumber getRequiredLevel() {
        return requiredLevel;
    }
}
