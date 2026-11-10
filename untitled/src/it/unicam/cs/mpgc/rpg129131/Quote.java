package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class Quote extends Weapon {

    public Quote() {
        super("Passi della Bibbia", 2.0,
                Map.of(
                        EnemyType.GHOST, 0.0,
                        EnemyType.VAMPIRE, 0.0,
                        EnemyType.WENDIGO, 0.0,
                        EnemyType.DEMON, 0.0,
                        EnemyType.YELLOW_EYED_DEMON, 0.0,
                        EnemyType.LUCIFER, 0.0,
                        EnemyType.LEVIATHAN, 0.0,
                        EnemyType.ANGEL, 0.0,
                        EnemyType.GOD, 2.5
                )
        );
    }
}
