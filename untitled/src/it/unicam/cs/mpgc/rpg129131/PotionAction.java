package it.unicam.cs.mpgc.rpg129131;

/**
 * Descrive la richiesta di usare una specifica pozione su chi sta agendo.
 * La creazione del record non cura l’eroe e non consuma la pozione.
 *
 * @param hero eroe che intende curarsi
 * @param potion pozione da utilizzare
 */
public record PotionAction(
        Hero hero,
        Potion potion
) implements HeroAction {

    /**
     * Valida i riferimenti della richiesta.
     * Il possesso della pozione e la possibilità di curarsi vengono
     * verificati quando la richiesta viene eseguita.
     *
     * @throws IllegalArgumentException se eroe o pozione è null
     */
    public PotionAction {
        if (hero == null) {
            throw new IllegalArgumentException("L'eroe non può essere null.");
        }

        if (potion == null) {
            throw new IllegalArgumentException("La pozione non può essere null.");
        }
    }
}
