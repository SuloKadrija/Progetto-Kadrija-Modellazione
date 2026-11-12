package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Fucile senza moltiplicatori specifici per categoria di nemico.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class Rifle extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public Rifle() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Fucile",
                1.4,
                Map.of()
        );
    }
}
