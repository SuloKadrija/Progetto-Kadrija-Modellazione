package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public class GameRunner {

    private final GameState gameState;
    private final CombatController combatController;
    private final LevelPreparation levelPreparation;
    private final GameView view;

    public GameRunner(
            GameState gameState,
            CombatController combatController,
            LevelPreparation levelPreparation,
            GameView view
    ) {
        if (gameState == null
                || combatController == null
                || levelPreparation == null
                || view == null) {

            throw new IllegalArgumentException("I componenti della partita non possono essere null.");
        }

        this.gameState = gameState;
        this.combatController = combatController;
        this.levelPreparation = levelPreparation;
        this.view = view;
    }

    public void run() {
        if (gameState.isGameCompleted() || gameState.isGameOver()) {
            showFinalResult();
            return;
        }

        view.showMessage("=== SUPERNATURAL RPG ===");
        prepareCurrentLevel();

        while (!gameState.isGameOver()
                && !gameState.isGameCompleted()) {

            Encounter encounter = gameState.getCurrentEncounter();

            if (encounter == null) {
                throw new IllegalStateException("Non è disponibile un incontro corrente.");
            }

            view.showStatus(
                    gameState.getCurrentLevel().getNumber(),
                    gameState.getCurrentEncounterIndex() + 1,
                    encounter
            );

            List<HeroAction> actions = view.readActions(encounter);

            List<CombatEvent> events =
                    combatController.executeRound(encounter, actions);

            view.showEvents(events);
            view.showMessage(
                    combatController.getCombatResult(encounter)
            );

            if (!encounter.getParty().hasLivingMembers()) {
                break;
            }

            if (encounter.isCompleted()) {
                finishEncounter(encounter);
            }
        }

        showFinalResult();
    }

    private void finishEncounter(Encounter encounter) {
        for (Hero hero : encounter.getParty().getMembers()) {
            hero.restoreHealth();
        }

        view.showMessage("\nIncontro superato! Tutto il gruppo recupera la vita.");

        if (gameState.isGameCompleted()) {
            return;
        }

        int previousLevelIndex = gameState.getCurrentLevelIndex();

        boolean advanced = gameState.advanceEncounter();

        if (!advanced) {
            throw new IllegalStateException("Impossibile avanzare dopo l'incontro vinto.");
        }

        if (gameState.getCurrentLevelIndex() != previousLevelIndex) {
            prepareCurrentLevel();
        }
    }

    private void prepareCurrentLevel() {
        LevelNumber number = gameState.getCurrentLevel().getNumber();
        Party party = gameState.getCurrentParty();

        levelPreparation.prepare(number, party);

        view.showMessage("\nInizio livello " + number + ": gruppo sbloccato e dotazioni preparate.");
    }

    private void showFinalResult() {
        if (gameState.isGameCompleted()) {
            view.showMessage("\nVITTORIA: hai completato tutti i livelli!");
        } else if (gameState.isGameOver()) {
            view.showMessage("\nGAME OVER: il gruppo è stato sconfitto.");
        }
    }
}