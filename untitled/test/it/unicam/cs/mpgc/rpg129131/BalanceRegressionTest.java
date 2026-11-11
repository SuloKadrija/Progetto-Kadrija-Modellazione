package it.unicam.cs.mpgc.rpg129131;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceRegressionTest {

    private final CombatService combatService = new CombatService();

    @Test
    void heroStatisticsMatchApprovedRevision() {
        checkStatistics(new Sam(), 120, 24, 12);
        checkStatistics(new Dean(), 130, 23, 15);
        checkStatistics(new Bobby(), 115, 25, 10);
        checkStatistics(new Castiel(), 125, 24, 13);
        checkStatistics(new Jack(), 110, 27, 9);
    }

    @Test
    void enemyStatisticsMatchApprovedRevision() {
        checkStatistics(new Ghost(), 180, 28, 8);
        checkStatistics(new Vampire(), 240, 34, 10);
        checkStatistics(new Wendigo(), 280, 40, 12);
        checkStatistics(new Demon(), 220, 40, 12);
        checkStatistics(new YellowEyedDemon(), 400, 45, 15);
        checkStatistics(new Lucifer(), 550, 48, 18);
        checkStatistics(new Leviathan(), 240, 44, 16);
        checkStatistics(new Angel(), 260, 48, 18);
        checkStatistics(new God(), 600, 55, 20);
    }

    @Test
    void weaponMultipliersMatchApprovedRevision() {
        Weapon[] weapons = {
                new Knife(), new Rifle(), new WoodenStake(), new Salt(),
                new FireWeapon(), new HolyWater(), new DemoniacKnife(),
                new DemoniacArrow(), new AngelicSword(), new AngelicArrow(),
                new AngelicKnife(), new Quote()
        };

        Enemy[] enemies = {
                new Ghost(), new Vampire(), new Wendigo(), new Demon(),
                new YellowEyedDemon(), new Lucifer(), new Leviathan(),
                new Angel(), new God()
        };

        double[] bases = {
                1.2, 1.4, 1.4, 1.4, 1.4, 1.4,
                1.4, 1.5, 1.4, 1.4, 1.4, 1.2
        };

        double[][] effectiveness = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0.5, 2, 0.8, 1, 1, 1, 1, 1, 1},
                {2, 0.5, 0.5, 1, 1, 1, 1, 1, 1},
                {1.3, 0.8, 2, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 2, 1.2, 0.6, 1, 1, 1},
                {1, 1, 1, 1.3, 2, 1, 1, 1, 1},
                {1, 1, 1, 1.6, 1.6, 1.3, 1, 1, 1},
                {1, 1, 1, 1, 1, 2, 1.5, 1.2, 1.6},
                {1, 1, 1, 1, 1, 1, 2, 1.5, 0.8},
                {1, 1, 1, 1, 1, 1, 1.3, 2, 0.8},
                {0, 0, 0, 0, 0, 0, 0, 0, 2}
        };

        for (int i = 0; i < weapons.length; i++) {
            assertEquals(bases[i], weapons[i].getBaseMultiplier(),
                    0.000001, weapons[i].getName());

            for (int j = 0; j < enemies.length; j++) {
                assertEquals(effectiveness[i][j],
                        weapons[i].getMultiplierAgainst(enemies[j]),
                        0.000001,
                        weapons[i].getName() + " / " + enemies[j].getName());
            }
        }
    }

    @Test
    void specialistDamageMatchesApprovedValues() {
        checkDamage(new Salt(), new Ghost(), 59, 56);
        checkDamage(new WoodenStake(), new Vampire(), 57, 54);
        checkDamage(new FireWeapon(), new Wendigo(), 55, 52);
        checkDamage(new HolyWater(), new Demon(), 55, 52);
        checkDamage(new DemoniacKnife(), new YellowEyedDemon(), 52, 49);
        checkDamage(new AngelicSword(), new Lucifer(), 49, 46);
        checkDamage(new AngelicArrow(), new Leviathan(), 51, 48);
        checkDamage(new AngelicKnife(), new Angel(), 49, 46);
        checkDamage(new Quote(), new God(), 38, 35);
        assertEquals(38, combatService.calculateDamage(
                new Castiel(), new Quote(), new God()));
        assertEquals(45, combatService.calculateDamage(
                new Jack(), new Quote(), new God()));
    }

    @Test
    void damageIsRoundedBeforeDefenseIsSubtracted() {
        Weapon weapon = new Weapon("Arma di prova", 1.0625, Map.of());
        assertEquals(18, combatService.calculateDamage(
                new Sam(), weapon, new Ghost()));
    }

    @Test
    void effectiveAttackHasMinimumDamageOfOne() {
        Weapon weapon = new Weapon("Arma debole", 0.01, Map.of());
        assertEquals(1, combatService.calculateDamage(
                new Sam(), weapon, new God()));
    }

    @Test
    void quoteDealsZeroDamageToEveryNonGodEnemy() {
        List<Enemy> enemies = List.of(
                new Ghost(), new Vampire(), new Wendigo(), new Demon(),
                new YellowEyedDemon(), new Lucifer(), new Leviathan(),
                new Angel()
        );

        for (Enemy enemy : enemies) {
            assertEquals(0, combatService.calculateDamage(
                    new Sam(), new Quote(), enemy), enemy.getName());
        }
    }

    @Test
    void actualDamageCannotExceedRemainingHealth() {
        Sam sam = new Sam();
        Salt salt = new Salt();
        Ghost ghost = new Ghost();
        sam.getInventory().addItem(salt);
        ghost.takeDamage(ghost.getMaxHealth() - 4);

        assertEquals(4, combatService.attack(sam, salt, ghost));
        assertEquals(0, ghost.getHealth());
        assertFalse(ghost.isAlive());
    }

    @Test
    void preparingEncounterGivesOnePotionToEveryHeroWithoutAccumulation() {
        Party party = new Party();
        List<Hero> heroes = List.of(
                new Sam(), new Dean(), new Bobby(), new Castiel(), new Jack()
        );

        for (Hero hero : heroes) {
            party.addMember(hero);
            hero.getInventory().addItem(new Potion());
            hero.getInventory().addItem(new Potion());
        }

        Weapon weapon = new Rifle();
        heroes.get(0).getInventory().addItem(weapon);

        LevelPreparation preparation = new LevelPreparation();
        preparation.prepareEncounter(party);
        preparation.prepareEncounter(party);

        for (Hero hero : heroes) {
            assertEquals(1L, potionCount(hero), hero.getName());
        }

        assertTrue(heroes.get(0).getInventory().contains(weapon));
    }

    @Test
    void successfulHealingConsumesPotionAndPreventsReuse() {
        Sam sam = new Sam();
        Potion potion = new Potion();
        sam.getInventory().addItem(potion);
        sam.takeDamage(30);

        Party party = new Party();
        party.addMember(sam);
        Encounter encounter = new Encounter(party, List.of(new Ghost()));
        RoundManager manager = new RoundManager(combatService);

        manager.executeHeroAction(encounter, new PotionAction(sam, potion));

        assertEquals(sam.getMaxHealth(), sam.getHealth());
        assertFalse(sam.getInventory().contains(potion));
        assertEquals(0L, potionCount(sam));

        sam.takeDamage(10);
        int healthBefore = sam.getHealth();
        manager.executeHeroAction(encounter, new PotionAction(sam, potion));

        assertEquals(healthBefore, sam.getHealth());
        assertEquals(0L, potionCount(sam));
    }

    @Test
    void fullHealthHeroDoesNotConsumePotion() {
        Sam sam = new Sam();
        Potion potion = new Potion();
        sam.getInventory().addItem(potion);
        Party party = new Party();
        party.addMember(sam);
        Encounter encounter = new Encounter(party, List.of(new Ghost()));

        new RoundManager(combatService).executeHeroAction(
                encounter, new PotionAction(sam, potion));

        assertEquals(sam.getMaxHealth(), sam.getHealth());
        assertTrue(sam.getInventory().contains(potion));
    }

    @Test
    void heroCannotSkipWhileAnActionIsAvailable() {
        GameSession session = newSession();
        session.startNewGame();
        assertThrows(IllegalStateException.class, session::skipCurrentHero);
    }

    @Test
    void nextEncounterRestoresPotionAndHealthAfterPotionWasUsed() {
        GameSession session = newSession();
        session.startNewGame();

        Hero sam = session.getCurrentHero();
        sam.takeDamage(20);
        Potion potion = sam.getInventory().getItems().stream()
                .filter(Potion.class::isInstance)
                .map(Potion.class::cast)
                .findFirst().orElseThrow();

        session.playHeroAction(new PotionAction(sam, potion));
        assertEquals(0L, potionCount(sam));
        assertEquals("Dean", session.getCurrentHero().getName());

        sam.takeDamage(10);
        Enemy ghost = session.getGameState()
                .getCurrentEncounter().getEnemies().get(0);
        ghost.takeDamage(ghost.getHealth() - 1);

        Hero dean = session.getCurrentHero();
        Weapon salt = dean.getInventory().getItems().stream()
                .filter(Salt.class::isInstance)
                .map(Weapon.class::cast)
                .findFirst().orElseThrow();

        session.playHeroAction(new AttackAction(dean, salt, ghost));

        assertEquals(1, session.getGameState().getCurrentEncounterIndex());
        assertSame(sam, session.getCurrentHero());
        assertEquals(sam.getMaxHealth(), sam.getHealth());

        for (Hero hero : session.getGameState().getCurrentParty().getMembers()) {
            assertEquals(1L, potionCount(hero), hero.getName());
        }
    }

    @Test
    void sessionCanCompleteAllEncountersAndRejectsFurtherActions() {
        GameSession session = newSession();
        session.startNewGame();
        int actions = 0;

        while (!session.isFinished() && actions < 100) {
            Encounter encounter = session.getGameState().getCurrentEncounter();
            Hero hero = session.getCurrentHero();

            if (hero == null) {
                session.playNextEnemyAction();
            } else {
                Enemy target = encounter.getEnemies().stream()
                        .filter(Enemy::isAlive).findFirst().orElseThrow();

                target.takeDamage(target.getHealth() - 1);

                Weapon weapon = hero.getInventory().getItems().stream()
                        .filter(Weapon.class::isInstance)
                        .map(Weapon.class::cast)
                        .filter(item -> item.getMultiplierAgainst(target) > 0)
                        .findFirst().orElseThrow();

                session.playHeroAction(new AttackAction(hero, weapon, target));
            }

            actions++;
        }

        assertTrue(session.isFinished());
        assertTrue(session.getGameState().isGameCompleted());
        assertFalse(session.getGameState().isGameOver());
        assertNull(session.getGameState().getCurrentEncounter());
        assertThrows(IllegalStateException.class, session::getCurrentHero);
        assertThrows(IllegalStateException.class, session::playNextEnemyAction);
    }

    private GameSession newSession() {
        RoundManager manager = new RoundManager(new CombatService());
        return new GameSession(
                new GameFactory().create(),
                new CombatController(manager),
                new LevelPreparation()
        );
    }

    private long potionCount(Hero hero) {
        return hero.getInventory().getItems().stream()
                .filter(Potion.class::isInstance).count();
    }

    private void checkStatistics(Entity entity, int health, int attack, int defense) {
        assertAll(entity.getName(),
                () -> assertEquals(health, entity.getMaxHealth()),
                () -> assertEquals(health, entity.getHealth()),
                () -> assertEquals(attack, entity.getAttack()),
                () -> assertEquals(defense, entity.getDefense())
        );
    }

    private void checkDamage(Weapon weapon, Enemy enemy, int sam, int dean) {
        assertEquals(sam, combatService.calculateDamage(new Sam(), weapon, enemy),
                "Sam / " + enemy.getName());
        assertEquals(dean, combatService.calculateDamage(new Dean(), weapon, enemy),
                "Dean / " + enemy.getName());
    }
}
