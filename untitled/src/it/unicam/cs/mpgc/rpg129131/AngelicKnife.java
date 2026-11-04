package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class AngelicKnife extends Weapon {

    public AngelicKnife() {
        super("Pugnale angelico", 1.4, Map.of(
                        EnemyType.ANGEL, 2.0,
                        EnemyType.LEVIATHAN, 1.5,
                        EnemyType.GOD, 1.2
                )
        );
    }
}
