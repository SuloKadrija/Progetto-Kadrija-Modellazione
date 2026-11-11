package it.unicam.cs.mpgc.rpg129131;

import java.util.Arrays;
import java.util.Scanner;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--console")) {
            runConsole();
        } else {
            Application.launch(GameApplication.class, args);
        }
    }

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