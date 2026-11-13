package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.game.GameRunner;
import it.unicam.cs.mpgc.rpg129131.game.GameSession;
import it.unicam.cs.mpgc.rpg129131.game.GameSessionFactory;
import it.unicam.cs.mpgc.rpg129131.game.GameView;
import it.unicam.cs.mpgc.rpg129131.ui.ConsoleGameView;
import it.unicam.cs.mpgc.rpg129131.ui.GameApplication;
import javafx.application.Application;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione.
 * Avvia JavaFX oppure la modalità console quando è presente --console.
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
     * Collega una nuova sessione alla vista console e avvia il ciclo del gioco.
     * Lo Scanner viene chiuso al termine.
     * La modalità console non espone comandi di salvataggio e caricamento.
     */
    private static void runConsole() {
        GameSession session = new GameSessionFactory().create();

        try (Scanner scanner = new Scanner(System.in)) {
            GameView view = new ConsoleGameView(scanner);
            GameRunner runner = new GameRunner(session, view);
            runner.run();
        }
    }
}