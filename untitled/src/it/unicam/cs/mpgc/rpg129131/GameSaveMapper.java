package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameSaveMapper {

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

    public GameSession fromSaveData(GameSaveData data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "I dati del salvataggio non possono essere null."
            );
        }

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

        session.restoreTurnState(data.turn());

        return session;
    }

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