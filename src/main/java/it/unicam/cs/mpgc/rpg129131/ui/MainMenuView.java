package it.unicam.cs.mpgc.rpg129131.ui;

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

/**
 * Costruisce il menu principale con immagini, pulsanti e istruzioni.
 * Riceve stato dei pulsanti e callback dall’applicazione: non crea partite,
 * non interpreta salvataggi e non gestisce direttamente i turni.
 */
public class MainMenuView extends StackPane {

    /**
     * Costruisce sfondo, personaggi, logo, pulsanti e istruzioni del menu.
     * Il menu usa i valori ricevuti alla costruzione; l’applicazione lo ricrea
     * quando deve mostrare uno stato aggiornato.
     *
     * @param canContinue abilita la ripresa della sessione in memoria
     * @param canLoad abilita il tentativo di caricamento da file
     * @param onNewGame operazione associata a Nuova partita
     * @param onContinue operazione associata a Continua partita
     * @param onLoad operazione associata a Carica partita
     * @param onExit operazione associata a Esci
     * @throws IllegalStateException se una risorsa grafica non è disponibile
     */
    public MainMenuView(
            boolean canContinue,
            boolean canLoad,
            Runnable onNewGame,
            Runnable onContinue,
            Runnable onLoad,
            Runnable onExit
    ) {
        // Le risorse sono incluse nel classpath dell’applicazione.
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

        ImageView dean = createCharacter("/images/Dean.png");
        ImageView sam = createCharacter("/images/Sam.png");

        StackPane.setAlignment(dean, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(sam, Pos.BOTTOM_RIGHT);

        StackPane.setMargin(
                dean,
                new Insets(0, 0, 0, 20)
        );

        StackPane.setMargin(
                sam,
                new Insets(0, 20, 0, 0)
        );

        ImageView logo = new ImageView(logoImage);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);

        logo.fitWidthProperty().bind(
                widthProperty().multiply(0.88)
        );

        logo.fitHeightProperty().bind(
                heightProperty().multiply(0.42)
        );

        StackPane logoContainer = new StackPane(logo);
        logoContainer.setAlignment(Pos.CENTER);

        // Le operazioni sono fornite da GameApplication tramite callback.
        Button newGameButton = createButton(
                "Nuova partita",
                onNewGame
        );

        Button continueButton = createButton(
                "Continua partita",
                onContinue
        );

        continueButton.setDisable(!canContinue);

        Button loadButton = createButton(
                "Carica partita",
                onLoad
        );

        loadButton.setDisable(!canLoad);

        Button exitButton = createButton(
                "Esci",
                onExit
        );

        VBox buttons = new VBox(
                18,
                newGameButton,
                continueButton,
                loadButton,
                exitButton
        );

        buttons.setAlignment(Pos.CENTER);

        Label note = new Label(
                "Continua partita riprende la sessione aperta.\n"
                        + "Carica partita apre l'ultimo salvataggio.\n"
                        + "Durante il gioco premi Salva partita "
                        + "per conservare i progressi."
        );

        note.setWrapText(true);
        note.setMaxWidth(380);
        note.setAlignment(Pos.CENTER);

        note.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 12px;"
                        + "-fx-text-alignment: center;"
                        + "-fx-background-color: rgba(0, 0, 0, 0.65);"
                        + "-fx-padding: 10px;"
                        + "-fx-background-radius: 10px;"
        );

        VBox content = new VBox(
                24,
                logoContainer,
                buttons,
                note
        );

        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));

        getChildren().addAll(
                dean,
                sam,
                content
        );
    }

    /**
     * Crea l’immagine di un personaggio e ne lega le dimensioni a quelle del menu.
     * L’immagine non intercetta il mouse, così non blocca i controlli sovrapposti.
     *
     * @param path percorso della risorsa nel classpath
     * @return vista del personaggio con proporzioni conservate
     * @throws IllegalStateException se l’immagine non è caricabile
     */
    private ImageView createCharacter(String path) {
        ImageView character = new ImageView(loadImage(path));

        character.setPreserveRatio(true);
        character.setSmooth(true);
        character.setMouseTransparent(true);

        character.fitWidthProperty().bind(
                widthProperty().multiply(0.27)
        );

        character.fitHeightProperty().bind(
                heightProperty().multiply(0.62)
        );

        return character;
    }

    /**
     * Crea un pulsante con dimensioni uniformi, stile normale e al passaggio del mouse.
     * Il clic esegue la callback ricevuta senza implementarne qui la logica.
     *
     * @param text etichetta del pulsante
     * @param action operazione da eseguire al clic
     * @return pulsante configurato
     */
    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);

        button.setMinSize(240, 54);
        button.setPrefSize(240, 54);
        button.setMaxSize(240, 54);

        String normalStyle =
                "-fx-background-color: #970000;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 21px;"
                        + "-fx-background-radius: 16px;"
                        + "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #bd1111;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 21px;"
                        + "-fx-background-radius: 16px;"
                        + "-fx-cursor: hand;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(
                event -> button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(
                event -> button.setStyle(normalStyle)
        );

        button.setOnAction(event -> action.run());

        return button;
    }

    /**
     * Carica una risorsa grafica dal classpath, non da un percorso del filesystem.
     *
     * @param path percorso della risorsa, ad esempio /images/Logo.png
     * @return immagine caricata
     * @throws IllegalStateException se la risorsa manca o presenta un errore di lettura
     */
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
