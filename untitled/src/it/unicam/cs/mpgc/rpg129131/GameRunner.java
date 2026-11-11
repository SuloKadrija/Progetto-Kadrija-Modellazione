package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public class GameRunner {

    private final GameSession session;
    private final GameView view;

    public GameRunner(GameSession session, GameView view) {
        if (session == null || view == null) {
            throw new IllegalArgumentException("La sessione e la vista non possono essere null.");
        }

        this.session = session;
        this.view = view;
    }

    public void run() {
        if (!session.isStarted()) {
            showMessages(session.startNewGame());
        }

        while (!session.isFinished()) {
            GameState gameState = session.getGameState();
            Encounter encounter = gameState.getCurrentEncounter();

            if (encounter == null) {
                throw new IllegalStateException("Non è disponibile un incontro corrente.");
            }

            view.showStatus(gameState.getCurrentLevel().getNumber(), gameState.getCurrentEncounterIndex() + 1, encounter);

            List<HeroAction> actions = view.readActions(encounter);

            List<CombatEvent> events = session.playRound(actions);

            view.showEvents(events);
        }

        showFinalResult();
    }

    private void showMessages(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            view.showMessage(event.message());
        }
    }

    private void showFinalResult() {
        GameState gameState = session.getGameState();

        if (gameState.isGameCompleted()) {
            view.showMessage("\nVITTORIA: hai completato tutti i livelli!");
        } else if (gameState.isGameOver()) {
            view.showMessage("\nGAME OVER: il gruppo è stato sconfitto.");
        }
    }
}