package it.unicam.cs.mpgc.rpg129131.combat;

import java.util.List;

/**
 * Separa l'esito dell'esecuzione dai messaggi destinati alle viste.
 * La sessione usa executed per decidere se consumare il turno, senza
 * interpretare il testo degli eventi. Il danno zero non implica un rifiuto.
 *
 * @param executed true se l'azione è stata eseguita
 * @param events eventi prodotti, anche in caso di rifiuto
 */
public record ActionResult(boolean executed, List<CombatEvent> events) {

    /**
     * Conserva una copia non modificabile degli eventi.
     *
     * @throws IllegalArgumentException se la lista è null o contiene null
     */
    public ActionResult {
        if (events == null || events.stream().anyMatch(event -> event == null)) {
            throw new IllegalArgumentException("Gli eventi non possono essere null.");
        }
        events = List.copyOf(events);
    }
}
