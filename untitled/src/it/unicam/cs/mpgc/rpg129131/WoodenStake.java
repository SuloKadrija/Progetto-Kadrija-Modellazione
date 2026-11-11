package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class WoodenStake extends Weapon {

    public WoodenStake() {
        super(
                "Paletto di legno",
                1.4,
                Map.of(
                        EnemyType.VAMPIRE, 2.0,
                        EnemyType.GHOST, 0.5,
                        EnemyType.WENDIGO, 0.8
                )
        );
    }
}
