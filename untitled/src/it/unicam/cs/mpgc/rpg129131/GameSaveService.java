package it.unicam.cs.mpgc.rpg129131;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Gestisce lettura e scrittura dei salvataggi JSON in UTF-8.
 * Delega al mapper la conversione e i controlli relativi al gioco.
 * Prima di sostituire il file di destinazione scrive un file temporaneo;
 * tenta uno spostamento atomico e, se non supportato, uno normale.
 * Non mostra finestre e non sostituisce la sessione dell’interfaccia.
 */
public class GameSaveService {

    /**
     * Converte e valida lo stato del gioco, senza occuparsi dei file.
     */
    private final GameSaveMapper mapper = new GameSaveMapper();

    /**
     * Serializzatore JSON con formattazione leggibile e sintassi rigorosa.
     * La correttezza del dominio viene verificata separatamente dal mapper.
     */
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setStrictness(Strictness.STRICT)
            .create();

    /**
     * Valida la sessione e scrive il salvataggio sul percorso indicato.
     * Prima di operare sui file verifica anche che i dati siano ricostruibili.
     * Crea le cartelle mancanti, scrive un temporaneo nella stessa cartella
     * e sostituisce la destinazione. Se lo spostamento atomico non è supportato,
     * il ripiego normale non offre la stessa garanzia di atomicità.
     *
     * @param file percorso del salvataggio, anche relativo alla directory di lavoro
     * @param session partita attiva da salvare
     * @throws IllegalArgumentException se il percorso è null
     * @throws IOException se lo stato non è salvabile o un’operazione sui file fallisce
     */
    public void save(Path file, GameSession session)
            throws IOException {

        if (file == null) {
            throw new IllegalArgumentException(
                    "Il percorso del salvataggio non può essere null."
            );
        }

        String json;

        try {
            GameSaveData data = mapper.toSaveData(session);

            // Verifica che lo stato possa essere ricaricato.
            mapper.fromSaveData(data);

            json = gson.toJson(data);
        } catch (RuntimeException e) {
            throw new IOException(
                    "Impossibile salvare la partita: " + e.getMessage(),
                    e
            );
        }

        // I percorsi relativi vengono risolti rispetto alla directory di lavoro.
        Path target = file.toAbsolutePath().normalize();
        Path parent = target.getParent();

        Files.createDirectories(parent);

        Path temporary = Files.createTempFile(
                parent,
                "rpg-",
                ".tmp"
        );

        try {
            Files.writeString(
                    temporary,
                    json,
                    StandardCharsets.UTF_8
            );

            try {
                Files.move(
                        temporary,
                        target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temporary,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            // Dopo lo spostamento il temporaneo non esiste più; in caso di errore lo elimina.
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Legge il JSON, controlla i campi primitivi del turno e ricostruisce la partita.
     * Restituisce una nuova sessione senza modificare quella eventualmente aperta.
     * Il chiamante decide se e quando sostituire la propria sessione.
     *
     * @param file percorso del salvataggio da leggere
     * @return sessione indipendente ripristinata
     * @throws IllegalArgumentException se il percorso è null
     * @throws IOException se lettura, sintassi JSON o dati della partita non sono validi
     */
    public GameSession load(Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(
                    "Il percorso del salvataggio non può essere null."
            );
        }

        String json = Files.readString(
                file,
                StandardCharsets.UTF_8
        );

        try {
            JsonObject root = gson.fromJson(
                    json,
                    JsonObject.class
            );

            if (root == null) {
                throw new IllegalArgumentException(
                        "Il file di salvataggio è vuoto."
                );
            }

            validateTurnFields(root);

            GameSaveData data = gson.fromJson(
                    root,
                    GameSaveData.class
            );

            return mapper.fromSaveData(data);
        } catch (RuntimeException e) {
            throw new IOException(
                    "Salvataggio non valido: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Verifica che il turno sia un oggetto con fase booleana e indici interi.
     * È necessario perché i componenti primitivi di TurnState non distinguono
     * un valore mancante dai valori predefiniti zero e false.
     *
     * @param root oggetto JSON principale del salvataggio
     * @throws IllegalArgumentException se un campo obbligatorio del turno non è valido
     */
    private void validateTurnFields(JsonObject root) {
        JsonElement turnElement = root.get("turn");

        if (turnElement == null || !turnElement.isJsonObject()) {
            throw new IllegalArgumentException(
                    "Lo stato del turno è mancante o non valido."
            );
        }

        JsonObject turn = turnElement.getAsJsonObject();
        JsonElement enemiesTurn = turn.get("enemiesTurn");

        if (enemiesTurn == null
                || !enemiesTurn.isJsonPrimitive()
                || !enemiesTurn.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(
                    "Il campo enemiesTurn è mancante o non valido."
            );
        }

        requireInteger(turn, "heroIndex");
        requireInteger(turn, "enemyIndex");
    }

    /**
     * Richiede un valore JSON numerico rappresentabile esattamente come int.
     * Rifiuta stringhe numeriche, valori frazionari e numeri fuori intervallo.
     * La non negatività è controllata successivamente da TurnState.
     *
     * @param object oggetto che deve contenere il campo
     * @param field nome del campo da verificare
     * @throws IllegalArgumentException se il campo manca o non rappresenta un intero valido
     */
    private void requireInteger(
            JsonObject object,
            String field
    ) {
        JsonElement value = object.get(field);

        if (value == null
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException(
                    "Il campo " + field
                            + " è mancante o non è numerico."
            );
        }

        try {
            value.getAsBigDecimal().intValueExact();
        } catch (ArithmeticException | NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Il campo " + field
                            + " deve essere un numero intero valido.",
                    e
            );
        }
    }
}
