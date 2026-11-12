package it.unicam.cs.mpgc.rpg129131;

public abstract class Entity {

    private final String name;
    private int health;
    private final int attack;
    private final int defense;
    private final int maxHealth;

    protected Entity(String name, int health, int attack, int defense) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Il nome non può essere nullo o vuoto."
            );
        }

        if (health <= 0) {
            throw new IllegalArgumentException("La vita iniziale deve essere maggiore di zero.");
        }

        if (attack < 0) {
            throw new IllegalArgumentException("L'attacco non può essere negativo.");
        }

        if (defense < 0) {
            throw new IllegalArgumentException("La difesa non può essere negativa.");
        }

        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.maxHealth = health;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void restoreHealth(int savedHealth) {
        if (savedHealth < 0 || savedHealth > maxHealth) {
            throw new IllegalArgumentException("La salute salvata deve essere compresa tra 0 e " + maxHealth + ".");
        }

        this.health = savedHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
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
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo.");
        }

        health = Math.max(0, health - damage);
    }

    public void healToMax() {
        health = maxHealth;
    }
}