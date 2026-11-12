package it.unicam.cs.mpgc.rpg129131.model.item;

import it.unicam.cs.mpgc.rpg129131.model.enemy.EnemyType;

import java.util.Map;

/**
 * Sale, particolarmente efficace contro i fantasmi.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class Salt extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public Salt() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Sale",
                1.4,
                Map.of(
                        // Efficacia contro fantasmi.
                        EnemyType.GHOST, 2.0,
                        // Efficacia contro vampiri.
                        EnemyType.VAMPIRE, 0.5,
                        // Efficacia contro wendigo.
                        EnemyType.WENDIGO, 0.5
                )
        );
    }
}
