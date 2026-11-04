package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class Quote extends Weapon {

    public Quote() {
        super("Passi della Bibbia", 2.0, Map.of(
                        EnemyType.GOD, 2.5,
                        EnemyType.ANGEL, 1.2,
                        EnemyType.LEVIATHAN, 0.8
                )
        );
    }
}
