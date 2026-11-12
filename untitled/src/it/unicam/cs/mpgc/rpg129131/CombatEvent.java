package it.unicam.cs.mpgc.rpg129131;

/**
 * Rappresenta un messaggio prodotto dalla logica del gioco.
 * Le interfacce lo visualizzano senza dover conoscere i dettagli
 * dell’esecuzione dell’azione.
 *
 * @param message testo non nullo e non vuoto dell’evento
 */
public record CombatEvent(String message) {

    /**
     * Impedisce la creazione di un evento privo di un messaggio leggibile.
     *
     * @throws IllegalArgumentException se il messaggio è null o composto solo da spazi
     */
    public CombatEvent {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Il messaggio dell'evento non può essere nullo o vuoto.");
        }
    }
}
