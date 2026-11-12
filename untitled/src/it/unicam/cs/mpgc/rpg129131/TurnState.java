package it.unicam.cs.mpgc.rpg129131;

public record TurnState(boolean enemiesTurn, int heroIndex, int enemyIndex) {

    public TurnState {
        if (heroIndex < 0 || enemyIndex < 0) {
            throw new IllegalArgumentException("Gli indici del turno non possono essere negativi.");
        }
    }
}