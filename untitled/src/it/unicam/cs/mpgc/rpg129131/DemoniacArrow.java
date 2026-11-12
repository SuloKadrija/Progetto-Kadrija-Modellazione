package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Freccia demoniaca, con efficacia specifica sui nemici del secondo livello.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class DemoniacArrow extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public DemoniacArrow() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super(
                "Freccia demoniaca",
                1.5,
                Map.of(
                        // Efficacia contro demoni.
                        EnemyType.DEMON, 1.6,
                        // Efficacia contro demoni dagli occhi gialli.
                        EnemyType.YELLOW_EYED_DEMON, 1.6,
                        // Efficacia contro Lucifero.
                        EnemyType.LUCIFER, 1.3
                )
        );
    }
}
