package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Acqua santa, particolarmente efficace contro i demoni comuni.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class HolyWater extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public HolyWater() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Acqua Santa",
                1.4,
                Map.of(
                        // Efficacia contro demoni.
                        EnemyType.DEMON, 2.0,
                        // Efficacia contro demoni dagli occhi gialli.
                        EnemyType.YELLOW_EYED_DEMON, 1.2,
                        // Efficacia contro Lucifero.
                        EnemyType.LUCIFER, 0.6
                )
        );
    }
}
