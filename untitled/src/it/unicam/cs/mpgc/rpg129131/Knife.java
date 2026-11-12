package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Pugnale generico senza moltiplicatori specifici per categoria di nemico.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class Knife extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public Knife() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Pugnale",
                1.2,
                Map.of()
        );
    }
}
