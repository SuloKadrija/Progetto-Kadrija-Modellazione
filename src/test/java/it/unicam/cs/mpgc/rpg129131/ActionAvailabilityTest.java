package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.combat.ActionAvailability;
import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Ghost;
import it.unicam.cs.mpgc.rpg129131.model.hero.Sam;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Quote;
import it.unicam.cs.mpgc.rpg129131.model.item.Salt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica i criteri condivisi per bersagli, armi e disponibilità delle cure.
 * Controlla che le consultazioni non consumino oggetti né modifichino la salute.
 */
class ActionAvailabilityTest {

    private final ActionAvailability availability = new ActionAvailability();

    /**
     * Esclude le armi inefficaci e i nemici sconfitti.
     */
    @Test
    void selectsEffectiveWeaponsAndLivingTargets() {
        Sam sam = new Sam();
        Quote quote = new Quote();
        Salt salt = new Salt();
        sam.getInventory().addItem(quote);
        sam.getInventory().addItem(salt);

        Ghost livingEnemy = new Ghost();
        Ghost defeatedEnemy = new Ghost();
        defeatedEnemy.restoreHealth(0);

        Party party = new Party();
        party.addMember(sam);

        Encounter encounter = new Encounter(
                party,
                List.of(livingEnemy, defeatedEnemy)
        );

        assertEquals(
                List.of(livingEnemy),
                availability.getAvailableTargets(sam, encounter)
        );
        assertEquals(
                List.of(salt),
                availability.getUsableWeapons(sam, livingEnemy)
        );
        assertTrue(availability.getUsableWeapons(sam, defeatedEnemy).isEmpty());
        assertEquals(livingEnemy.getMaxHealth(), livingEnemy.getHealth());
    }

    /**
     * La sola pozione permette un'azione soltanto quando l'eroe è vivo e ferito.
     * La verifica non consuma la pozione e non applica la cura.
     */
    @Test
    void potionRequiresLivingInjuredHeroAndIsNotConsumed() {
        Sam sam = new Sam();
        Potion potion = new Potion();
        sam.getInventory().addItem(potion);

        Party party = new Party();
        party.addMember(sam);
        Encounter encounter = new Encounter(party, List.of(new Ghost()));

        assertFalse(availability.hasAvailableAction(sam, encounter));

        sam.takeDamage(10);
        int healthBefore = sam.getHealth();

        assertTrue(availability.hasAvailableAction(sam, encounter));
        assertEquals(healthBefore, sam.getHealth());
        assertEquals(List.of(potion), availability.getPotions(sam));

        sam.restoreHealth(0);

        assertFalse(availability.hasAvailableAction(sam, encounter));
        assertEquals(List.of(potion), availability.getPotions(sam));
    }

    /**
     * Un'arma inefficace sul nemico corrente non permette di attaccare.
     */
    @Test
    void ineffectiveWeaponDoesNotMakeAnActionAvailable() {
        Sam sam = new Sam();
        sam.getInventory().addItem(new Quote());

        Party party = new Party();
        party.addMember(sam);
        Encounter encounter = new Encounter(party, List.of(new Ghost()));

        assertTrue(availability.getAvailableTargets(sam, encounter).isEmpty());
        assertFalse(availability.hasAvailableAction(sam, encounter));
    }
}