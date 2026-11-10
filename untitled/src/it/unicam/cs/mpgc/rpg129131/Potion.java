package it.unicam.cs.mpgc.rpg129131;

public class Potion implements Item {

    @Override
    public String getName() {
        return "Pozione";
    }

    public void use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException(
                    "L'eroe non può essere null."
            );
        }

        if (!hero.isAlive()) {
            throw new IllegalStateException(
                    "Un eroe sconfitto non può usare una pozione."
            );
        }

        if (hero.getHealth() >= hero.getMaxHealth()) {
            throw new IllegalStateException(
                    hero.getName() + " ha già tutta la vita."
            );
        }

        hero.healToMax();
    }
}
