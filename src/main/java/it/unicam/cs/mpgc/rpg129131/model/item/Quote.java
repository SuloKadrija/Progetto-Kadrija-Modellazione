package it.unicam.cs.mpgc.rpg129131.model.item;

import it.unicam.cs.mpgc.rpg129131.model.enemy.EnemyType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Rappresenta i Passi della Bibbia, efficaci esclusivamente contro Dio.
 * Configura efficacia zero per tutte le altre categorie, comprese
 * quelle eventualmente aggiunte in futuro a EnemyType.
 * Il calcolo e l'applicazione del danno spettano al servizio di combattimento.
 */
public class Quote extends Weapon {

    /**
     * Crea l'arma con moltiplicatore base 1.2 ed efficacia 2.0 contro Dio.
     */
    public Quote() {
        super(
                "Passi della Bibbia",
                1.2,
                createEffectiveness()
        );
    }

    /**
     * Costruisce la mappa includendo ogni categoria definita nell'enumerazione.
     * Evita che nuove categorie ereditino l'efficacia predefinita di Weapon.
     *
     * @return efficacia zero per ogni categoria diversa da GOD
     */
    private static Map<EnemyType, Double> createEffectiveness() {
        Map<EnemyType, Double> effectiveness =
                new EnumMap<>(EnemyType.class);

        for (EnemyType type : EnemyType.values()) {
            effectiveness.put(type, 0.0);
        }

        effectiveness.put(EnemyType.GOD, 2.0);

        return effectiveness;
    }
}