package it.unicam.cs.mpgc.rpg129131.combat;

import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.model.item.Item;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Individua bersagli e oggetti disponibili per le azioni degli eroi.
 * Condivide i criteri di selezione fra sessione, console e interfaccia grafica.
 * Non modifica il gioco e non verifica a chi spetti il turno.
 */
public class ActionAvailability {

    /**
     * Individua i nemici vivi contro cui l'eroe possiede un'arma efficace.
     *
     * @param hero eroe da valutare
     * @param encounter incontro da esaminare
     * @return nuova lista dei bersagli, nell'ordine dell'incontro
     * @throws IllegalArgumentException se eroe o incontro è null
     */
    public List<Enemy> getAvailableTargets(Hero hero, Encounter encounter) {
        validateHero(hero);

        if (encounter == null) {
            throw new IllegalArgumentException(
                    "L'incontro non può essere null."
            );
        }

        List<Enemy> targets = new ArrayList<>();

        if (!hero.isAlive()) {
            return targets;
        }

        for (Enemy enemy : encounter.getEnemies()) {
            if (enemy.isAlive() && hasEffectiveWeapon(hero, enemy)) {
                targets.add(enemy);
            }
        }

        return targets;
    }

    /**
     * Seleziona le armi possedute con efficacia positiva sul bersaglio.
     * Un eroe o un bersaglio sconfitto non permette un attacco.
     *
     * @param hero proprietario dell'inventario
     * @param target nemico da attaccare
     * @return nuova lista delle armi, nell'ordine dell'inventario
     * @throws IllegalArgumentException se eroe o bersaglio è null
     */
    public List<Weapon> getUsableWeapons(Hero hero, Enemy target) {
        validateHero(hero);

        if (target == null) {
            throw new IllegalArgumentException(
                    "Il bersaglio non può essere null."
            );
        }

        List<Weapon> weapons = new ArrayList<>();

        if (!hero.isAlive() || !target.isAlive()) {
            return weapons;
        }

        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Weapon weapon
                    && weapon.getMultiplierAgainst(target) > 0.0) {
                weapons.add(weapon);
            }
        }

        return weapons;
    }

    /**
     * Seleziona le pozioni possedute, indipendentemente dalla salute dell'eroe.
     * Permette anche di mostrarne la quantità nell'interfaccia.
     *
     * @param hero proprietario dell'inventario
     * @return nuova lista delle pozioni, nell'ordine dell'inventario
     * @throws IllegalArgumentException se l'eroe è null
     */
    public List<Potion> getPotions(Hero hero) {
        validateHero(hero);

        List<Potion> potions = new ArrayList<>();

        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Potion potion) {
                potions.add(potion);
            }
        }

        return potions;
    }

    /**
     * Verifica la presenza di una pozione utilizzabile o di un bersaglio attaccabile.
     * Non controlla il turno o l'appartenenza dell'eroe al gruppo.
     *
     * @param hero eroe da valutare
     * @param encounter incontro da esaminare
     * @return true se esiste almeno un attacco o una cura disponibile
     * @throws IllegalArgumentException se eroe o incontro è null
     */
    public boolean hasAvailableAction(Hero hero, Encounter encounter) {
        List<Enemy> targets = getAvailableTargets(hero, encounter);

        if (!hero.isAlive()) {
            return false;
        }

        return !targets.isEmpty()
                || (hero.getHealth() < hero.getMaxHealth()
                && !getPotions(hero).isEmpty());
    }

    /**
     * Cerca un'arma efficace senza costruire la lista di tutte le armi utilizzabili.
     *
     * @param hero eroe proprietario delle armi
     * @param target nemico vivo da valutare
     * @return true alla prima arma con efficacia positiva
     */
    private boolean hasEffectiveWeapon(Hero hero, Enemy target) {
        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Weapon weapon
                    && weapon.getMultiplierAgainst(target) > 0.0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica la presenza dell'eroe richiesto.
     *
     * @param hero eroe da verificare
     * @throws IllegalArgumentException se l'eroe è null
     */
    private void validateHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException(
                    "L'eroe non può essere null."
            );
        }
    }
}