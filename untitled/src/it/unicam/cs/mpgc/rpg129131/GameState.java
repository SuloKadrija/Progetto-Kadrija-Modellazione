package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private final List<Level> levels;
    private int currentLevelIndex;
    private int currentEncounterIndex;

    public GameState(List<Level> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException("La lista dei livelli non può essere null o vuota.");
        }

        for (Level level : levels) {
            if (level == null) {
                throw new IllegalArgumentException("La lista non può contenere livelli null.");
            }
        }

        this.levels = new ArrayList<>(levels);
        this.currentLevelIndex = 0;
        this.currentEncounterIndex = 0;
    }

    public Level getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getCurrentEncounterIndex() {
        return currentEncounterIndex;
    }

    public Encounter getCurrentEncounter() {
        if (isGameCompleted()) {
            return null;
        }

        return getCurrentLevel()
                .getEncounters()
                .get(currentEncounterIndex);
    }

    public Party getCurrentParty() {
        Encounter encounter = getCurrentEncounter();

        if (encounter == null) {
            return null;
        }

        return encounter.getParty();
    }

    public boolean isGameOver() {
        Party party = getCurrentParty();

        if (party == null) {
            return false;
        }

        return !party.hasLivingMembers();
    }

    public boolean isLevelCompleted() {
        return getCurrentLevel().isCompleted();
    }

    public boolean isGameCompleted() {
        return currentLevelIndex == levels.size() - 1
                && getCurrentLevel().isCompleted();
    }

    public boolean advanceEncounter() {
        if (isGameOver() || isGameCompleted()) {
            return false;
        }

        Encounter currentEncounter = getCurrentEncounter();

        if (currentEncounter == null || !currentEncounter.isCompleted()) {
            return false;
        }

        List<Encounter> encounters = getCurrentLevel().getEncounters();

        if (currentEncounterIndex < encounters.size() - 1) {
            currentEncounterIndex++;
            return true;
        }

        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            currentEncounterIndex = 0;
            return true;
        }

        return false;
    }

    public void restoreCurrentPartyHealth() {
        Party party = getCurrentParty();

        if (party == null) {
            return;
        }

        for (Hero hero : party.getMembers()) {
            hero.restoreHealth();
        }
    }
}