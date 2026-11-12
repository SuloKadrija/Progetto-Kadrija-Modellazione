package it.unicam.cs.mpgc.rpg129131;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveDataJsonTest {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setStrictness(Strictness.STRICT)
            .create();

    private GameSaveData createSampleData() {
        return new GameSaveData(
                GameSaveData.CURRENT_VERSION,
                0,
                0,
                new TurnState(false, 1, 0),
                List.of(
                        new GameSaveData.HeroData(
                                "Sam",
                                40,
                                true,
                                List.of("FireWeapon")
                        ),
                        new GameSaveData.HeroData(
                                "Dean",
                                60,
                                true,
                                List.of("FireWeapon", "Potion")
                        )
                ),
                List.of(
                        new GameSaveData.EncounterData(
                                0,
                                0,
                                List.of(20)
                        )
                )
        );
    }

    @Test
    void convertsDataToJsonAndBackWithoutLosingInformation() {
        GameSaveData original = createSampleData();

        String json = gson.toJson(original);
        GameSaveData restored =
                gson.fromJson(json, GameSaveData.class);

        assertEquals(original, restored);
    }

    @Test
    void preservesDefeatedEnemyAndEmptyInventory() {
        GameSaveData original = new GameSaveData(
                GameSaveData.CURRENT_VERSION,
                0,
                1,
                new TurnState(false, 0, 0),
                List.of(
                        new GameSaveData.HeroData(
                                "Sam", 10, true, List.of()
                        )
                ),
                List.of(
                        new GameSaveData.EncounterData(
                                0, 0, List.of(0)
                        ),
                        new GameSaveData.EncounterData(
                                0, 1, List.of(30)
                        )
                )
        );

        GameSaveData restored = gson.fromJson(
                gson.toJson(original),
                GameSaveData.class
        );

        assertEquals(original, restored);
        assertTrue(restored.heroes().get(0).items().isEmpty());
        assertEquals(
                List.of(0),
                restored.encounters().get(0).enemyHealth()
        );
    }

    @Test
    void rejectsUnsupportedFormatVersion() {
        GameSaveData valid = createSampleData();

        assertThrows(
                IllegalArgumentException.class,
                () -> new GameSaveData(
                        999,
                        valid.currentLevelIndex(),
                        valid.currentEncounterIndex(),
                        valid.turn(),
                        valid.heroes(),
                        valid.encounters()
                )
        );
    }

    @Test
    void rejectsMissingHeroHealth() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GameSaveData.HeroData(
                        "Sam", null, true, List.of()
                )
        );
    }

    @Test
    void rejectsNegativeEnemyHealth() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GameSaveData.EncounterData(
                        0, 0, List.of(-1)
                )
        );
    }

    @Test
    void copiesInventoryList() {
        List<String> items = new ArrayList<>();
        items.add("Potion");

        GameSaveData.HeroData hero =
                new GameSaveData.HeroData(
                        "Sam", 40, true, items
                );

        items.clear();

        assertEquals(List.of("Potion"), hero.items());
        assertThrows(
                UnsupportedOperationException.class,
                () -> hero.items().clear()
        );
    }
}