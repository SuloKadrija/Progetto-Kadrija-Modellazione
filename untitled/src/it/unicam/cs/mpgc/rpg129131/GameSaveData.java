package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public record GameSaveData(
        Integer formatVersion,
        Integer currentLevelIndex,
        Integer currentEncounterIndex,
        TurnState turn,
        List<HeroData> heroes,
        List<EncounterData> encounters
) {

    public static final int CURRENT_VERSION = 1;

    public GameSaveData {
        if (formatVersion == null
                || formatVersion != CURRENT_VERSION) {
            throw new IllegalArgumentException(
                    "Versione del salvataggio non supportata."
            );
        }

        if (currentLevelIndex == null || currentLevelIndex < 0
                || currentEncounterIndex == null
                || currentEncounterIndex < 0) {
            throw new IllegalArgumentException(
                    "La posizione salvata non è valida."
            );
        }

        if (turn == null) {
            throw new IllegalArgumentException(
                    "Lo stato del turno è obbligatorio."
            );
        }

        if (heroes == null || heroes.isEmpty()
                || encounters == null || encounters.isEmpty()) {
            throw new IllegalArgumentException(
                    "Eroi e incontri del salvataggio sono obbligatori."
            );
        }

        heroes = List.copyOf(heroes);
        encounters = List.copyOf(encounters);
    }

    public record HeroData(
            String id,
            Integer health,
            Boolean unlocked,
            List<String> items
    ) {

        public HeroData {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException(
                        "L'identificatore dell'eroe è obbligatorio."
                );
            }

            if (health == null || health < 0) {
                throw new IllegalArgumentException(
                        "La salute dell'eroe non è valida."
                );
            }

            if (unlocked == null || items == null) {
                throw new IllegalArgumentException(
                        "Sblocco e inventario dell'eroe sono obbligatori."
                );
            }

            for (String item : items) {
                if (item == null || item.isBlank()) {
                    throw new IllegalArgumentException(
                            "L'identificatore di un oggetto non è valido."
                    );
                }
            }

            items = List.copyOf(items);
        }
    }

    public record EncounterData(
            Integer levelIndex,
            Integer encounterIndex,
            List<Integer> enemyHealth
    ) {

        public EncounterData {
            if (levelIndex == null || levelIndex < 0
                    || encounterIndex == null || encounterIndex < 0) {
                throw new IllegalArgumentException(
                        "La posizione dell'incontro non è valida."
                );
            }

            if (enemyHealth == null || enemyHealth.isEmpty()) {
                throw new IllegalArgumentException(
                        "La salute dei nemici è obbligatoria."
                );
            }

            for (Integer health : enemyHealth) {
                if (health == null || health < 0) {
                    throw new IllegalArgumentException(
                            "La salute di un nemico non è valida."
                    );
                }
            }

            enemyHealth = List.copyOf(enemyHealth);
        }
    }
}