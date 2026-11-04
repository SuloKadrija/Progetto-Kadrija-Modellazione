package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class DemoniacArrow extends Weapon {

    public DemoniacArrow() {
        super("Freccia demoniaca", 1.2, Map.of(
                        EnemyType.DEMON, 1.8,
                        EnemyType.YELLOW_EYED_DEMON, 1.8,
                        EnemyType.LUCIFER, 1.5
                )
        );
    }
}

