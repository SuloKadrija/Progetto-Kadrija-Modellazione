package it.unicam.cs.mpgc.rpg129131;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        GameState gameState = new GameFactory().create();

        CombatService combatService = new CombatService();

        RoundManager roundManager = new RoundManager(combatService);

        CombatController combatController = new CombatController(roundManager);

        LevelPreparation levelPreparation = new LevelPreparation();

        GameSession session = new GameSession(gameState, combatController, levelPreparation);

        try (Scanner scanner = new Scanner(System.in)) {
            GameView view = new ConsoleGameView(scanner);
            GameRunner runner = new GameRunner(session, view);
            runner.run();
        }
    }
}