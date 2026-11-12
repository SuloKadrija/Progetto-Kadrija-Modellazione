package it.unicam.cs.mpgc.rpg129131;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveServiceTest {

    @TempDir
    Path directory;

    private final GameSaveService service =
            new GameSaveService();

    private final GameSaveMapper mapper =
            new GameSaveMapper();

    private GameSession createSession() {
        return new GameSession(
                new GameFactory().create(),
                new CombatController(
                        new RoundManager(new CombatService())
                ),
                new LevelPreparation()
        );
    }

    private GameSession createStartedSession() {
        GameSession session = createSession();
        session.startNewGame();
        return session;
    }

    private JsonObject readJson(Path file) throws IOException {
        return JsonParser.parseString(
                Files.readString(file)
        ).getAsJsonObject();
    }

    private void writeJson(Path file, JsonObject json)
            throws IOException {
        Files.writeString(file, json.toString());
    }

    @Test
    void savesAndLoadsIndependentSession() throws IOException {
        GameSession original = createStartedSession();

        Hero sam = original.getCurrentHero();
        sam.restoreHealth(sam.getMaxHealth() - 1);

        Potion potion = sam.getInventory()
                .getItems()
                .stream()
                .filter(Potion.class::isInstance)
                .map(Potion.class::cast)
                .findFirst()
                .orElseThrow();

        original.playHeroAction(new PotionAction(sam, potion));

        Enemy enemy = original.getGameState()
                .getCurrentEncounter().getEnemies().get(0);

        enemy.restoreHealth(enemy.getMaxHealth() - 1);

        Path file = directory.resolve("partita.json");

        service.save(file, original);
        GameSession loaded = service.load(file);

        assertTrue(Files.exists(file));
        assertEquals(
                mapper.toSaveData(original),
                mapper.toSaveData(loaded)
        );

        assertNotSame(
                original.getGameState(),
                loaded.getGameState()
        );

        Hero loadedSam = loaded.getGameState()
                .getCurrentParty().getMembers().get(0);

        assertTrue(
                loadedSam.getInventory().getItems()
                        .stream()
                        .noneMatch(Potion.class::isInstance)
        );
    }

    @Test
    void createsMissingSaveDirectory() throws IOException {
        Path file = directory
                .resolve("saves")
                .resolve("partita.json");

        service.save(file, createStartedSession());

        assertTrue(Files.isRegularFile(file));
        assertNotNull(service.load(file));
    }

    @Test
    void replacesExistingSave() throws IOException {
        GameSession session = createStartedSession();
        Path file = directory.resolve("partita.json");

        service.save(file, session);

        session.getCurrentHero().restoreHealth(1);
        service.save(file, session);

        GameSession loaded = service.load(file);

        assertEquals(1, loaded.getCurrentHero().getHealth());
    }

    @Test
    void rejectsMissingFile() {
        Path file = directory.resolve("inesistente.json");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void rejectsMalformedJson() throws IOException {
        Path file = directory.resolve("corrotto.json");
        Files.writeString(file, "{ questo non è JSON");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void rejectsEmptyFile() throws IOException {
        Path file = directory.resolve("vuoto.json");
        Files.writeString(file, "");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void rejectsMissingTurnField() throws IOException {
        Path file = directory.resolve("partita.json");
        service.save(file, createStartedSession());

        JsonObject json = readJson(file);
        json.getAsJsonObject("turn").remove("heroIndex");
        writeJson(file, json);

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void rejectsFractionalTurnIndex() throws IOException {
        Path file = directory.resolve("partita.json");
        service.save(file, createStartedSession());

        JsonObject json = readJson(file);
        json.getAsJsonObject("turn")
                .addProperty("heroIndex", 0.5);
        writeJson(file, json);

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void rejectsUnsupportedVersion() throws IOException {
        Path file = directory.resolve("partita.json");
        service.save(file, createStartedSession());

        JsonObject json = readJson(file);
        json.addProperty("formatVersion", 999);
        writeJson(file, json);

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    @Test
    void failedValidationDoesNotOverwritePreviousSave()
            throws IOException {

        Path file = directory.resolve("partita.json");
        service.save(file, createStartedSession());

        String previousContent = Files.readString(file);

        GameSession notStarted = createSession();

        assertThrows(
                IOException.class,
                () -> service.save(file, notStarted)
        );

        assertEquals(
                previousContent,
                Files.readString(file)
        );

        assertNotNull(service.load(file));
    }
}