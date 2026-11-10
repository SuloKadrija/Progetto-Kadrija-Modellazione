package it.unicam.cs.mpgc.rpg129131;

public record PotionAction(
        Hero hero,
        Potion potion
) implements HeroAction {

    public PotionAction {
        if (hero == null) {
            throw new IllegalArgumentException("L'eroe non può essere null.");
        }

        if (potion == null) {
            throw new IllegalArgumentException("La pozione non può essere null.");
        }
    }
}