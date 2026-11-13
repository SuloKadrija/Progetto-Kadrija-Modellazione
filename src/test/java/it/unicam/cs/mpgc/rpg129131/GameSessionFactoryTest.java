package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.game.GameFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameSession;
import it.unicam.cs.mpgc.rpg129131.game.GameSessionFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la costruzione delle sessioni senza avvio automatico
 * e senza alterazione dello stato fornito.
 */
class GameSessionFactoryTest {

    private final GameSessionFactory factory = new GameSessionFactory();

    /**
     * Due nuove sessioni devono avere stati ed eroi indipendenti.
     */
    @Test
    void createsIndependentUnstartedSessions() {
        GameSession first = factory.create();
        GameSession second = factory.create();

        assertFalse(first.isStarted());
        assertFalse(second.isStarted());

        assertNotSame(first.getGameState(), second.getGameState());
        assertNotSame(
                first.getGameState().getCurrentParty().getMembers().get(0),
                second.getGameState().getCurrentParty().getMembers().get(0)
        );

        assertDoesNotThrow(() -> first.startNewGame());
        assertFalse(second.isStarted());
    }

    /**
     * Collegare i servizi non deve sostituire o preparare lo stato ricevuto.
     */
    @Test
    void preservesProvidedStateWithoutPreparingIt() {
        GameState state = new GameFactory().create();
        var hero = state.getCurrentParty().getMembers().get(0);
        hero.restoreHealth(1);

        GameSession session = factory.create(state);

        assertSame(state, session.getGameState());
        assertFalse(session.isStarted());
        assertEquals(1, hero.getHealth());
        assertTrue(hero.getInventory().getItems().isEmpty());
    }

    /**
     * Uno stato nullo deve essere rifiutato.
     */
    @Test
    void rejectsNullState() {
        assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(null)
        );
    }
}