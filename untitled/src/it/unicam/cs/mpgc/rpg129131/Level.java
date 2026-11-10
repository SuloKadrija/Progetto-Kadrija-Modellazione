package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {

    private static final int REQUIRED_ENCOUNTERS = 3;

    private final LevelNumber number;
    private final List<Encounter> encounters;

    public Level(LevelNumber number, List<Encounter> encounters) {
        if (number == null) {
            throw new IllegalArgumentException("Il numero del livello non può essere null.");
        }

        if (encounters == null) {
            throw new IllegalArgumentException("La lista degli incontri non può essere null.");
        }

        if (encounters.size() != REQUIRED_ENCOUNTERS) {
            throw new IllegalArgumentException("Ogni livello deve contenere esattamente " + REQUIRED_ENCOUNTERS + " incontri.");
        }

        for (Encounter encounter : encounters) {
            if (encounter == null) {
                throw new IllegalArgumentException("La lista non può contenere incontri null.");
            }

            for (Enemy enemy : encounter.getEnemies()) {
                if (enemy.getRequiredLevel() != number) {
                    throw new IllegalArgumentException(
                            "Il nemico " + enemy.getName() + " appartiene al livello " + enemy.getRequiredLevel() + " e non può essere inserito nel livello " + number + ".");
                }
            }
        }

        this.number = number;
        this.encounters = new ArrayList<>(encounters);
    }

    public LevelNumber getNumber() {
        return number;
    }

    public List<Encounter> getEncounters() {
        return Collections.unmodifiableList(encounters);
    }

    public boolean isCompleted() {
        for (Encounter encounter : encounters) {
            if (!encounter.isCompleted()) {
                return false;
            }
        }

        return true;
    }
}