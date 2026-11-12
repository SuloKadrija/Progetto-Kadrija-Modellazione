package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Raggruppa i tre incontri previsti per un livello del gioco.
 * Verifica che ogni nemico appartenga al livello dichiarato e considera
 * il livello completato soltanto quando tutti gli incontri sono completati.
 */
public class Level {

    /**
     * Numero di incontri previsto per ciascun livello.
     */
    private static final int REQUIRED_ENCOUNTERS = 3;

    /**
     * Identificatore logico del livello.
     */
    private final LevelNumber number;
    /**
     * Incontri del livello nell’ordine di progressione.
     */
    private final List<Encounter> encounters;

    /**
     * Crea un livello verificando numero, tre incontri e appartenenza dei nemici.
     *
     * @param number identificatore del livello
     * @param encounters tre incontri del livello, nell’ordine di gioco
     * @throws IllegalArgumentException se i dati non rispettano i vincoli del livello
     */
    public Level(LevelNumber number, List<Encounter> encounters) {
        if (number == null) {
            throw new IllegalArgumentException("Il numero del livello non può essere null.");
        }

        if (encounters == null) {
            throw new IllegalArgumentException("La lista degli incontri non può essere null.");
        }

        if (encounters.size() != REQUIRED_ENCOUNTERS) {
            throw new IllegalArgumentException("Ogni livello deve contenere esattamente " + REQUIRED_ENCOUNTERS + " incontri.");
        }

        for (Encounter encounter : encounters) {
            if (encounter == null) {
                throw new IllegalArgumentException("La lista non può contenere incontri null.");
            }

            for (Enemy enemy : encounter.getEnemies()) {
                if (enemy.getRequiredLevel() != number) {
                    throw new IllegalArgumentException(
                            "Il nemico " + enemy.getName() + " appartiene al livello " + enemy.getRequiredLevel() + " e non può essere inserito nel livello " + number + ".");
                }
            }
        }

        this.number = number;
        this.encounters = new ArrayList<>(encounters);
    }

    /**
     * Restituisce l’identificatore del livello.
     *
     * @return numero logico del livello
     */
    public LevelNumber getNumber() {
        return number;
    }

    /**
     * Restituisce una vista non modificabile degli incontri nell’ordine previsto.
     *
     * @return incontri del livello
     */
    public List<Encounter> getEncounters() {
        return Collections.unmodifiableList(encounters);
    }

    /**
     * Verifica il completamento di tutti gli incontri del livello.
     *
     * @return true soltanto se ogni incontro è completato
     */
    public boolean isCompleted() {
        for (Encounter encounter : encounters) {
            if (!encounter.isCompleted()) {
                return false;
            }
        }

        return true;
    }
}
