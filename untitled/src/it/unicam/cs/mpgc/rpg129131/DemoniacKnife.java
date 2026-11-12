package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Pugnale demoniaco, particolarmente efficace contro il demone dagli occhi gialli.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class DemoniacKnife extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public DemoniacKnife() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Pugnale demoniaco",
                1.4,
                Map.of(
                        // Efficacia contro demoni.
                        EnemyType.DEMON, 1.3,
                        // Efficacia contro demoni dagli occhi gialli.
                        EnemyType.YELLOW_EYED_DEMON, 2.0,
                        // Efficacia contro Lucifero.
                        EnemyType.LUCIFER, 1.0
                )
        );
    }
}
