package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private final GameState gameState;
    private final CombatController combatController;
    private final LevelPreparation levelPreparation;
    private boolean started;

    public GameSession(GameState gameState, CombatController combatController, LevelPreparation levelPreparation) {

        if (gameState == null || combatController == null || levelPreparation == null) {
            throw new IllegalArgumentException("I componenti della partita non possono essere null.");
        }

        this.gameState = gameState;
        this.combatController = combatController;
        this.levelPreparation = levelPreparation;
        this.started = false;
    }

    public List<CombatEvent> startNewGame() {
        if (started) {
            throw new IllegalStateException("La sessione è già stata avviata.");
        }

        if (gameState.getCurrentLevelIndex() != 0 || gameState.getCurrentEncounterIndex() != 0 || gameState.isGameCompleted() || gameState.isGameOver()) {

            throw new IllegalStateException("Per avviare una nuova partita serve uno stato iniziale valido.");
        }

        List<CombatEvent> events = new ArrayList<>();

        events.add(new CombatEvent("=== SUPERNATURAL RPG ==="));

        prepareCurrentLevel(events);

        started = true;

        return events;
    }

    public List<CombatEvent> playRound(List<HeroAction> actions) {
        if (!started) {
            throw new IllegalStateException("La sessione non è ancora stata avviata.");
        }

        if (isFinished()) {
            throw new IllegalStateException("La partita è terminata.");
        }

        Encounter encounter = gameState.getCurrentEncounter();

        if (encounter == null) {
            throw new IllegalStateException("Non è disponibile un incontro corrente.");
        }

        List<CombatEvent> events = new ArrayList<>(combatController.executeRound(encounter, actions));

        events.add(new CombatEvent(combatController.getCombatResult(encounter)));

        if (!encounter.getParty().hasLivingMembers()) {
            return events;
        }

        if (encounter.isCompleted()) {
            finishEncounter(encounter, events);
        }

        return events;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return gameState.isGameCompleted() || gameState.isGameOver();
    }

    private void finishEncounter(Encounter encounter, List<CombatEvent> events) {
        for (Hero hero : encounter.getParty().getMembers()) {
            hero.restoreHealth();
        }

        events.add(new CombatEvent("Incontro superato! Tutto il gruppo recupera la vita."));

        if (gameState.isGameCompleted()) {
            return;
        }

        int previousLevelIndex = gameState.getCurrentLevelIndex();

        boolean advanced = gameState.advanceEncounter();

        if (!advanced) {
            throw new IllegalStateException("Impossibile avanzare dopo l'incontro vinto.");
        }

        if (gameState.getCurrentLevelIndex() != previousLevelIndex) {
            prepareCurrentLevel(events);
        }
    }

    private void prepareCurrentLevel(List<CombatEvent> events) {
        LevelNumber number = gameState.getCurrentLevel().getNumber();
        Party party = gameState.getCurrentParty();

        if (party == null) {
            throw new IllegalStateException("Non è disponibile un gruppo per il livello corrente.");
        }

        levelPreparation.prepare(number, party);

        events.add(new CombatEvent("Inizio livello " + number + ": gruppo sbloccato e dotazioni preparate."));
    }
}
