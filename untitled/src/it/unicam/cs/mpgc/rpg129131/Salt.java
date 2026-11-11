package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class Salt extends Weapon {

    public Salt() {
        super(
                "Sale",
                1.4,
                Map.of(
                        EnemyType.GHOST, 2.0,
                        EnemyType.VAMPIRE, 0.5,
                        EnemyType.WENDIGO, 0.5
                )
        );
    }
}