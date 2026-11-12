package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un combattimento tra un gruppo di eroi e una lista di nemici.
 * È completato quando tutti i nemici sono sconfitti. Non gestisce
 * l’ordine dei turni né l’avanzamento della partita.
 */
public class Encounter {

    /**
     * Gruppo di eroi che partecipa all’incontro.
     */
    private final Party party;
    /**
     * Nemici dell’incontro, con ordine stabile per turni e salvataggi.
     */
    private final List<Enemy> enemies;

    /**
     * Crea un incontro conservando il gruppo e copiando la lista dei nemici.
     * Le singole entità non vengono duplicate.
     *
     * @param party gruppo che partecipa al combattimento
     * @param enemies lista non vuota di nemici
     * @throws IllegalArgumentException se il gruppo, la lista o un nemico non è valido
     */
    public Encounter(Party party, List<Enemy> enemies) {
        if (party == null) {
            throw new IllegalArgumentException("Il gruppo non può essere null.");
        }

        if (enemies == null || enemies.isEmpty()) {
            throw new IllegalArgumentException("L'incontro deve avere almeno un nemico.");
        }

        for (Enemy enemy : enemies) {
            if (enemy == null) {
                throw new IllegalArgumentException("La lista non può contenere nemici null.");
            }
        }

        this.party = party;
        this.enemies = new ArrayList<>(enemies);
    }

    /**
     * Restituisce il gruppo che partecipa all’incontro.
     *
     * @return gruppo associato
     */
    public Party getParty() {
        return party;
    }

    /**
     * Restituisce una vista non modificabile dei nemici.
     * Le statistiche dei singoli nemici possono ancora cambiare.
     *
     * @return nemici nell’ordine dell’incontro
     */
    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    /**
     * Verifica se tutti i nemici sono sconfitti.
     * La sconfitta degli eroi viene verificata separatamente.
     *
     * @return true se nessun nemico è vivo
     */
    public boolean isCompleted() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }

        return true;
    }
}
