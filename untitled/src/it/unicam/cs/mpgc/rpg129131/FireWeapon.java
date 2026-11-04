package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class FireWeapon extends Weapon {

    public FireWeapon() {
        super("Fuoco", 1.5, Map.of(
                        EnemyType.GHOST, 2.0,
                        EnemyType.WENDIGO, 1.5
                )
        );
    }
}