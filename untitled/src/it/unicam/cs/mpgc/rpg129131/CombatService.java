package it.unicam.cs.mpgc.rpg129131;

public class CombatService {
    public int calculateDamage(Hero hero, Weapon weapon, Enemy enemy) {
        double baseDamage = hero.getAttack() * weapon.getMultiplier();
        int roundedDamage = (int) Math.round(baseDamage);
        return Math.max(1, roundedDamage - enemy.getDefense());
    }

    public int attack(Hero hero, Weapon weapon, Enemy enemy) {
        if (hero == null || weapon == null || enemy == null) {
            throw new IllegalArgumentException("Personaggio, arma e nemico non possono essere null.");
        }
        if (!hero.isAlive()) {
            throw new IllegalStateException("Un personaggio sconfitto non può attaccare.");
        }
        if (!enemy.isAlive()) {
            throw new IllegalStateException("Un nemico sconfitto non può essere attaccato.");
        }
        if (!hero.getInventory().contains(weapon)) {
            throw new IllegalStateException("Il personaggio non possiede quest'arma.");
        }
        int damage = calculateDamage(hero, weapon, enemy);
        enemy.takeDamage(damage);
        return damage;
    }
}
