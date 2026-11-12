package it.unicam.cs.mpgc.rpg129131.combat;

import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;

import java.util.List;

/**
 * Espone le operazioni di combattimento alla sessione e delega
 * l’esecuzione delle singole azioni a RoundManager.
 * Fornisce inoltre un messaggio che descrive l’esito corrente dell’incontro.
 */
public class CombatController {

    /**
     * Gestore al quale delegare l’esecuzione delle azioni.
     */
    private final RoundManager roundManager;

    /**
     * Crea il coordinatore usando il gestore delle azioni fornito.
     *
     * @param roundManager gestore che esegue le azioni
     * @throws IllegalArgumentException se il gestore è null
     */
    public CombatController(RoundManager roundManager) {
        if (roundManager == null) {
            throw new IllegalArgumentException("Il gestore dei round non può essere null.");
        }

        this.roundManager = roundManager;
    }

    /**
     * Delega l’esecuzione di un’azione dell’eroe al gestore.
     * I controlli di validità dell’incontro e dell’azione sono effettuati dal gestore.
     *
     * @param encounter incontro interessato
     * @param action azione richiesta
     * @return eventi prodotti dall’azione
     * @throws IllegalArgumentException se incontro, azione o partecipanti non sono validi
     * @throws IllegalStateException se il combattimento è terminato
     */
    public List<CombatEvent> executeHeroAction(Encounter encounter, HeroAction action) {
        return roundManager.executeHeroAction(encounter, action);
    }

    /**
     * Delega l’esecuzione dell’azione di un singolo nemico.
     *
     * @param encounter incontro interessato
     * @param enemy nemico che agisce
     * @return eventi prodotti, eventualmente una lista vuota
     * @throws IllegalArgumentException se incontro o nemico non sono validi
     * @throws IllegalStateException se il combattimento è terminato
     */
    public List<CombatEvent> executeEnemyAction(Encounter encounter, Enemy enemy) {
        return roundManager.executeEnemyAction(encounter, enemy);
    }

    /**
     * Descrive la situazione senza modificare l’incontro.
     * Controlla prima la sconfitta del gruppo, poi la vittoria e infine
     * la prosecuzione del combattimento.
     *
     * @param encounter incontro da valutare
     * @return messaggio di sconfitta, vittoria o combattimento in corso
     * @throws IllegalArgumentException se l’incontro è null
     */
    public String getCombatResult(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (!encounter.getParty().hasLivingMembers()) {
            return "SCONFITTA: tutti gli eroi sono stati sconfitti.";
        }

        if (encounter.isCompleted()) {
            return "VITTORIA: tutti i nemici sono stati sconfitti.";
        }

        return "COMBATTIMENTO IN CORSO";
    }
}
