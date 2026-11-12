package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Passi della Bibbia, utilizzabili come arma contro Dio.
 * La mappa assegna efficacia zero a tutte le altre categorie attuali.
 * La classe configura i moltiplicatori; il danno effettivo è calcolato
 * da CombatService usando anche attacco dell’eroe e difesa del nemico.
 */
public class Quote extends Weapon {

    /**
     * Inizializza nome, moltiplicatore base e mappa di efficacia dell’arma.
     * Per le categorie non presenti nella mappa, Weapon restituisce efficacia 1.0.
     */
    public Quote() {
        // Ordine dei parametri: nome, moltiplicatore base, efficacia per tipo di nemico.
        super("Passi della Bibbia", 1.2,
                Map.of(
                        // Nessun effetto contro fantasmi.
                        EnemyType.GHOST, 0.0,
                        // Nessun effetto contro vampiri.
                        EnemyType.VAMPIRE, 0.0,
                        // Nessun effetto contro wendigo.
                        EnemyType.WENDIGO, 0.0,
                        // Nessun effetto contro demoni.
                        EnemyType.DEMON, 0.0,
                        // Nessun effetto contro demoni dagli occhi gialli.
                        EnemyType.YELLOW_EYED_DEMON, 0.0,
                        // Nessun effetto contro Lucifero.
                        EnemyType.LUCIFER, 0.0,
                        // Nessun effetto contro leviatani.
                        EnemyType.LEVIATHAN, 0.0,
                        // Nessun effetto contro angeli.
                        EnemyType.ANGEL, 0.0,
                        // Efficacia contro Dio.
                        EnemyType.GOD, 2.0
                )
        );
    }
}
