package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

/**
 * Gestisce il ciclo interattivo sincrono attraverso il contratto GameView.
 * Viene utilizzato dalla modalità console; l’interfaccia JavaFX utilizza
 * invece i propri gestori di eventi per invocare GameSession.
 */
public class GameRunner {

    /**
     * Sessione coordinata dal ciclo sincrono.
     */
    private final GameSession session;
    /**
     * Contratto di input e output, indipendente dalla vista concreta.
     */
    private final GameView view;

    /**
     * Collega una sessione alla vista utilizzata dal ciclo sincrono.
     *
     * @param session sessione nuova o già ripristinata
     * @param view vista per input e output
     * @throws IllegalArgumentException se sessione o vista è null
     */
    public GameRunner(GameSession session, GameView view) {
        if (session == null || view == null) {
            throw new IllegalArgumentException(
                    "La sessione e la vista non possono essere null."
            );
        }

        this.session = session;
        this.view = view;
    }

    /**
     * Avvia la sessione soltanto se necessario, poi esegue azioni fino alla fine.
     * Mostra lo stato, legge una singola azione dell’eroe oppure fa agire
     * un nemico, e visualizza immediatamente gli eventi prodotti.
     */
    public void run() {
        if (!session.isStarted()) {
            showMessages(session.startNewGame());
        }

        while (!session.isFinished()) {
            GameState gameState = session.getGameState();
            Encounter encounter = gameState.getCurrentEncounter();

            if (encounter == null) {
                throw new IllegalStateException(
                        "Non è disponibile un incontro corrente."
                );
            }

            view.showStatus(
                    gameState.getCurrentLevel().getNumber(),
                    gameState.getCurrentEncounterIndex() + 1,
                    encounter
            );

            Hero hero = session.getCurrentHero();

            List<CombatEvent> events;

            if (hero != null) {
                HeroAction action = view.readAction(hero, encounter);

                if (action == null) {
                    events = session.skipCurrentHero();
                } else {
                    events = session.playHeroAction(action);
                }
            } else {
                events = session.playNextEnemyAction();
            }

            view.showEvents(events);
        }

        showFinalResult();
    }

    /**
     * Mostra gli eventi come messaggi singoli attraverso la vista.
     *
     * @param events eventi da visualizzare nell’ordine ricevuto
     */
    private void showMessages(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            view.showMessage(event.message());
        }
    }

    /**
     * Mostra il risultato finale distinguendo vittoria e sconfitta.
     */
    private void showFinalResult() {
        GameState gameState = session.getGameState();

        if (gameState.isGameCompleted()) {
            view.showMessage(
                    "\nVITTORIA: hai completato tutti i livelli!"
            );
        } else if (gameState.isGameOver()) {
            view.showMessage(
                    "\nGAME OVER: il gruppo è stato sconfitto."
            );
        }
    }
}
