package it.unicam.cs.mpgc.rpg129131;

public abstract class Hero extends Entity {

    private boolean unlocked;
    private final Inventory inventory;

    protected Hero(String name, int health, int attack, int defense, boolean unlocked) {

        super(name, health, attack, defense);

        this.unlocked = unlocked;
        this.inventory = new Inventory();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public Inventory getInventory() {
        return inventory;
    }
}