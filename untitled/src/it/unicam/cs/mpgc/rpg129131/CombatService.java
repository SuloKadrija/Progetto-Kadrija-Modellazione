package it.unicam.cs.mpgc.rpg129131;

public class CombatService {

    public int calculateDamage(Hero hero, Weapon weapon, Enemy enemy) {
        validateParameters(hero, weapon, enemy);

        double effectiveness = weapon.getMultiplierAgainst(enemy);

        if (effectiveness == 0.0) {
            return 0;
        }

        double baseDamage = hero.getAttack() * weapon.getBaseMultiplier();

        double totalDamage = baseDamage * effectiveness;
        double roundedDamage = Math.floor(totalDamage + 0.5);

        double damageAfterDefense = roundedDamage - enemy.getDefense();

        if (damageAfterDefense <= 1.0) {
            return 1;
        }

        if (damageAfterDefense >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) damageAfterDefense;
    }

    public int attack(Hero hero, Weapon weapon, Enemy enemy) {
        validateAttack(hero, weapon, enemy);

        int calculatedDamage = calculateDamage(hero, weapon, enemy);
        int healthBefore = enemy.getHealth();

        enemy.takeDamage(calculatedDamage);

        return healthBefore - enemy.getHealth();
    }

    private void validateParameters(
            Hero hero,
            Weapon weapon,
            Enemy enemy
    ) {
        if (hero == null || weapon == null || enemy == null) {
            throw new IllegalArgumentException(
                    "Eroe, arma e nemico non possono essere null."
            );
        }
    }

    private void validateAttack(Hero hero, Weapon weapon, Enemy enemy) {
        validateParameters(hero, weapon, enemy);

        if (!hero.isAlive()) {
            throw new IllegalStateException("Un eroe sconfitto non può attaccare.");
        }

        if (!enemy.isAlive()) {
            throw new IllegalStateException("Un nemico sconfitto non può essere attaccato.");
        }

        if (!hero.getInventory().contains(weapon)) {
            throw new IllegalStateException("L'eroe non possiede quest'arma.");
        }
    }
}