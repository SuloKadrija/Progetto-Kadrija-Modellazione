package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class AngelicSword extends Weapon {

    public AngelicSword() {
        super(
                "Stella del Mattino",
                1.4,
                Map.of(
                        EnemyType.LUCIFER, 2.0,
                        EnemyType.LEVIATHAN, 1.5,
                        EnemyType.ANGEL, 1.2,
                        EnemyType.GOD, 1.6
                )
        );
    }
}
