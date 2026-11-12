package it.unicam.cs.mpgc.rpg129131.model.item;

import it.unicam.cs.mpgc.rpg129131.model.enemy.EnemyType;

import java.util.Map;

/**
 * Paletto di legno, particolarmente efficace contro i vampiri.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class WoodenStake extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public WoodenStake() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Paletto di legno",
                1.4,
                Map.of(
                        // Efficacia contro vampiri.
                        EnemyType.VAMPIRE, 2.0,
                        // Efficacia contro fantasmi.
                        EnemyType.GHOST, 0.5,
                        // Efficacia contro wendigo.
                        EnemyType.WENDIGO, 0.8
                )
        );
    }
}
