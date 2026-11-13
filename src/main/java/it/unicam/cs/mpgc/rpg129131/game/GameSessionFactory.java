package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.combat.CombatController;
import it.unicam.cs.mpgc.rpg129131.combat.CombatService;
import it.unicam.cs.mpgc.rpg129131.combat.RoundManager;

/**
 * Costruisce le sessioni collegando i servizi necessari al gioco.
 * Centralizza la configurazione condivisa da console, JavaFX e caricamento.
 * Non avvia la partita e non ripristina il turno o i dati salvati.
 */
public class GameSessionFactory {

    /**
     * Costruisce una sessione con un nuovo stato iniziale.
     * Ogni chiamata crea uno stato e servizi indipendenti.
     *
     * @return nuova sessione non ancora avviata
     */
    public GameSession create() {
        return create(new GameFactory().create());
    }

    /**
     * Collega nuovi servizi allo stato fornito, senza modificarlo o copiarlo.
     * Il chiamante decide se avviare la partita o ripristinare il turno.
     *
     * @param state stato da associare alla sessione
     * @return sessione non ancora avviata che usa lo stato fornito
     * @throws IllegalArgumentException se lo stato è null
     */
    public GameSession create(GameState state) {
        if (state == null) {
            throw new IllegalArgumentException(
                    "Lo stato della partita non può essere null."
            );
        }

        CombatService combatService = new CombatService();
        RoundManager roundManager = new RoundManager(combatService);
        CombatController combatController =
                new CombatController(roundManager);

        return new GameSession(
                state,
                combatController,
                new LevelPreparation()
        );
    }
}