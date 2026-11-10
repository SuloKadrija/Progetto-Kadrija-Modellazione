package it.unicam.cs.mpgc.rpg129131;

public record AttackAction(Hero hero, Weapon weapon, Enemy target) implements HeroAction {

    public AttackAction {
        if (hero == null) {
            throw new IllegalArgumentException("L'eroe non può essere null.");
        }

        if (weapon == null) {
            throw new IllegalArgumentException("L'arma non può essere null.");
        }

        if (target == null) {
            throw new IllegalArgumentException("Il bersaglio non può essere null.");
        }
    }
}