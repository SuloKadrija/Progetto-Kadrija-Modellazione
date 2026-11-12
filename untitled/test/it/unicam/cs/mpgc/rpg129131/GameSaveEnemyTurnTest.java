package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveEnemyTurnTest {

    @TempDir
    Path directory;

    private final GameSaveService service = new GameSaveService();
    private final GameSaveMapper mapper = new GameSaveMapper();

    private GameSession createSecondLevelSession() {
        GameSession session = new GameSession(
                new GameFactory().create(),
                new CombatController(
                        new RoundManager(new CombatService())
                ),
                new LevelPreparation()
        );

        session.startNewGame();

        for (int i = 0; i < 3; i++) {
            defeatFirstLivingEnemy(session);
        }

        assertEquals(
                1,
                session.getGameState().getCurrentLevelIndex()
        );

        assertEquals(
                2,
                session.getGameState()
                        .getCurrentEncounter().getEnemies().size()
        );

        return session;
    }

    private void defeatFirstLivingEnemy(GameSession session) {
        Hero hero = session.getCurrentHero();
        assertNotNull(hero);

        Enemy enemy = session.getGameState()
                .getCurrentEncounter()
                .getEnemies()
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

        assertFalse(enemy.isAlive());
    }

    private void finishHeroPhaseUsingPotions(GameSession session) {
        Hero hero;

        while ((hero = session.getCurrentHero()) != null) {
            hero.restoreHealth(hero.getMaxHealth() - 1);

            Potion potion = hero.getInventory()
                    .getItems()
                    .stream()
                    .filter(Potion.class::isInstance)
                    .map(Potion.class::cast)
                    .findFirst()
                    .orElseThrow();

            session.playHeroAction(
                    new PotionAction(hero, potion)
            );
        }

        assertTrue(session.getTurnState().enemiesTurn());
    }

    private void assertOneAttackAndNewRound(
            GameSession session,
            List<CombatEvent> events
    ) {
        long attacks = events.stream()
                .filter(event -> event.message().contains(" attacca "))
                .count();

        assertEquals(1L, attacks);
        assertFalse(session.getTurnState().enemiesTurn());
        assertEquals(0, session.getTurnState().enemyIndex());
        assertNotNull(session.getCurrentHero());
    }

    @Test
    void resumesAfterFirstEnemyWithoutRepeatingItsAction()
            throws IOException {

        GameSession original = createSecondLevelSession();

        finishHeroPhaseUsingPotions(original);

        original.playNextEnemyAction();

        assertTrue(original.getTurnState().enemiesTurn());
        assertEquals(1, original.getTurnState().enemyIndex());

        Path file = directory.resolve("turno-nemici.json");

        service.save(file, original);
        GameSession loaded = service.load(file);

        assertEquals(
                mapper.toSaveData(original),
                mapper.toSaveData(loaded)
        );

        assertNull(loaded.getCurrentHero());
        assertEquals(1, loaded.getTurnState().enemyIndex());

        List<CombatEvent> events =
                loaded.playNextEnemyAction();

        assertOneAttackAndNewRound(loaded, events);
    }

    @Test
    void skipsDefeatedEnemyAfterLoading()
            throws IOException {

        GameSession original = createSecondLevelSession();

        defeatFirstLivingEnemy(original);
        finishHeroPhaseUsingPotions(original);

        assertFalse(
                original.getGameState()
                        .getCurrentEncounter()
                        .getEnemies().get(0).isAlive()
        );

        Path file = directory.resolve("nemico-sconfitto.json");

        service.save(file, original);
        GameSession loaded = service.load(file);

        assertEquals(
                mapper.toSaveData(original),
                mapper.toSaveData(loaded)
        );

        List<Enemy> enemies = loaded.getGameState()
                .getCurrentEncounter().getEnemies();

        assertFalse(enemies.get(0).isAlive());
        assertTrue(enemies.get(1).isAlive());

        List<CombatEvent> events =
                loaded.playNextEnemyAction();

        assertOneAttackAndNewRound(loaded, events);
    }
}