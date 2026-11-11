package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class AngelicArrow extends Weapon {

    public AngelicArrow() {
        super(
                "Freccia angelica",
                1.4,
                Map.of(
                        EnemyType.LEVIATHAN, 2.0,
                        EnemyType.ANGEL, 1.5,
                        EnemyType.GOD, 0.8
                )
        );
    }
}