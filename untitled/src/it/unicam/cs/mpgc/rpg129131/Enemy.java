package it.unicam.cs.mpgc.rpg129131;

public abstract class Enemy extends Entity {

    private final LevelNumber requiredLevel;

    protected Enemy(String name, int health, int attack, int defense, LevelNumber requiredLevel){
        super(name, health, attack, defense);
        this.requiredLevel=requiredLevel;
    }

    public LevelNumber getRequiredLevel() {
        return requiredLevel;
    }
}
