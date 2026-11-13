package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.combat.AttackAction;
import it.unicam.cs.mpgc.rpg129131.combat.PotionAction;
import it.unicam.cs.mpgc.rpg129131.game.GameSession;
import it.unicam.cs.mpgc.rpg129131.game.GameSessionFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameState;
import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.Level;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Ghost;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.model.hero.Sam;
import it.unicam.cs.mpgc.rpg129131.model.hero.Dean;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Quote;
import it.unicam.cs.mpgc.rpg129131.model.item.Salt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica che i rifiuti non consumino il turno, mentre le azioni
 * realmente eseguite lo consumino anche quando il danno è zero.
 */
class HeroActionTurnTest {

    /** Una cura rifiutata a salute piena conserva turno, salute e pozione. */
    @Test
    void fullHealthPotionDoesNotConsumeTurn() {
        GameSession session = newSession();
        Hero hero = session.getCurrentHero();
        Potion potion = hero.getInventory().getItems().stream()
                .filter(Potion.class::isInstance).map(Potion.class::cast)
                .findFirst().orElseThrow();
        var turn = session.getTurnState();

        assertFalse(session.playHeroAction(new PotionAction(hero, potion)).isEmpty());

        assertEquals(turn, session.getTurnState());
        assertSame(hero, session.getCurrentHero());
        assertEquals(hero.getMaxHealth(), hero.getHealth());
        assertTrue(hero.getInventory().contains(potion));
    }

    /** Una pozione non posseduta non cura e non cambia il turno. */
    @Test
    void missingPotionDoesNotConsumeTurn() {
        GameSession session = newSession();
        Hero hero = session.getCurrentHero();
        hero.takeDamage(10);
        int health = hero.getHealth();
        var turn = session.getTurnState();

        session.playHeroAction(new PotionAction(hero, new Potion()));

        assertEquals(turn, session.getTurnState());
        assertEquals(health, hero.getHealth());
    }

    /** Un'arma non posseduta non danneggia il bersaglio e non cambia il turno. */
    @Test
    void missingWeaponDoesNotConsumeTurn() {
        GameSession session = newSession();
        Hero hero = session.getCurrentHero();
        var enemy = session.getGameState().getCurrentEncounter().getEnemies().get(0);
        int health = enemy.getHealth();
        var turn = session.getTurnState();

        session.playHeroAction(new AttackAction(hero, new Salt(), enemy));

        assertEquals(turn, session.getTurnState());
        assertEquals(health, enemy.getHealth());
    }

    /** Un attacco eseguito con un'arma inefficace consuma il turno. */
    @Test
    void zeroDamageAttackConsumesTurn() {
        GameSession session = newSession();
        Hero hero = session.getCurrentHero();
        Quote quote = new Quote();
        hero.getInventory().addItem(quote);
        var enemy = session.getGameState().getCurrentEncounter().getEnemies().get(0);
        int health = enemy.getHealth();

        session.playHeroAction(new AttackAction(hero, quote, enemy));

        assertEquals(health, enemy.getHealth());
        assertNotSame(hero, session.getCurrentHero());
        assertEquals(1, session.getTurnState().heroIndex());
    }

    /** Una cura riuscita consuma la pozione e passa al prossimo eroe. */
    @Test
    void successfulPotionConsumesTurn() {
        GameSession session = newSession();
        Hero hero = session.getCurrentHero();
        hero.takeDamage(10);
        Potion potion = hero.getInventory().getItems().stream()
                .filter(Potion.class::isInstance).map(Potion.class::cast)
                .findFirst().orElseThrow();

        session.playHeroAction(new PotionAction(hero, potion));

        assertEquals(hero.getMaxHealth(), hero.getHealth());
        assertFalse(hero.getInventory().contains(potion));
        assertNotSame(hero, session.getCurrentHero());
    }

    /** Un bersaglio sconfitto non consuma il turno se l'incontro è ancora attivo. */
    @Test
    void defeatedTargetDoesNotConsumeTurn() {
        Party party = new Party();
        party.addMember(new Sam());
        party.addMember(new Dean());
        Ghost defeated = new Ghost();
        defeated.restoreHealth(0);
        Encounter encounter = new Encounter(party, List.of(defeated, new Ghost()));
        Level level = new Level(LevelNumber.ONE, List.of(
                encounter,
                new Encounter(party, List.of(new Ghost())),
                new Encounter(party, List.of(new Ghost()))
        ));
        GameSession session = new GameSessionFactory().create(
                new GameState(List.of(level))
        );
        session.startNewGame();
        Hero hero = session.getCurrentHero();
        Salt salt = new Salt();
        hero.getInventory().addItem(salt);
        var turn = session.getTurnState();

        session.playHeroAction(new AttackAction(hero, salt, defeated));

        assertEquals(turn, session.getTurnState());
        assertEquals(0, defeated.getHealth());
    }

    /** Crea una partita indipendente e ne prepara il primo turno. */
    private GameSession newSession() {
        GameSession session = new GameSessionFactory().create();
        session.startNewGame();
        return session;
    }
}
