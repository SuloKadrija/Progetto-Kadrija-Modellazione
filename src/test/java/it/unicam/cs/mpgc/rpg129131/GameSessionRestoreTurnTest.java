package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.combat.CombatController;
import it.unicam.cs.mpgc.rpg129131.combat.CombatService;
import it.unicam.cs.mpgc.rpg129131.combat.RoundManager;
import it.unicam.cs.mpgc.rpg129131.game.*;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il ripristino dei dati del turno in una sessione non avviata.
 * Riutilizza uno stato già in memoria per isolare il turno; non costituisce
 * una verifica della ricostruzione dell’intero gioco dal disco.
 */
class GameSessionRestoreTurnTest {

    /**
     * Costruisce una sessione non avviata sullo stato fornito, senza preparare dotazioni.
     *
     * @param state stato da associare alla nuova sessione
     * @return sessione pronta per avvio o ripristino
     */
    private GameSession createSession(GameState state) {
        CombatService combatService = new CombatService();
        RoundManager roundManager = new RoundManager(combatService);

        return new GameSession(
                state,
                new CombatController(roundManager),
                new LevelPreparation()
        );
    }

    /**
     * Verifica che il ripristino del turno non rigeneri inventario o salute.
     */
    @Test
    void restoresTurnWithoutPreparingInventoryAgain() {
        GameState state = new GameFactory().create();

        GameSession original = createSession(state);
        original.startNewGame();

        Hero sam = original.getCurrentHero();
        sam.getInventory().restoreItems(java.util.List.of());
        sam.restoreHealth(1);

        TurnState savedTurn = original.getTurnState();

        GameSession restored = createSession(state);
        restored.restoreTurnState(savedTurn);

        assertTrue(restored.isStarted());
        assertEquals(savedTurn, restored.getTurnState());
        assertSame(sam, restored.getCurrentHero());
        assertEquals(1, sam.getHealth());
        assertTrue(sam.getInventory().getItems().isEmpty());
    }

    /**
     * Verifica il ripristino della posizione del secondo eroe nel turno.
     */
    @Test
    void restoresSecondHeroTurn() {
        GameState state = new GameFactory().create();

        GameSession original = createSession(state);
        original.startNewGame();

        Hero firstHero = original.getCurrentHero();
        firstHero.getInventory().restoreItems(java.util.List.of());
        original.skipCurrentHero();

        Hero expectedHero = original.getCurrentHero();
        TurnState savedTurn = original.getTurnState();

        GameSession restored = createSession(state);
        restored.restoreTurnState(savedTurn);

        assertSame(expectedHero, restored.getCurrentHero());
        assertEquals(1, restored.getTurnState().heroIndex());
    }

    /**
     * Verifica il ripristino della fase nemica e la possibilità di eseguire l’azione successiva.
     */
    @Test
    void restoresEnemyTurnAndAllowsNextEnemyAction() {
        GameState state = new GameFactory().create();

        GameSession original = createSession(state);
        original.startNewGame();

        while (original.getCurrentHero() != null) {
            original.getCurrentHero()
                    .getInventory()
                    .restoreItems(java.util.List.of());

            original.skipCurrentHero();
        }

        TurnState savedTurn = original.getTurnState();

        GameSession restored = createSession(state);
        restored.restoreTurnState(savedTurn);

        assertNull(restored.getCurrentHero());
        assertEquals(savedTurn, restored.getTurnState());
        assertDoesNotThrow(() -> restored.playNextEnemyAction());
    }

    /**
     * Verifica che una sessione già avviata rifiuti il ripristino del turno senza alterarlo.
     */
    @Test
    void rejectsRestoreOnStartedSession() {
        GameSession session =
                createSession(new GameFactory().create());

        session.startNewGame();
        TurnState previousTurn = session.getTurnState();

        assertThrows(
                IllegalStateException.class,
                () -> session.restoreTurnState(previousTurn)
        );

        assertEquals(previousTurn, session.getTurnState());
    }

    /**
     * Verifica il rifiuto di un indice oltre il gruppo mantenendo la sessione non avviata.
     */
    @Test
    void rejectsInvalidHeroIndexWithoutStartingSession() {
        GameState state = new GameFactory().create();
        GameSession session = createSession(state);

        int heroCount = state.getCurrentParty().getMembers().size();

        assertThrows(
                IllegalArgumentException.class,
                () -> session.restoreTurnState(
                        new TurnState(false, heroCount, 0)
                )
        );

        assertFalse(session.isStarted());
    }

    /**
     * Verifica che il turno degli eroi non possa essere ripristinato su un eroe sconfitto.
     */
    @Test
    void rejectsDefeatedCurrentHero() {
        GameState state = new GameFactory().create();
        state.getCurrentParty().getMembers().get(0).restoreHealth(0);

        GameSession session = createSession(state);

        assertThrows(
                IllegalArgumentException.class,
                () -> session.restoreTurnState(
                        new TurnState(false, 0, 0)
                )
        );

        assertFalse(session.isStarted());
    }

    /**
     * Verifica il rifiuto di un indice oltre i nemici senza avviare la sessione.
     */
    @Test
    void rejectsInvalidEnemyIndexWithoutStartingSession() {
        GameState state = new GameFactory().create();
        GameSession session = createSession(state);

        int heroCount = state.getCurrentParty().getMembers().size();
        int enemyCount = state.getCurrentEncounter().getEnemies().size();

        assertThrows(
                IllegalArgumentException.class,
                () -> session.restoreTurnState(
                        new TurnState(true, heroCount, enemyCount)
                )
        );

        assertFalse(session.isStarted());
    }

    /**
     * Verifica il rifiuto di uno stato del turno assente.
     */
    @Test
    void rejectsNullTurnState() {
        GameSession session =
                createSession(new GameFactory().create());

        assertThrows(
                IllegalArgumentException.class,
                () -> session.restoreTurnState(null)
        );

        assertFalse(session.isStarted());
    }

    /**
     * Verifica che il record del turno rifiuti indici negativi.
     */
    @Test
    void rejectsNegativeTurnIndices() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TurnState(false, -1, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TurnState(true, 2, -1)
        );
    }
}
