package it.unicam.cs.mpgc.rpg129131.combat;

import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;

/**
 * Definisce una richiesta di azione associata a un eroe.
 * Permette alla sessione di verificare chi intende agire senza
 * conoscere il tipo concreto della richiesta.
 * L'esecuzione spetta al sistema di combattimento.
 */
public interface HeroAction {

    /**
     * Restituisce l'eroe che richiede l'azione.
     *
     * @return eroe non nullo associato alla richiesta
     */
    Hero hero();
}