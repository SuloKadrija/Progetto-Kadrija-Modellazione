package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converte una sessione attiva in dati di salvataggio e ricostruisce
 * una nuova sessione a partire da quei dati. Non gestisce JSON o filesystem.
 * Durante il ripristino ricrea la struttura con GameFactory e controlla
 * identificatori, salute, sblocchi, progressione e turno.
 * Gli identificatori di eroi e oggetti coincidono con i nomi semplici
 * delle classi: rinomine e cambiamenti strutturali richiedono attenzione
 * alla compatibilità dei salvataggi. Il generatore casuale viene ricreato.
 */
public class GameSaveMapper {

    /**
     * Estrae i dati da una sessione attiva senza modificarla.
     * Registra ogni eroe una sola volta e include tutti gli incontri,
     * anche quelli precedenti e futuri.
     *
     * @param session partita attiva da convertire
     * @return fotografia dei dati da serializzare
     * @throws IllegalArgumentException se la sessione è null
     * @throws IllegalStateException se la sessione non è attiva o esistono eroi distinti con lo stesso identificatore
     */
    public GameSaveData toSaveData(GameSession session) {
        if (session == null) {
            throw new IllegalArgumentException(
                    "La sessione non può essere null."
            );
        }

        TurnState turn = session.getTurnState();
        GameState state = session.getGameState();

        List<GameSaveData.HeroData> heroes = new ArrayList<>();

        for (Map.Entry<String, Hero> entry
                : collectHeroes(state).entrySet()) {
            Hero hero = entry.getValue();

            List<String> items = hero.getInventory()
                    .getItems()
                    .stream()
                    .map(item -> item.getClass().getSimpleName())
                    .toList();

            heroes.add(new GameSaveData.HeroData(
                    entry.getKey(),
                    hero.getHealth(),
                    hero.isUnlocked(),
                    items
            ));
        }

        List<GameSaveData.EncounterData> encounters =
                new ArrayList<>();

        List<Level> levels = state.getLevels();

        for (int levelIndex = 0;
             levelIndex < levels.size();
             levelIndex++) {

            List<Encounter> levelEncounters =
                    levels.get(levelIndex).getEncounters();

            for (int encounterIndex = 0;
                 encounterIndex < levelEncounters.size();
                 encounterIndex++) {

                List<Integer> enemyHealth = levelEncounters
                        .get(encounterIndex)
                        .getEnemies()
                        .stream()
                        .map(Enemy::getHealth)
                        .toList();

                encounters.add(new GameSaveData.EncounterData(
                        levelIndex,
                        encounterIndex,
                        enemyHealth
                ));
            }
        }

        return new GameSaveData(
                GameSaveData.CURRENT_VERSION,
                state.getCurrentLevelIndex(),
                state.getCurrentEncounterIndex(),
                turn,
                heroes,
                encounters
        );
    }

    /**
     * Ricostruisce una nuova partita indipendente applicando i dati salvati.
     * Ripristina prima posizione, eroi e nemici, verifica la progressione
     * e infine ripristina il turno senza chiamare startNewGame.
     * Se un controllo fallisce, la nuova sessione non viene restituita.
     *
     * @param data dati del salvataggio già costruiti
     * @return nuova sessione avviata e pronta a proseguire
     * @throws IllegalArgumentException se i dati sono null, incompleti o incoerenti
     * @throws IllegalStateException se lo stato finale non consente di riprendere il turno
     */
    public GameSession fromSaveData(GameSaveData data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "I dati del salvataggio non possono essere null."
            );
        }

        // La factory mantiene i riferimenti condivisi degli eroi fra i diversi gruppi.
        GameState state = new GameFactory().create();

        state.restorePosition(
                data.currentLevelIndex(),
                data.currentEncounterIndex()
        );

        restoreHeroes(state, data.heroes());
        restoreEncounters(state, data.encounters());
        validateProgress(state);

        CombatService combatService = new CombatService();
        RoundManager roundManager = new RoundManager(combatService);

        GameSession session = new GameSession(
                state,
                new CombatController(roundManager),
                new LevelPreparation()
        );

        // Ripristina il turno per ultimo: non rigenera armi, pozioni o salute.
        session.restoreTurnState(data.turn());

        return session;
    }

    /**
     * Raccoglie gli eroi per identificatore mantenendo l’ordine di prima comparsa.
     * Riferimenti ripetuti allo stesso eroe sono ammessi; due istanze distinte
     * con lo stesso identificatore vengono rifiutate.
     *
     * @param state stato dal quale attraversare gruppi e incontri
     * @return mappa degli eroi senza duplicare le istanze condivise
     * @throws IllegalStateException se l’identificatore non individua un’unica istanza
     */
    private Map<String, Hero> collectHeroes(GameState state) {
        Map<String, Hero> heroes = new LinkedHashMap<>();

        for (Level level : state.getLevels()) {
            for (Encounter encounter : level.getEncounters()) {
                for (Hero hero : encounter.getParty().getMembers()) {
                    String id = hero.getClass().getSimpleName();
                    Hero previous = heroes.putIfAbsent(id, hero);

                    if (previous != null && previous != hero) {
                        throw new IllegalStateException(
                                "Identificatore eroe duplicato: " + id
                        );
                    }
                }
            }
        }

        return heroes;
    }

    /**
     * Richiede esattamente gli eroi previsti dalla factory, senza duplicati.
     * Ripristina salute, sblocco coerente con il livello e inventario.
     * La struttura modificata è quella nuova costruita per il caricamento.
     *
     * @param state nuovo stato di gioco con posizione già ripristinata
     * @param savedHeroes dati di tutti gli eroi
     * @throws IllegalArgumentException se eroi, sblocchi, saluti o oggetti non sono validi
     */
    private void restoreHeroes(
            GameState state,
            List<GameSaveData.HeroData> savedHeroes
    ) {
        Map<String, Hero> expectedHeroes = collectHeroes(state);
        Map<String, GameSaveData.HeroData> savedById =
                new HashMap<>();

        for (GameSaveData.HeroData saved : savedHeroes) {
            if (savedById.putIfAbsent(saved.id(), saved) != null) {
                throw new IllegalArgumentException(
                        "Eroe duplicato nel salvataggio: " + saved.id()
                );
            }
        }

        if (!savedById.keySet().equals(expectedHeroes.keySet())) {
            throw new IllegalArgumentException(
                    "Il salvataggio contiene eroi mancanti o sconosciuti."
            );
        }

        for (Map.Entry<String, Hero> entry
                : expectedHeroes.entrySet()) {
            Hero hero = entry.getValue();
            GameSaveData.HeroData saved =
                    savedById.get(entry.getKey());

            boolean expectedUnlocked =
                    shouldBeUnlocked(state, hero);

            if (saved.unlocked() != expectedUnlocked) {
                throw new IllegalArgumentException(
                        "Stato di sblocco non coerente per "
                                + saved.id() + "."
                );
            }

            hero.restoreHealth(saved.health());

            if (saved.unlocked()) {
                hero.unlock();
            }

            List<Item> items = new ArrayList<>();

            for (String itemId : saved.items()) {
                items.add(createItem(itemId));
            }

            hero.getInventory().restoreItems(items);
        }
    }

    /**
     * Determina lo sblocco atteso dalla presenza dell’eroe nei livelli
     * raggiunti fino a quello corrente, compreso. Un eroe già sbloccato
     * resta tale anche se non appartiene al gruppo del livello attuale.
     *
     * @param state stato con posizione corrente valida
     * @param hero istanza dell’eroe da cercare
     * @return true se l’eroe compare in un livello già raggiunto
     */
    private boolean shouldBeUnlocked(
            GameState state,
            Hero hero
    ) {
        for (int i = 0; i <= state.getCurrentLevelIndex(); i++) {
            for (Encounter encounter
                    : state.getLevels().get(i).getEncounters()) {
                if (encounter.getParty().getMembers().contains(hero)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Ricostruisce un oggetto usando esclusivamente gli identificatori riconosciuti.
     * Non carica classi arbitrarie dal testo del salvataggio. Per supportare
     * un nuovo tipo di oggetto bisogna aggiornare anche questa selezione.
     *
     * @param id nome semplice della classe dell’oggetto
     * @return nuova istanza dell’oggetto
     * @throws IllegalArgumentException se l’identificatore è sconosciuto
     */
    private Item createItem(String id) {
        return switch (id) {
            case "Knife" -> new Knife();
            case "Rifle" -> new Rifle();
            case "WoodenStake" -> new WoodenStake();
            case "Salt" -> new Salt();
            case "FireWeapon" -> new FireWeapon();
            case "HolyWater" -> new HolyWater();
            case "DemoniacKnife" -> new DemoniacKnife();
            case "DemoniacArrow" -> new DemoniacArrow();
            case "AngelicSword" -> new AngelicSword();
            case "AngelicArrow" -> new AngelicArrow();
            case "AngelicKnife" -> new AngelicKnife();
            case "Quote" -> new Quote();
            case "Potion" -> new Potion();
            default -> throw new IllegalArgumentException(
                    "Oggetto sconosciuto nel salvataggio: " + id
            );
        };
    }

    /**
     * Indicizza gli incontri salvati e richiede tutte e sole le posizioni previste.
     * Verifica il numero dei nemici e ne ripristina la salute nello stesso ordine.
     *
     * @param state nuova struttura degli incontri
     * @param savedEncounters dati di tutti gli incontri
     * @throws IllegalArgumentException se posizioni, quantità o saluti sono incoerenti
     */
    private void restoreEncounters(
            GameState state,
            List<GameSaveData.EncounterData> savedEncounters
    ) {
        Map<String, GameSaveData.EncounterData> savedByPosition =
                new HashMap<>();

        for (GameSaveData.EncounterData saved : savedEncounters) {
            String key = saved.levelIndex()
                    + ":" + saved.encounterIndex();

            if (savedByPosition.putIfAbsent(key, saved) != null) {
                throw new IllegalArgumentException(
                        "Incontro duplicato nel salvataggio: " + key
                );
            }
        }

        List<Level> levels = state.getLevels();

        for (int levelIndex = 0;
             levelIndex < levels.size();
             levelIndex++) {

            List<Encounter> encounters =
                    levels.get(levelIndex).getEncounters();

            for (int encounterIndex = 0;
                 encounterIndex < encounters.size();
                 encounterIndex++) {

                // La rimozione permette di rilevare alla fine anche incontri estranei.
                String key = levelIndex + ":" + encounterIndex;

                GameSaveData.EncounterData saved =
                        savedByPosition.remove(key);

                if (saved == null) {
                    throw new IllegalArgumentException(
                            "Incontro mancante nel salvataggio: " + key
                    );
                }

                List<Enemy> enemies =
                        encounters.get(encounterIndex).getEnemies();

                if (saved.enemyHealth().size() != enemies.size()) {
                    throw new IllegalArgumentException(
                            "Numero di nemici non valido nell'incontro "
                                    + key + "."
                    );
                }

                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).restoreHealth(
                            saved.enemyHealth().get(i)
                    );
                }
            }
        }

        if (!savedByPosition.isEmpty()) {
            throw new IllegalArgumentException(
                    "Il salvataggio contiene incontri sconosciuti."
            );
        }
    }

    /**
     * Richiede incontri precedenti completati, incontro corrente non completato
     * e nemici futuri ancora a salute massima. Non prova che ogni dettaglio
     * dell’inventario sia raggiungibile attraverso una partita realmente giocata.
     *
     * @param state stato dopo il ripristino di posizione e saluti
     * @throws IllegalArgumentException se la progressione degli incontri è incoerente
     */
    private void validateProgress(GameState state) {
        List<Level> levels = state.getLevels();

        for (int levelIndex = 0;
             levelIndex < levels.size();
             levelIndex++) {

            List<Encounter> encounters =
                    levels.get(levelIndex).getEncounters();

            for (int encounterIndex = 0;
                 encounterIndex < encounters.size();
                 encounterIndex++) {

                Encounter encounter = encounters.get(encounterIndex);

                boolean previous =
                        levelIndex < state.getCurrentLevelIndex()
                                || (levelIndex == state.getCurrentLevelIndex()
                                && encounterIndex
                                < state.getCurrentEncounterIndex());

                boolean future =
                        levelIndex > state.getCurrentLevelIndex()
                                || (levelIndex == state.getCurrentLevelIndex()
                                && encounterIndex
                                > state.getCurrentEncounterIndex());

                if (previous && !encounter.isCompleted()) {
                    throw new IllegalArgumentException(
                            "Un incontro precedente non è completato."
                    );
                }

                if (!previous && !future && encounter.isCompleted()) {
                    throw new IllegalArgumentException(
                            "L'incontro corrente è già completato."
                    );
                }

                if (future) {
                    for (Enemy enemy : encounter.getEnemies()) {
                        if (enemy.getHealth() != enemy.getMaxHealth()) {
                            throw new IllegalArgumentException(
                                    "Un incontro futuro contiene "
                                            + "nemici già danneggiati."
                            );
                        }
                    }
                }
            }
        }
    }
}
