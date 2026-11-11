package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class HolyWater extends Weapon {

    public HolyWater() {
        super(
                "Acqua Santa",
                1.4,
                Map.of(
                        EnemyType.DEMON, 2.0,
                        EnemyType.YELLOW_EYED_DEMON, 1.2,
                        EnemyType.LUCIFER, 0.6
                )
        );
    }
}