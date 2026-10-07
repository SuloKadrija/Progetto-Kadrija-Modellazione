package it.unicam.cs.mpgc.rpg129131;
public abstract class Entity {

    private final String name;
    private int health;
    private final int attack;
    private final int defense;

    protected Entity(String name, int health, int attack, int defense) {

        if (name==null){
            throw new NullPointerException("Name cannot be null");
        }

        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if(health<0) this.health=0;
    }
}