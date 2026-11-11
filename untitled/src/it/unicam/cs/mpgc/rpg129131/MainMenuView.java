package it.unicam.cs.mpgc.rpg129131;

import java.net.URL;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuView extends StackPane {

    public MainMenuView(
            boolean canContinue,
            Runnable onNewGame,
            Runnable onContinue,
            Runnable onExit
    ) {
        Image backgroundImage = loadImage("/images/Sfondo.png");
        Image logoImage = loadImage("/images/Logo.png");

        BackgroundSize backgroundSize = new BackgroundSize(
                BackgroundSize.AUTO,
                BackgroundSize.AUTO,
                false,
                false,
                false,
                true
        );

        setBackground(
                new Background(
                        new BackgroundImage(
                                backgroundImage,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                backgroundSize
                        )
                )
        );

        ImageView logo = new ImageView(logoImage);
        logo.setPreserveRatio(true);
        logo.fitWidthProperty().bind(
                widthProperty().multiply(0.65)
        );
        logo.setFitHeight(220);

        Button newGameButton = createButton(
                "Nuova partita",
                onNewGame
        );

        Button continueButton = createButton(
                "Continua partita",
                onContinue
        );

        continueButton.setDisable(!canContinue);

        Button exitButton = createButton("Esci", onExit);

        Label note = new Label(
                "Continua partita riprende la sessione aperta.\n"
                        + "Il salvataggio su disco non è ancora disponibile."
        );

        note.setWrapText(true);
        note.setMaxWidth(400);
        note.setAlignment(Pos.CENTER);
        note.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 14px;"
                        + "-fx-text-alignment: center;"
                        + "-fx-background-color: rgba(0, 0, 0, 0.65);"
                        + "-fx-padding: 12px;"
                        + "-fx-background-radius: 10px;"
        );

        VBox buttons = new VBox(
                24,
                newGameButton,
                continueButton,
                exitButton,
                note
        );

        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(40, logo, buttons);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        getChildren().add(content);
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);

        button.setPrefSize(300, 70);

        String normalStyle =
                "-fx-background-color: #970000;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 25px;"
                        + "-fx-background-radius: 20px;"
                        + "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #bd1111;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 25px;"
                        + "-fx-background-radius: 20px;"
                        + "-fx-cursor: hand;";

        button.setStyle(normalStyle);
        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
        button.setOnAction(event -> action.run());

        return button;
    }

    private Image loadImage(String path) {
        URL resource = MainMenuView.class.getResource(path);

        if (resource == null) {
            throw new IllegalStateException(
                    "Immagine non trovata: " + path
            );
        }

        Image image = new Image(resource.toExternalForm());

        if (image.isError()) {
            throw new IllegalStateException(
                    "Impossibile caricare l'immagine: " + path,
                    image.getException()
            );
        }

        return image;
    }
}