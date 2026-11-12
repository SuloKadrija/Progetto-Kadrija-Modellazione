package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveMapperTest {

    private final GameSaveMapper mapper = new GameSaveMapper();

    private GameSession createStartedSession() {
        GameState state = new GameFactory().create();

        GameSession session = new GameSession(
                state,
                new CombatController(
                        new RoundManager(new CombatService())
                ),
                new LevelPreparation()
        );

        session.startNewGame();
        return session;
    }

    private GameSaveData withHeroes(
            GameSaveData original,
            List<GameSaveData.HeroData> heroes
    ) {
        return new GameSaveData(
                original.formatVersion(),
                original.currentLevelIndex(),
                original.currentEncounterIndex(),
                original.turn(),
                heroes,
                original.encounters()
        );
    }

    private void winCurrentEncounter(GameSession session) {
        Encounter encounter =
                session.getGameState().getCurrentEncounter();

        Hero hero = session.getCurrentHero();

        assertNotNull(hero);

        Enemy enemy = encounter.getEnemies()
                .stream()
                .filter(Enemy::isAlive)
                .findFirst()
                .orElseThrow();

        Weapon weapon = hero.getInventory()
                .getItems()
                .stream()
                .filter(Weapon.class::isInstance)
                .map(Weapon.class::cast)
                .filter(item -> item.getMultiplierAgainst(enemy) > 0)
                .findFirst()
                .orElseThrow();

        enemy.restoreHealth(1);

        session.playHeroAction(
                new AttackAction(hero, weapon, enemy)
        );
    }

    @Test
    void restoresInitialSessionIntoIndependentObjects() {
        GameSession original = createStartedSession();

        GameSaveData saved = mapper.toSaveData(original);
        GameSession restored = mapper.fromSaveData(saved);

        assertEquals(saved, mapper.toSaveData(restored));
        assertNotSame(
                original.getGameState(),
                restored.getGameState()
        );
        assertNotSame(
                original.getCurrentHero(),
                restored.getCurrentHero()
        );

        restored.getCurrentHero().restoreHealth(1);

        assertEquals(
                original.getCurrentHero().getMaxHealth(),
                original.getCurrentHero().getHealth()
        );
    }

    @Test
    void preservesConsumedPotionAndNextHeroTurn() {
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

        GameSaveData saved = mapper.toSaveData(original);
        GameSession restored = mapper.fromSaveData(saved);

        assertEquals(saved, mapper.toSaveData(restored));
        assertEquals(
                original.getCurrentHero().getName(),
                restored.getCurrentHero().getName()
        );

        Hero restoredSam = restored.getGameState()
                .getCurrentParty().getMembers().get(0);

        assertTrue(
                restoredSam.getInventory().getItems()
                        .stream()
                        .noneMatch(Potion.class::isInstance)
        );
    }

    @Test
    void restoresSecondLevelAndKeepsSharedHeroes() {
        GameSession original = createStartedSession();

        for (int i = 0; i < 3; i++) {
            winCurrentEncounter(original);
        }

        assertEquals(
                1,
                original.getGameState().getCurrentLevelIndex()
        );

        GameSaveData saved = mapper.toSaveData(original);
        GameSession restored = mapper.fromSaveData(saved);

        assertEquals(saved, mapper.toSaveData(restored));

        List<Level> levels = restored.getGameState().getLevels();

        Hero firstLevelSam = levels.get(0)
                .getEncounters().get(0)
                .getParty().getMembers().get(0);

        Hero secondLevelSam = levels.get(1)
                .getEncounters().get(0)
                .getParty().getMembers().get(0);

        assertSame(firstLevelSam, secondLevelSam);
    }

    @Test
    void rejectsDuplicateHero() {
        GameSaveData saved =
                mapper.toSaveData(createStartedSession());

        List<GameSaveData.HeroData> heroes =
                new ArrayList<>(saved.heroes());

        heroes.add(heroes.get(0));

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.fromSaveData(withHeroes(saved, heroes))
        );
    }

    @Test
    void rejectsHeroHealthAboveMaximum() {
        GameSession session = createStartedSession();
        GameSaveData saved = mapper.toSaveData(session);

        List<GameSaveData.HeroData> heroes =
                new ArrayList<>(saved.heroes());

        GameSaveData.HeroData sam = heroes.get(0);

        heroes.set(0, new GameSaveData.HeroData(
                sam.id(),
                session.getCurrentHero().getMaxHealth() + 1,
                sam.unlocked(),
                sam.items()
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.fromSaveData(withHeroes(saved, heroes))
        );
    }

    @Test
    void rejectsUnknownItem() {
        GameSaveData saved =
                mapper.toSaveData(createStartedSession());

        List<GameSaveData.HeroData> heroes =
                new ArrayList<>(saved.heroes());

        GameSaveData.HeroData sam = heroes.get(0);

        heroes.set(0, new GameSaveData.HeroData(
                sam.id(),
                sam.health(),
                sam.unlocked(),
                List.of("OggettoInesistente")
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.fromSaveData(withHeroes(saved, heroes))
        );
    }

    @Test
    void rejectsMissingEncounter() {
        GameSaveData saved =
                mapper.toSaveData(createStartedSession());

        List<GameSaveData.EncounterData> encounters =
                new ArrayList<>(saved.encounters());

        encounters.remove(0);

        GameSaveData incomplete = new GameSaveData(
                saved.formatVersion(),
                saved.currentLevelIndex(),
                saved.currentEncounterIndex(),
                saved.turn(),
                saved.heroes(),
                encounters
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.fromSaveData(incomplete)
        );
    }
}