package it.unicam.cs.mpgc.rpg129131.combat;

import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;

/**
 * Descrive la richiesta di attacco di un eroe con un’arma contro un nemico.
 * Il record conserva i riferimenti, senza eseguire l’attacco né copiare le entità.
 *
 * @param hero eroe che richiede l’attacco
 * @param weapon arma scelta dall’eroe
 * @param target nemico bersaglio
 */
public record AttackAction(Hero hero, Weapon weapon, Enemy target) implements HeroAction {

    /**
     * Valida la presenza dei riferimenti dell’azione.
     * Possesso dell’arma, appartenenza all’incontro e turno vengono
     * verificati quando la richiesta viene eseguita.
     *
     * @throws IllegalArgumentException se eroe, arma o bersaglio è null
     */
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
