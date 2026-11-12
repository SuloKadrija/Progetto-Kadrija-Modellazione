package it.unicam.cs.mpgc.rpg129131.model.item;

import it.unicam.cs.mpgc.rpg129131.model.enemy.EnemyType;

import java.util.Map;

/**
 * Fuoco, particolarmente efficace contro i wendigo.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class FireWeapon extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public FireWeapon() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Fuoco",
                1.4,
                Map.of(
                        // Efficacia contro fantasmi.
                        EnemyType.GHOST, 1.3,
                        // Efficacia contro vampiri.
                        EnemyType.VAMPIRE, 0.8,
                        // Efficacia contro wendigo.
                        EnemyType.WENDIGO, 2.0
                )
        );
    }
}
