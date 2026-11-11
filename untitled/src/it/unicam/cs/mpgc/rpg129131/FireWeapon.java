package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class FireWeapon extends Weapon {

    public FireWeapon() {
        super(
                "Fuoco",
                1.4,
                Map.of(
                        EnemyType.GHOST, 1.3,
                        EnemyType.VAMPIRE, 0.8,
                        EnemyType.WENDIGO, 2.0
                )
        );
    }
}