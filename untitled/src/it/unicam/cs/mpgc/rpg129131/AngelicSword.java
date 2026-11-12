package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Stella del Mattino: arma efficace contro Lucifero e utilizzabile
 * anche contro gli avversari del terzo livello.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class AngelicSword extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public AngelicSword() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Stella del Mattino",
                1.4,
                Map.of(
                        // Efficacia contro Lucifero.
                        EnemyType.LUCIFER, 2.0,
                        // Efficacia contro leviatani.
                        EnemyType.LEVIATHAN, 1.5,
                        // Efficacia contro angeli.
                        EnemyType.ANGEL, 1.2,
                        // Efficacia contro Dio.
                        EnemyType.GOD, 1.6
                )
        );
    }
}
