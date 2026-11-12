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

public class GameSaveService {

    private final GameSaveMapper mapper = new GameSaveMapper();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setStrictness(Strictness.STRICT)
            .create();

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
            Files.deleteIfExists(temporary);
        }
    }

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