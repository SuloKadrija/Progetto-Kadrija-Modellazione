package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class DemoniacKnife extends Weapon {

    public DemoniacKnife() {
        super(
                "Pugnale demoniaco",
                1.4,
                Map.of(
                        EnemyType.DEMON, 1.3,
                        EnemyType.YELLOW_EYED_DEMON, 2.0,
                        EnemyType.LUCIFER, 1.0
                )
        );
    }
}