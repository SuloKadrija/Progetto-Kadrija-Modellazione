package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

/**
 * Definisce le operazioni di presentazione usate dal ciclo sincrono GameRunner.
 * ConsoleGameView implementa questo contratto. La vista JavaFX utilizza
 * invece i propri eventi per invocare la stessa sessione di gioco.
 */
public interface GameView {

    /**
     * Visualizza un singolo messaggio del gioco.
     *
     * @param message testo da mostrare
     */
    void showMessage(String message);

    /**
     * Visualizza la situazione corrente del combattimento.
     *
     * @param level livello corrente
     * @param encounterNumber numero dell’incontro da mostrare
     * @param encounter stato del combattimento
     */
    void showStatus(LevelNumber level, int encounterNumber, Encounter encounter);

    /**
     * Acquisisce la richiesta di azione dell’eroe senza eseguirla.
     *
     * @param hero eroe che deve agire
     * @param encounter incontro corrente
     * @return azione richiesta, oppure null quando non vi sono azioni disponibili
     */
    HeroAction readAction(Hero hero, Encounter encounter);

    /**
     * Visualizza gli eventi prodotti dalla logica nell’ordine ricevuto.
     *
     * @param events eventi da mostrare
     */
    void showEvents(List<CombatEvent> events);
}
