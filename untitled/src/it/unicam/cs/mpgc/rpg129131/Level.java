package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private final LevelNumber number;
    private final List<Encounter> encounters;

    public Level(LevelNumber number, List<Encounter> encounters) {
        if (number == null) {
            throw new IllegalArgumentException( "Il numero del livello non può essere null." );
        }
        if (encounters == null || encounters.isEmpty()) {
            throw new IllegalArgumentException( "Il livello deve contenere almeno un incontro." );
        }
        for (Encounter encounter : encounters) {
            if (encounter == null) {
                throw new IllegalArgumentException("La lista non può contenere incontri null.");
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
                return false; }
        }
        return true;
    }
}
