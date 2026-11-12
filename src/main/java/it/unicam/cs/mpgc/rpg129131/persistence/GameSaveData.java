package it.unicam.cs.mpgc.rpg129131.persistence;

import it.unicam.cs.mpgc.rpg129131.game.TurnState;

import java.util.List;

/**
 * Raccoglie i dati persistiti di una partita in corso, separandoli dai servizi
 * e dalle viste. Conserva posizione, turno, eroi e salute dei nemici di tutti
 * gli incontri. Le statistiche fisse e la struttura dei livelli sono ricreate
 * da GameFactory, non memorizzate in questo record.
 * I tipi Integer e Boolean permettono di rilevare campi assenti o null.
 * I controlli qui sono strutturali: la coerenza con il gioco è verificata
 * da GameSaveMapper. Le liste vengono copiate in liste non modificabili.
 *
 * @param formatVersion versione del formato, attualmente 1
 * @param currentLevelIndex indice da zero del livello corrente
 * @param currentEncounterIndex indice da zero dell’incontro nel livello corrente
 * @param turn fase del round e indici dei partecipanti
 * @param heroes stato di tutti gli eroi, inclusi quelli non ancora sbloccati
 * @param encounters salute dei nemici di tutti gli incontri
 */
public record GameSaveData(
        Integer formatVersion,
        Integer currentLevelIndex,
        Integer currentEncounterIndex,
        TurnState turn,
        List<HeroData> heroes,
        List<EncounterData> encounters
) {

    /**
     * Versione del formato attualmente accettata dal gioco.
     */
    public static final int CURRENT_VERSION = 1;

    /**
     * Verifica versione, indici non negativi e presenza dei dati essenziali.
     * Conserva copie non modificabili delle liste senza ricostruire il gioco.
     * Non verifica qui identificatori duplicati, salute massima o progressione.
     *
     * @throws IllegalArgumentException se versione o dati obbligatori non sono validi
     * @throws NullPointerException se una lista contiene elementi null
     */
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

    /**
     * Conserva i dati variabili di un singolo eroe.
     * L’inventario contiene identificatori di oggetti, non istanze Java serializzate.
     *
     * @param id identificatore dell’eroe riconosciuto dal mapper
     * @param health salute corrente, anche zero
     * @param unlocked stato di sblocco
     * @param items identificatori degli oggetti nell’ordine dell’inventario
     */
    public record HeroData(
            String id,
            Integer health,
            Boolean unlocked,
            List<String> items
    ) {

        /**
         * Valida i dati essenziali dell’eroe e copia la lista degli oggetti.
         * Una lista vuota è valida; il limite massimo della salute e la validità
         * degli identificatori vengono controllati dal mapper.
         *
         * @throws IllegalArgumentException se un campo obbligatorio è assente o non valido
         */
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

    /**
     * Conserva la salute dei nemici di un incontro identificato dalla sua posizione.
     * L’ordine delle saluti corrisponde all’ordine dei nemici creati dalla factory.
     *
     * @param levelIndex indice da zero del livello
     * @param encounterIndex indice da zero dell’incontro nel livello
     * @param enemyHealth salute corrente di ciascun nemico, incluso zero per gli sconfitti
     */
    public record EncounterData(
            Integer levelIndex,
            Integer encounterIndex,
            List<Integer> enemyHealth
    ) {

        /**
         * Verifica indici non negativi e una lista non vuota di saluti valide.
         * Non controlla qui il numero effettivo dei nemici o le loro saluti massime.
         *
         * @throws IllegalArgumentException se indici o saluti non sono validi
         */
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
