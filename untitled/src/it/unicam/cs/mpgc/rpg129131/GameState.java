package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;

/**
 * Conserva i livelli e la posizione corrente della partita.
 * Ricava completamento e sconfitta dallo stato degli incontri e degli eroi.
 * Non esegue azioni, non stabilisce il turno e non legge o scrive file.
 */
public class GameState {

    /**
     * Livelli della partita nell’ordine di progressione.
     */
    private final List<Level> levels;
    /**
     * Indice da zero del livello corrente.
     */
    private int currentLevelIndex;
    /**
     * Indice da zero dell’incontro nel livello corrente.
     */
    private int currentEncounterIndex;

    /**
     * Crea uno stato posizionato sul primo incontro del primo livello.
     * Copia la lista dei livelli, ma non duplica i livelli e le entità contenute.
     *
     * @param levels livelli non nulli, nell’ordine di gioco
     * @throws IllegalArgumentException se la lista è null, vuota o contiene null
     */
    public GameState(List<Level> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException("La lista dei livelli non può essere null o vuota.");
        }

        for (Level level : levels) {
            if (level == null) {
                throw new IllegalArgumentException("La lista non può contenere livelli null.");
            }
        }

        this.levels = new ArrayList<>(levels);
        this.currentLevelIndex = 0;
        this.currentEncounterIndex = 0;
    }

    /**
     * Restituisce una copia non modificabile della lista dei livelli.
     * Gli oggetti Level contenuti restano gli stessi e non vengono duplicati.
     *
     * @return livelli nell’ordine di gioco
     */
    public List<Level> getLevels() {
        return List.copyOf(levels);
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
     * Restituisce l’indice del livello, con numerazione da zero.
     *
     * @return indice nella lista dei livelli
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    /**
     * Restituisce l’indice dell’incontro nel livello corrente, partendo da zero.
     *
     * @return indice nella lista degli incontri del livello
     */
    public int getCurrentEncounterIndex() {
        return currentEncounterIndex;
    }

    /**
     * Ripristina i due indici dopo averne verificato entrambi i limiti.
     * Non modifica incontri o salute e non verifica che gli incontri precedenti
     * siano completati: la coerenza globale è controllata dal mapper del salvataggio.
     *
     * @param savedLevelIndex indice del livello da ripristinare
     * @param savedEncounterIndex indice dell’incontro nel livello selezionato
     * @throws IllegalArgumentException se uno degli indici è fuori intervallo
     */
    public void restorePosition(
            int savedLevelIndex,
            int savedEncounterIndex
    ) {
        if (savedLevelIndex < 0 || savedLevelIndex >= levels.size()) {
            throw new IllegalArgumentException("L'indice del livello salvato non è valido.");
        }

        List<Encounter> encounters =
                levels.get(savedLevelIndex).getEncounters();

        if (savedEncounterIndex < 0 || savedEncounterIndex >= encounters.size()) {
            throw new IllegalArgumentException("L'indice dell'incontro salvato non è valido.");
        }

        // Assegna entrambi gli indici soltanto dopo aver validato tutta la posizione.
        this.currentLevelIndex = savedLevelIndex;
        this.currentEncounterIndex = savedEncounterIndex;
    }

    /**
     * Restituisce l’incontro corrente, se la partita non è completata.
     *
     * @return incontro corrente oppure null dopo il completamento del gioco
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
     * Restituisce il gruppo associato all’incontro corrente.
     *
     * @return gruppo corrente oppure null se il gioco è completato
     */
    public Party getCurrentParty() {
        Encounter encounter = getCurrentEncounter();

        if (encounter == null) {
            return null;
        }

        return encounter.getParty();
    }

    /**
     * Verifica la sconfitta del gruppo corrente.
     * Se non esiste più un incontro corrente restituisce false.
     *
     * @return true se il gruppo corrente non ha eroi vivi
     */
    public boolean isGameOver() {
        Party party = getCurrentParty();

        if (party == null) {
            return false;
        }

        return !party.hasLivingMembers();
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
     * Verifica che il livello corrente sia l’ultimo e che sia completato.
     * Si basa sullo stato dei nemici; non conserva un flag di vittoria separato.
     *
     * @return true se la condizione di completamento finale è soddisfatta
     */
    public boolean isGameCompleted() {
        return currentLevelIndex == levels.size() - 1
                && getCurrentLevel().isCompleted();
    }

    /**
     * Avanza dopo una vittoria all’incontro successivo o al primo del livello seguente.
     * Non prepara inventari, non ripristina salute e non cambia gli indici del turno.
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
     * Riporta al massimo la salute di tutti i membri del gruppo corrente.
     * Non esegue operazioni se il gioco completato non ha più un gruppo corrente.
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
