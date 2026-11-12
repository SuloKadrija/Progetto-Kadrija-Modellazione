package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.game.GameFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica indici, limiti e accesso non modificabile ai livelli.
 * I test isolano la posizione: non ricostruiscono una progressione completa
 * con incontri precedenti già vinti.
 */
class GameStateRestorePositionTest {

    /**
     * Verifica il ripristino di indici nei limiti e dell’incontro corrispondente.
     */
    @Test
    void restoresValidPosition() {
        GameState state = new GameFactory().create();

        state.restorePosition(1, 2);

        assertEquals(1, state.getCurrentLevelIndex());
        assertEquals(2, state.getCurrentEncounterIndex());

        assertSame(
                state.getLevels().get(1).getEncounters().get(2),
                state.getCurrentEncounter()
        );
    }

    /**
     * Verifica la possibilità di riportare entrambi gli indici alla posizione iniziale.
     */
    @Test
    void restoresInitialPosition() {
        GameState state = new GameFactory().create();
        state.restorePosition(1, 2);

        state.restorePosition(0, 0);

        assertEquals(0, state.getCurrentLevelIndex());
        assertEquals(0, state.getCurrentEncounterIndex());
    }

    /**
     * Verifica il rifiuto di un indice di livello negativo senza cambiare posizione.
     */
    @Test
    void rejectsNegativeLevelIndex() {
        GameState state = new GameFactory().create();

        assertThrows(
                IllegalArgumentException.class,
                () -> state.restorePosition(-1, 0)
        );

        assertEquals(0, state.getCurrentLevelIndex());
        assertEquals(0, state.getCurrentEncounterIndex());
    }

    /**
     * Verifica il rifiuto dell’indice immediatamente successivo all’ultimo livello.
     */
    @Test
    void rejectsLevelIndexOutsideAvailableLevels() {
        GameState state = new GameFactory().create();

        assertThrows(
                IllegalArgumentException.class,
                () -> state.restorePosition(
                        state.getLevels().size(), 0
                )
        );

        assertEquals(0, state.getCurrentLevelIndex());
        assertEquals(0, state.getCurrentEncounterIndex());
    }

    /**
     * Verifica che un incontro negativo non cambi neppure l’indice del livello precedente.
     */
    @Test
    void rejectsNegativeEncounterIndexWithoutChangingPosition() {
        GameState state = new GameFactory().create();
        state.restorePosition(1, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> state.restorePosition(2, -1)
        );

        assertEquals(1, state.getCurrentLevelIndex());
        assertEquals(1, state.getCurrentEncounterIndex());
    }

    /**
     * Verifica i limiti dell’incontro rispetto al livello selezionato e la mancata modifica dello stato.
     */
    @Test
    void rejectsEncounterIndexOutsideSelectedLevel() {
        GameState state = new GameFactory().create();
        state.restorePosition(1, 1);

        int encounterCount =
                state.getLevels().get(2).getEncounters().size();

        assertThrows(
                IllegalArgumentException.class,
                () -> state.restorePosition(2, encounterCount)
        );

        assertEquals(1, state.getCurrentLevelIndex());
        assertEquals(1, state.getCurrentEncounterIndex());
    }

    /**
     * Verifica che la lista dei livelli restituita non permetta la rimozione degli elementi.
     */
    @Test
    void returnedLevelsListCannotBeModified() {
        GameState state = new GameFactory().create();

        assertThrows(
                UnsupportedOperationException.class,
                () -> state.getLevels().clear()
        );

        assertEquals(3, state.getLevels().size());
    }
}
