package it.unicam.cs.mpgc.rpg129131;

public class GameState {

    private final Party party;
    private Level currentLevel;
    private int currentEncounterIndex;

    public GameState(Party party, Level initialLevel) {
        if (party == null) {
            throw new IllegalArgumentException( "Il gruppo non può essere null." );
        }

        if (initialLevel == null) {
            throw new IllegalArgumentException( "Il livello iniziale non può essere null." );
        }

        this.party = party;
        this.currentLevel = initialLevel;
        this.currentEncounterIndex = 0; }

    public Party getParty() {
        return party;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentEncounterIndex() {
        return currentEncounterIndex;
    }

    public Encounter getCurrentEncounter() {
        if (isGameOver() || currentLevel.isCompleted()) {
            return null;
        }
        return currentLevel.getEncounters().get(currentEncounterIndex);
    }

    public boolean isGameOver() {
        return !party.hasLivingMembers();
    }

    public boolean isLevelCompleted() {
        return currentLevel.isCompleted();
    }

    public boolean isGameCompleted() {
        return isLevelCompleted();
    }

    public boolean advanceEncounter() {
        if (isGameOver() || isLevelCompleted()) {
            return false;
        }

        Encounter encounter = getCurrentEncounter();
        if (encounter == null || !encounter.isCompleted()) {
            return false;
        }
        if (currentEncounterIndex < currentLevel.getEncounters().size() - 1) {
            currentEncounterIndex++;
            return true;
        }
        return false;
    }
}
