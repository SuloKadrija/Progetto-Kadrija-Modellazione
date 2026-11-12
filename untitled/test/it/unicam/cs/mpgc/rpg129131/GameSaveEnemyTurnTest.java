package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica su file la ripresa della fase nemica a metà round e con
 * un nemico già sconfitto. Riduce la salute di alcuni bersagli soltanto
 * per preparare rapidamente gli scenari di test.
 */
class GameSaveEnemyTurnTest {

    /**
     * Cartella temporanea creata e ripulita da JUnit per isolare i file dei test.
     */
    @TempDir
    Path directory;

    /**
     * Servizio di salvataggio sottoposto a verifica.
     */
    private final GameSaveService service = new GameSaveService();
    /**
     * Convertitore usato per confrontare i dati prima e dopo il ripristino.
     */
    private final GameSaveMapper mapper = new GameSaveMapper();

    /**
     * Raggiunge il primo incontro del secondo livello con due nemici.
     * Riduce artificialmente la salute dei bersagli del primo livello per abbreviare il test.
     *
     * @return sessione attiva nel secondo livello
     */
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

    /**
     * Prepara un bersaglio con un punto salute e lo sconfigge tramite un’azione reale.
     * L’alterazione della salute serve solo ad abbreviare la preparazione del test.
     *
     * @param session sessione con un eroe corrente e un’arma efficace
     */
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

    /**
     * Fa agire ogni eroe rimasto usando la sua pozione, dopo averlo ferito di un punto.
     * Permette di entrare nella fase nemica senza uccidere altri nemici.
     *
     * @param session sessione attiva da portare alla fase nemica
     */
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

    /**
     * Verifica un solo evento di attacco e il ritorno alla fase degli eroi.
     * Il conteggio dipende dalla formulazione attuale dei messaggi degli eventi.
     *
     * @param session sessione dopo l’azione nemica
     * @param events eventi appena prodotti
     */
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

    /**
     * Salva dopo l’azione del primo nemico e verifica che il caricamento
     * conservi l’indice del secondo, la cui azione conclude il round.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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

    /**
     * Salva con il primo nemico sconfitto e verifica che, dopo il caricamento,
     * agisca soltanto quello ancora vivo prima dell’inizio del nuovo round.
     *
     * @throws IOException se la preparazione o l’accesso ai file fallisce
     */
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
