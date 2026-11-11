package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class AngelicKnife extends Weapon {

    public AngelicKnife() {
        super(
                "Pugnale angelico",
                1.4,
                Map.of(
                        EnemyType.LEVIATHAN, 1.3,
                        EnemyType.ANGEL, 2.0,
                        EnemyType.GOD, 0.8
                )
        );
    }
}