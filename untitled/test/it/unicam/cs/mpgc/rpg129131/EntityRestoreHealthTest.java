package it.unicam.cs.mpgc.rpg129131;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica il ripristino della salute corrente e il rifiuto dei valori fuori limite.
 * Non esegue letture JSON: testa direttamente il comportamento di Entity.
 */
class EntityRestoreHealthTest {

    /**
     * Verifica che il ripristino cambi la salute corrente senza alterare quella massima.
     */
    @Test
    void restoresSavedHealthWithoutChangingMaximum() {
        Hero hero = new Sam();
        int maxHealth = hero.getMaxHealth();
        int savedHealth = maxHealth / 2;

        hero.restoreHealth(savedHealth);

        assertEquals(savedHealth, hero.getHealth());
        assertEquals(maxHealth, hero.getMaxHealth());
    }

    /**
     * Verifica che salute zero ripristini correttamente un’entità sconfitta.
     */
    @Test
    void restoresDefeatedEntity() {
        Hero hero = new Sam();

        hero.restoreHealth(0);

        assertEquals(0, hero.getHealth());
        assertFalse(hero.isAlive());
    }

    /**
     * Verifica il ripristino della salute massima anche dopo che la salute era arrivata a zero.
     */
    @Test
    void restoresMaximumHealth() {
        Hero hero = new Sam();
        hero.takeDamage(hero.getMaxHealth());

        hero.restoreHealth(hero.getMaxHealth());

        assertEquals(hero.getMaxHealth(), hero.getHealth());
        assertTrue(hero.isAlive());
    }

    /**
     * Verifica il rifiuto di una salute negativa senza modificare il valore precedente.
     */
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

    /**
     * Verifica il rifiuto di una salute superiore al massimo senza modificare il valore precedente.
     */
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
