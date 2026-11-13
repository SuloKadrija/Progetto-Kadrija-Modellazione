package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.Level;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;

import java.util.List;

/**
 * Conserva i livelli e la posizione corrente della partita.
 * Ricava completamento e sconfitta dallo stato degli incontri e degli eroi.
 * Non esegue azioni, non stabilisce il turno e non gestisce file.
 *
 * La composizione della lista dei livelli è fissata alla costruzione;
 * lo stato degli oggetti contenuti può invece cambiare durante il gioco.
 */
public class GameState {

    /** Livelli nell'ordine di progressione, in una lista non modificabile. */
    private final List<Level> levels;

    /** Indice da zero del livello corrente. */
    private int currentLevelIndex;

    /** Indice da zero dell'incontro nel livello corrente. */
    private int currentEncounterIndex;

    /**
     * Crea uno stato posizionato sul primo incontro del primo livello.
     * Conserva una copia non modificabile della lista senza duplicare
     * i livelli o le entità contenute.
     *
     * @param levels livelli nell'ordine di gioco
     * @throws IllegalArgumentException se la lista è null, vuota o contiene null
     */
    public GameState(List<Level> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException(
                    "La lista dei livelli non può essere null o vuota."
            );
        }

        for (Level level : levels) {
            if (level == null) {
                throw new IllegalArgumentException(
                        "La lista non può contenere livelli null."
                );
            }
        }

        this.levels = List.copyOf(levels);
        this.currentLevelIndex = 0;
        this.currentEncounterIndex = 0;
    }

    /**
     * Restituisce la lista non modificabile creata alla costruzione.
     * Non crea nuove copie a ogni consultazione.
     * I livelli contenuti sono gli stessi utilizzati dalla partita.
     *
     * @return livelli nell'ordine di gioco
     */
    public List<Level> getLevels() {
        return levels;
    }

    /**
     * Restituisce il livello indicato dalla posizione corrente.
     *
     * @return livello corrente
     */
    public Level getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    /**
     * Restituisce l'indice del livello, con numerazione da zero.
     *
     * @return indice nella lista dei livelli
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    /**
     * Restituisce l'indice dell'incontro nel livello corrente.
     *
     * @return indice da zero nella lista degli incontri
     */
    public int getCurrentEncounterIndex() {
        return currentEncounterIndex;
    }

    /**
     * Ripristina la posizione dopo aver verificato entrambi gli indici.
     * Non modifica salute, inventari o turni.
     * La coerenza della progressione salvata è verificata dal mapper.
     *
     * @param savedLevelIndex indice del livello da ripristinare
     * @param savedEncounterIndex indice dell'incontro nel livello selezionato
     * @throws IllegalArgumentException se uno degli indici è fuori intervallo
     */
    public void restorePosition(
            int savedLevelIndex,
            int savedEncounterIndex
    ) {
        if (savedLevelIndex < 0 || savedLevelIndex >= levels.size()) {
            throw new IllegalArgumentException(
                    "L'indice del livello salvato non è valido."
            );
        }

        List<Encounter> encounters =
                levels.get(savedLevelIndex).getEncounters();

        if (savedEncounterIndex < 0
                || savedEncounterIndex >= encounters.size()) {
            throw new IllegalArgumentException(
                    "L'indice dell'incontro salvato non è valido."
            );
        }

        // La posizione cambia soltanto dopo la validazione di entrambi gli indici.
        currentLevelIndex = savedLevelIndex;
        currentEncounterIndex = savedEncounterIndex;
    }

    /**
     * Restituisce l'incontro corrente se il gioco non è completato.
     *
     * @return incontro corrente oppure null dopo la vittoria finale
     */
    public Encounter getCurrentEncounter() {
        if (isGameCompleted()) {
            return null;
        }

        return getCurrentLevel()
                .getEncounters()
                .get(currentEncounterIndex);
    }

    /**
     * Restituisce il gruppo associato all'incontro corrente.
     *
     * @return gruppo corrente oppure null dopo la vittoria finale
     */
    public Party getCurrentParty() {
        Encounter encounter = getCurrentEncounter();
        return encounter == null ? null : encounter.getParty();
    }

    /**
     * Verifica la sconfitta del gruppo corrente.
     *
     * @return true se esiste un gruppo corrente senza eroi vivi
     */
    public boolean isGameOver() {
        Party party = getCurrentParty();
        return party != null && !party.hasLivingMembers();
    }

    /**
     * Verifica il completamento di tutti gli incontri del livello corrente.
     *
     * @return true se il livello corrente è completato
     */
    public boolean isLevelCompleted() {
        return getCurrentLevel().isCompleted();
    }

    /**
     * Verifica che il livello corrente sia l'ultimo e sia completato.
     * Non conserva un indicatore di vittoria separato dallo stato degli incontri.
     *
     * @return true se la condizione di vittoria finale è soddisfatta
     */
    public boolean isGameCompleted() {
        return currentLevelIndex == levels.size() - 1
                && getCurrentLevel().isCompleted();
    }

    /**
     * Avanza dopo una vittoria all'incontro successivo o al livello seguente.
     * Non prepara dotazioni, non ripristina salute e non cambia il turno.
     *
     * @return true se la posizione è avanzata; false se non è possibile avanzare
     */
    public boolean advanceEncounter() {
        if (isGameOver() || isGameCompleted()) {
            return false;
        }

        Encounter currentEncounter = getCurrentEncounter();

        if (currentEncounter == null || !currentEncounter.isCompleted()) {
            return false;
        }

        List<Encounter> encounters = getCurrentLevel().getEncounters();

        if (currentEncounterIndex < encounters.size() - 1) {
            currentEncounterIndex++;
            return true;
        }

        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            currentEncounterIndex = 0;
            return true;
        }

        return false;
    }

    /**
     * Riporta al massimo la salute dei membri del gruppo corrente.
     * Non esegue operazioni se non esiste più un gruppo corrente.
     */
    public void restoreCurrentPartyHealth() {
        Party party = getCurrentParty();

        if (party == null) {
            return;
        }

        for (Hero hero : party.getMembers()) {
            hero.restoreHealth();
        }
    }
}