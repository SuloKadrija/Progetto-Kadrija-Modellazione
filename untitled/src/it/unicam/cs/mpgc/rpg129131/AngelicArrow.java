package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Freccia angelica, particolarmente efficace contro i leviatani.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class AngelicArrow extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public AngelicArrow() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Freccia angelica",
                1.4,
                Map.of(
                        // Efficacia contro leviatani.
                        EnemyType.LEVIATHAN, 2.0,
                        // Efficacia contro angeli.
                        EnemyType.ANGEL, 1.5,
                        // Efficacia contro Dio.
                        EnemyType.GOD, 0.8
                )
        );
    }
}
