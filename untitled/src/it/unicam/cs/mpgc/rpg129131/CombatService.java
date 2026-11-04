package it.unicam.cs.mpgc.rpg129131;

public class CombatService {

    public int calculateDamage(Hero hero, Weapon weapon, Enemy enemy) {
        double baseDamage = hero.getAttack() * weapon.getBaseMultiplier();
        double effectiveness = weapon.getMultiplierAgainst(enemy);
        double totalDamage = baseDamage * effectiveness;
        int roundedDamage = (int) Math.round(totalDamage);
        return Math.max(1, roundedDamage - enemy.getDefense());
    }

    public int attack(Hero hero, Weapon weapon, Enemy enemy) {
        validateAttack(hero, weapon, enemy);
        int damage = calculateDamage(hero, weapon, enemy);
        enemy.takeDamage(damage);
        return damage;
    }

    private void validateAttack(Hero hero, Weapon weapon, Enemy enemy) {
        if (hero == null || weapon == null || enemy == null) {
            throw new IllegalArgumentException(
                    "Eroe, arma e nemico non possono essere null."
            );
        }

        if (!hero.isAlive()) {
            throw new IllegalStateException(
                    "Un eroe sconfitto non può attaccare."
            );
        }

        if (!enemy.isAlive()) {
            throw new IllegalStateException(
                    "Un nemico sconfitto non può essere attaccato."
            );
        }

        if (!hero.getInventory().contains(weapon)) {
            throw new IllegalStateException(
                    "L'eroe non possiede quest'arma."
            );
        }
    }
}