package it.unicam.cs.mpgc.rpg129131;

public abstract class Enemy extends Entity {

    private final EnemyType type;
    private final LevelNumber requiredLevel;

    protected Enemy(String name, int health, int attack, int defense, EnemyType type, LevelNumber requiredLevel) {

        super(name, health, attack, defense);

        if (type == null) {
            throw new IllegalArgumentException("Il tipo di nemico non può essere null.");
        }

        this.type = type;
        this.requiredLevel = requiredLevel;
    }

    public EnemyType getType() {
        return type;
    }

    public LevelNumber getRequiredLevel() {
        return requiredLevel;
    }
}