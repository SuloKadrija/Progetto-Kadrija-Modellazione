package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameApplication extends Application {

    private GameSession session;
    private TextArea eventArea;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        Label title = new Label("SUPERNATURAL RPG");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        statusLabel = new Label("Premi Nuova partita per iniziare.");
        statusLabel.setWrapText(true);

        eventArea = new TextArea();
        eventArea.setEditable(false);
        eventArea.setWrapText(true);
        eventArea.setPromptText(
                "Qui compariranno gli eventi della partita."
        );

        Button newGameButton = new Button("Nuova partita");
        newGameButton.setOnAction(event -> startNewGame());

        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> stage.close());

        VBox root = new VBox(
                16,
                title,
                statusLabel,
                newGameButton,
                eventArea,
                exitButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        VBox.setVgrow(eventArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Supernatural RPG");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    private void startNewGame() {
        GameState gameState = new GameFactory().create();

        CombatService combatService = new CombatService();
        RoundManager roundManager = new RoundManager(combatService);

        CombatController combatController =
                new CombatController(roundManager);

        LevelPreparation levelPreparation = new LevelPreparation();

        session = new GameSession(
                gameState,
                combatController,
                levelPreparation
        );

        eventArea.clear();

        showEvents(session.startNewGame());

        Hero currentHero = session.getCurrentHero();

        if (currentHero != null) {
            statusLabel.setText(
                    "Turno di " + currentHero.getName()
                            + ". I comandi di combattimento "
                            + "saranno aggiunti nel prossimo passaggio."
            );
        } else {
            statusLabel.setText("Turno dei nemici.");
        }
    }

    private void showEvents(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            eventArea.appendText(event.message() + "\n");
        }
    }
}