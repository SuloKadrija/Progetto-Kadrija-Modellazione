package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionRestoreTurnTest {

    private GameSession createSession(GameState state) {
        CombatService combatService = new CombatService();
        RoundManager roundManager = new RoundManager(combatService);

        return new GameSession(
                state,
                new CombatController(roundManager),
                new LevelPreparation()
        );
    }

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