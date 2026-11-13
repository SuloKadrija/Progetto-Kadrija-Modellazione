package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.game.GameFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameState;
import it.unicam.cs.mpgc.rpg129131.model.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica che la composizione dei livelli sia protetta dalle modifiche esterne
 * e che le entità della partita non vengano duplicate.
 */
class GameStateLevelsTest {

    /**
     * Modificare la lista originale non deve alterare i livelli dello stato.
     */
    @Test
    void doesNotKeepMutableInputList() {
        List<Level> input =
                new ArrayList<>(new GameFactory().create().getLevels());

        int expectedSize = input.size();
        Level firstLevel = input.get(0);

        GameState state = new GameState(input);
        input.clear();

        assertEquals(expectedSize, state.getLevels().size());
        assertSame(firstLevel, state.getCurrentLevel());
    }

    /**
     * La lista restituita non deve permettere modifiche alla composizione.
     */
    @Test
    void returnedLevelsCannotBeModified() {
        GameState state = new GameFactory().create();

        assertThrows(
                UnsupportedOperationException.class,
                () -> state.getLevels().clear()
        );

        assertEquals(3, state.getLevels().size());
    }

    /**
     * Proteggere la lista non deve duplicare il livello e il suo stato di gioco.
     */
    @Test
    void preservesLevelIdentity() {
        GameState state = new GameFactory().create();

        assertSame(
                state.getCurrentLevel(),
                state.getLevels().get(0)
        );
    }
}