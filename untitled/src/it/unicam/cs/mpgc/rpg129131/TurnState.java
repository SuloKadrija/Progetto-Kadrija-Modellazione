package it.unicam.cs.mpgc.rpg129131;

/**
 * Conserva la fase del round e gli indici necessari per riprenderla.
 * È una fotografia dei soli dati del turno: non contiene salute, inventari
 * o posizione nel gioco. I limiti rispetto al gruppo e all’incontro
 * sono verificati da GameSession durante il ripristino.
 *
 * @param enemiesTurn true nella fase nemica
 * @param heroIndex indice dell’eroe o dimensione del gruppo nella fase nemica
 * @param enemyIndex indice di ricerca del prossimo nemico da far agire
 */
public record TurnState(boolean enemiesTurn, int heroIndex, int enemyIndex) {

    /**
     * Rifiuta indici negativi senza dipendere dalla struttura concreta del gioco.
     * Le verifiche dei limiti superiori sono eseguite durante il ripristino.
     *
     * @throws IllegalArgumentException se uno degli indici è negativo
     */
    public TurnState {
        if (heroIndex < 0 || enemyIndex < 0) {
            throw new IllegalArgumentException("Gli indici del turno non possono essere negativi.");
        }
    }
}
