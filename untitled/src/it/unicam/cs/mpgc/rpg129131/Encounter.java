package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Encounter {

    private final Party party;
    private final List<Enemy> enemies;

    public Encounter(Party party, List<Enemy> enemies) {
        if (party == null) {
            throw new IllegalArgumentException("Il gruppo non può essere null.");
        }

        if (enemies == null || enemies.isEmpty()) {
            throw new IllegalArgumentException("L'incontro deve avere almeno un nemico.");
        }

        for (Enemy enemy : enemies) {
            if (enemy == null) {
                throw new IllegalArgumentException("La lista non può contenere nemici null.");
            }
        }


        this.party = party;
        this.enemies = new ArrayList<>(enemies);
    }

    public Party getParty() {
        return party;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public boolean isCompleted() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }

        return true;
    }
}