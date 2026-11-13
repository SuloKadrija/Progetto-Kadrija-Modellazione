package it.unicam.cs.mpgc.rpg129131;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unicam.cs.mpgc.rpg129131.combat.CombatController;
import it.unicam.cs.mpgc.rpg129131.combat.CombatService;
import it.unicam.cs.mpgc.rpg129131.combat.PotionAction;
import it.unicam.cs.mpgc.rpg129131.combat.RoundManager;
import it.unicam.cs.mpgc.rpg129131.game.GameFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameSession;
import it.unicam.cs.mpgc.rpg129131.game.LevelPreparation;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.persistence.GameSaveMapper;
import it.unicam.cs.mpgc.rpg129131.persistence.GameSaveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import it.unicam.cs.mpgc.rpg129131.persistence.GamePersistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica scrittura e lettura JSON su file temporanei, sovrascrittura
 * e gestione di errori di sintassi o validazione. Non modifica
 * il salvataggio personale nella cartella saves.
 */
class GameSaveServiceTest {

    /**
     * Cartella temporanea creata e ripulita da JUnit per isolare i file dei test.
     */
    @TempDir
    Path directory;

    /** Implementazione JSON verificata attraverso il contratto di persistenza. */
    private final GamePersistence service = new GameSaveService();

    /**
     * Convertitore usato per confrontare i dati prima e dopo il ripristino.
     */
    private final GameSaveMapper mapper =
            new GameSaveMapper();

    /**
     * Costruisce una sessione indipendente ma non ancora avviata.
     *
     * @return sessione senza preparazione iniziale
     */
    private GameSession createSession() {
        return new GameSession(
                new GameFactory().create(),
                new CombatController(
                        new RoundManager(new CombatService())
                ),
                new LevelPreparation()
        );
    }

    /**
     * Costruisce una sessione indipendente e ne avvia la preparazione iniziale.
     *
     * @return nuova partita attiva
     */
    private GameSession createStartedSession() {
        GameSession session = createSession();
        session.startNewGame();
        return session;
    }

    /**
     * Legge il file di prova come albero JSON per modificarne campi specifici.
     *
     * @param file file creato dal test
     * @return oggetto JSON del salvataggio
     * @throws IOException se la lettura fallisce
     */
    private JsonObject readJson(Path file) throws IOException {
        return JsonParser.parseString(
                Files.readString(file)
        ).getAsJsonObject();
    }

    /**
     * Scrive il JSON modificato per preparare un caso di errore del caricamento.
     *
     * @param file destinazione temporanea
     * @param json contenuto da scrivere
     * @throws IOException se la scrittura fallisce
     */
    private void writeJson(Path file, JsonObject json)
            throws IOException {
        Files.writeString(file, json.toString());
    }

    /**
     * Verifica il percorso completo sessione, file JSON e nuova sessione
     * conservando danno, turno e consumo della pozione.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Verifica che il servizio crei la cartella di destinazione quando manca.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
    @Test
    void createsMissingSaveDirectory() throws IOException {
        Path file = directory
                .resolve("saves")
                .resolve("partita.json");

        service.save(file, createStartedSession());

        assertTrue(Files.isRegularFile(file));
        assertNotNull(service.load(file));
    }

    /**
     * Verifica che un secondo salvataggio sullo stesso percorso aggiorni i dati caricabili.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Verifica che il caricamento di un file inesistente segnali un errore di input/output.
     */
    @Test
    void rejectsMissingFile() {
        Path file = directory.resolve("inesistente.json");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    /**
     * Verifica che un file con sintassi JSON non valida venga rifiutato.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
    @Test
    void rejectsMalformedJson() throws IOException {
        Path file = directory.resolve("corrotto.json");
        Files.writeString(file, "{ questo non è JSON");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    /**
     * Verifica che un file vuoto non venga interpretato come una partita valida.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
    @Test
    void rejectsEmptyFile() throws IOException {
        Path file = directory.resolve("vuoto.json");
        Files.writeString(file, "");

        assertThrows(
                IOException.class,
                () -> service.load(file)
        );
    }

    /**
     * Verifica che l’assenza di heroIndex non venga trasformata silenziosamente in zero.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Verifica il rifiuto di un indice del turno con parte frazionaria.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Verifica che il caricamento da file rifiuti una versione del formato sconosciuta.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Verifica che una sessione non avviata sia rifiutata prima della scrittura
     * e che il contenuto del salvataggio precedente rimanga invariato.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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
