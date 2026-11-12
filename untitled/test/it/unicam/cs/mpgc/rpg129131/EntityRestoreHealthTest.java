package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityRestoreHealthTest {

    @Test
    void restoresSavedHealthWithoutChangingMaximum() {
        Hero hero = new Sam();
        int maxHealth = hero.getMaxHealth();
        int savedHealth = maxHealth / 2;

        hero.restoreHealth(savedHealth);

        assertEquals(savedHealth, hero.getHealth());
        assertEquals(maxHealth, hero.getMaxHealth());
    }

    @Test
    void restoresDefeatedEntity() {
        Hero hero = new Sam();

        hero.restoreHealth(0);

        assertEquals(0, hero.getHealth());
        assertFalse(hero.isAlive());
    }

    @Test
    void restoresMaximumHealth() {
        Hero hero = new Sam();
        hero.takeDamage(hero.getMaxHealth());

        hero.restoreHealth(hero.getMaxHealth());

        assertEquals(hero.getMaxHealth(), hero.getHealth());
        assertTrue(hero.isAlive());
    }

    @Test
    void rejectsNegativeHealthWithoutChangingCurrentHealth() {
        Hero hero = new Sam();
        int previousHealth = hero.getHealth();

        assertThrows(
                IllegalArgumentException.class,
                () -> hero.restoreHealth(-1)
        );

        assertEquals(previousHealth, hero.getHealth());
    }

    @Test
    void rejectsHealthAboveMaximumWithoutChangingCurrentHealth() {
        Hero hero = new Sam();
        int previousHealth = hero.getHealth();

        assertThrows(
                IllegalArgumentException.class,
                () -> hero.restoreHealth(hero.getMaxHealth() + 1)
        );

        assertEquals(previousHealth, hero.getHealth());
    }
}