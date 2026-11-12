package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateRestorePositionTest {

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

    @Test
    void restoresInitialPosition() {
        GameState state = new GameFactory().create();
        state.restorePosition(1, 2);

        state.restorePosition(0, 0);

        assertEquals(0, state.getCurrentLevelIndex());
        assertEquals(0, state.getCurrentEncounterIndex());
    }

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