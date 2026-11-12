package it.unicam.cs.mpgc.rpg129131;

import java.util.Arrays;
import java.util.Scanner;

import javafx.application.Application;

/**
 * Punto di ingresso dell’applicazione.
 * Avvia normalmente l’interfaccia JavaFX oppure la modalità console
 * quando gli argomenti contengono --console.
 */
public class Main {

    /**
     * Seleziona la modalità di esecuzione dagli argomenti.
     *
     * @param args argomenti; --console richiede la modalità testuale
     */
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--console")) {
            runConsole();
        } else {
            Application.launch(GameApplication.class, args);
        }
    }

    /**
     * Costruisce stato, servizi, sessione e vista console, quindi avvia GameRunner.
     * Lo Scanner legge da System.in ed è chiuso al termine. Questa modalità
     * non presenta comandi di salvataggio e caricamento.
     */
    private static void runConsole() {
        GameState gameState = new GameFactory().create();

        CombatService combatService = new CombatService();

        RoundManager roundManager = new RoundManager(combatService);

        CombatController combatController =
                new CombatController(roundManager);

        LevelPreparation levelPreparation = new LevelPreparation();

        GameSession session = new GameSession(
                gameState,
                combatController,
                levelPreparation
        );

        try (Scanner scanner = new Scanner(System.in)) {
            GameView view = new ConsoleGameView(scanner);

            GameRunner runner = new GameRunner(session, view);

            runner.run();
        }
    }
}
