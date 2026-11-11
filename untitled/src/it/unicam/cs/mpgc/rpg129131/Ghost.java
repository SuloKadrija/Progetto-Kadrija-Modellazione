package it.unicam.cs.mpgc.rpg129131;

public class Ghost extends Enemy {

    public Ghost() {
        super(
                "Ghost",
                180,
                28,
                8,
                EnemyType.GHOST,
                LevelNumber.ONE
        );
    }
}