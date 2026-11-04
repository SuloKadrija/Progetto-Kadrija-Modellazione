package it.unicam.cs.mpgc.rpg129131;

public record AttackAction(Hero hero, Weapon weapon, Enemy target) {

    public AttackAction {
        if (hero == null || weapon == null || target == null) {
            throw new IllegalArgumentException(
                    "Eroe, arma e bersaglio non possono essere null."
            );
        }
    }
}