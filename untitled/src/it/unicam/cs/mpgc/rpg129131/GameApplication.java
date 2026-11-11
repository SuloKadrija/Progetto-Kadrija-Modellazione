package it.unicam.cs.mpgc.rpg129131;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("SUPERNATURAL RPG");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label description = new Label(
                "Interfaccia grafica avviata correttamente.\n"
                        + "Il collegamento alla partita sarà aggiunto "
                        + "nel prossimo passaggio."
        );

        description.setWrapText(true);
        description.setAlignment(Pos.CENTER);
        description.setMaxWidth(500);

        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> stage.close());

        VBox root = new VBox(
                24,
                title,
                description,
                exitButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Supernatural RPG");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }
}