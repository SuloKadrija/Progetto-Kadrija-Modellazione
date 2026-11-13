package it.unicam.cs.mpgc.rpg129131.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Costruisce e aggiorna i controlli della schermata di combattimento.
 * Riceve testi, abilitazioni e callback dall'applicazione.
 * Non gestisce sessioni, turni, timer o salvataggi.
 */
public class CombatView extends VBox {

    private final Label encounterLabel = new Label();
    private final Label turnLabel = new Label();

    private final TextArea statusArea = new TextArea();
    private final TextArea eventArea = new TextArea();

    private final Button attackButton;
    private final Button potionButton;
    private final Button skipButton;
    private final Button saveButton;

    /**
     * Costruisce la schermata collegando i pulsanti alle operazioni ricevute.
     *
     * @param onMenu apertura del menu principale
     * @param onAttack richiesta di attacco
     * @param onPotion richiesta di cura
     * @param onSkip richiesta di passaggio del turno
     * @param onSave richiesta di salvataggio
     * @param onExit richiesta di uscita
     * @throws IllegalArgumentException se una callback è null
     */
    public CombatView(
            Runnable onMenu,
            Runnable onAttack,
            Runnable onPotion,
            Runnable onSkip,
            Runnable onSave,
            Runnable onExit
    ) {
        super(12);

        Label title = new Label("SUPERNATURAL RPG");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        turnLabel.setWrapText(true);

        configureTextArea(statusArea, 9);
        configureTextArea(eventArea, 10);

        Button menuButton = createButton("Menu principale", onMenu);
        attackButton = createButton("Attacca", onAttack);
        potionButton = createButton("Usa pozione", onPotion);
        skipButton = createButton("Passa turno", onSkip);
        saveButton = createButton("Salva partita", onSave);
        Button exitButton = createButton("Esci", onExit);

        FlowPane controls = new FlowPane(12, 12);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
                menuButton,
                attackButton,
                potionButton,
                skipButton,
                saveButton,
                exitButton
        );

        getChildren().addAll(
                title,
                encounterLabel,
                turnLabel,
                controls,
                new Label("Stato del combattimento"),
                statusArea,
                new Label("Eventi"),
                eventArea
        );

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(24));

        VBox.setVgrow(statusArea, Priority.ALWAYS);
        VBox.setVgrow(eventArea, Priority.ALWAYS);

        setActionsEnabled(false, false, false);
        setSaveEnabled(false);
    }

    /**
     * Aggiorna l'indicazione del livello e dell'incontro.
     *
     * @param text testo da visualizzare
     */
    public void setEncounterText(String text) {
        encounterLabel.setText(text);
    }

    /**
     * Aggiorna l'indicazione del turno o del risultato finale.
     *
     * @param text testo da visualizzare
     */
    public void setTurnText(String text) {
        turnLabel.setText(text);
    }

    /**
     * Sostituisce lo stato visualizzato e lo scorre all'inizio.
     *
     * @param text descrizione dei partecipanti o messaggio di stato
     */
    public void setStatusText(String text) {
        statusArea.setText(text);
        statusArea.positionCaret(0);
    }

    /**
     * Imposta l'abilitazione delle azioni decisa dall'applicazione.
     *
     * @param attackEnabled abilita l'attacco
     * @param potionEnabled abilita la cura
     * @param skipEnabled abilita il passaggio del turno
     */
    public void setActionsEnabled(
            boolean attackEnabled,
            boolean potionEnabled,
            boolean skipEnabled
    ) {
        attackButton.setDisable(!attackEnabled);
        potionButton.setDisable(!potionEnabled);
        skipButton.setDisable(!skipEnabled);
    }

    /**
     * Imposta l'abilitazione del comando di salvataggio.
     *
     * @param enabled true per abilitare il comando
     */
    public void setSaveEnabled(boolean enabled) {
        saveButton.setDisable(!enabled);
    }

    /**
     * Svuota il registro degli eventi visualizzati.
     */
    public void clearEvents() {
        eventArea.clear();
    }

    /**
     * Accoda un messaggio e scorre il registro alla fine.
     *
     * @param message messaggio da visualizzare
     */
    public void appendEvent(String message) {
        eventArea.appendText(message + "\n");
        eventArea.positionCaret(eventArea.getLength());
    }

    /**
     * Configura un'area destinata alla sola visualizzazione del testo.
     *
     * @param area controllo da configurare
     * @param rows numero preferito di righe
     */
    private void configureTextArea(TextArea area, int rows) {
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefRowCount(rows);
    }

    /**
     * Crea un pulsante collegato alla callback ricevuta.
     *
     * @param text etichetta del pulsante
     * @param action operazione da richiamare al clic
     * @return pulsante configurato
     * @throws IllegalArgumentException se la callback è null
     */
    private Button createButton(String text, Runnable action) {
        if (action == null) {
            throw new IllegalArgumentException(
                    "L'azione del pulsante non può essere null."
            );
        }

        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }
}