package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class AngelicArrow extends Weapon {

    public AngelicArrow() {
        super("Freccia angelica", 1.3, Map.of(
                        EnemyType.ANGEL, 1.8,
                        EnemyType.LEVIATHAN, 1.5,
                        EnemyType.GOD, 1.2
                )
        );
    }
}
