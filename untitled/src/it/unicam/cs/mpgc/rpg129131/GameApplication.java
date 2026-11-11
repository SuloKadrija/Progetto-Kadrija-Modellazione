package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameApplication extends Application {

    private GameSession session;
    private TextArea eventArea;
    private Label statusLabel;
    private Button attackButton;

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

        attackButton = new Button("Attacca");
        attackButton.setDisable(true);
        attackButton.setOnAction(event -> attack());

        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> stage.close());

        VBox root = new VBox(
                16,
                title,
                statusLabel,
                newGameButton,
                attackButton,
                eventArea,
                exitButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        VBox.setVgrow(eventArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Supernatural RPG");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
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
        updateControls();
    }

    private void attack() {
        if (session == null || session.isFinished()) {
            return;
        }

        Hero hero = session.getCurrentHero();

        if (hero == null) {
            return;
        }

        Encounter encounter =
                session.getGameState().getCurrentEncounter();

        List<Enemy> targets = getAvailableTargets(hero, encounter);

        if (targets.isEmpty()) {
            return;
        }

        List<String> targetNames = new ArrayList<>();

        for (Enemy enemy : targets) {
            int enemyNumber = encounter.getEnemies().indexOf(enemy) + 1;

            targetNames.add(
                    enemy.getName()
                            + " #" + enemyNumber
                            + " - HP: " + enemy.getHealth()
                            + "/" + enemy.getMaxHealth()
            );
        }

        int targetIndex = chooseIndex(
                "Scegli il nemico",
                "Turno di " + hero.getName(),
                targetNames
        );

        if (targetIndex < 0) {
            return;
        }

        Enemy target = targets.get(targetIndex);
        List<Weapon> weapons = getUsableWeapons(hero, target);
        List<String> weaponNames = new ArrayList<>();

        for (int i = 0; i < weapons.size(); i++) {
            weaponNames.add(
                    (i + 1) + ") " + weapons.get(i).getName()
            );
        }

        int weaponIndex = chooseIndex(
                "Scegli l'arma",
                "Bersaglio: " + target.getName(),
                weaponNames
        );

        if (weaponIndex < 0) {
            return;
        }

        Weapon weapon = weapons.get(weaponIndex);

        showEvents(
                session.playHeroAction(
                        new AttackAction(hero, weapon, target)
                )
        );

        updateControls();
    }

    private List<Enemy> getAvailableTargets(
            Hero hero,
            Encounter encounter
    ) {
        List<Enemy> targets = new ArrayList<>();

        for (Enemy enemy : encounter.getEnemies()) {
            if (enemy.isAlive()
                    && !getUsableWeapons(hero, enemy).isEmpty()) {
                targets.add(enemy);
            }
        }

        return targets;
    }

    private List<Weapon> getUsableWeapons(
            Hero hero,
            Enemy target
    ) {
        List<Weapon> weapons = new ArrayList<>();

        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Weapon weapon
                    && weapon.getMultiplierAgainst(target) > 0.0) {
                weapons.add(weapon);
            }
        }

        return weapons;
    }

    private int chooseIndex(
            String title,
            String header,
            List<String> choices
    ) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                choices.get(0),
                choices
        );

        dialog.initOwner(eventArea.getScene().getWindow());
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText("Seleziona:");

        Optional<String> selected = dialog.showAndWait();

        if (selected.isEmpty()) {
            return -1;
        }

        return choices.indexOf(selected.get());
    }

    private void updateControls() {
        attackButton.setDisable(true);

        if (session == null) {
            statusLabel.setText("Premi Nuova partita per iniziare.");
            return;
        }

        if (session.isFinished()) {
            statusLabel.setText("Partita terminata.");
            return;
        }

        Hero hero = session.getCurrentHero();

        if (hero == null) {
            statusLabel.setText(
                    "Turno dei nemici. Il relativo comando "
                            + "sarà aggiunto nel prossimo passaggio."
            );
            return;
        }

        Encounter encounter =
                session.getGameState().getCurrentEncounter();

        boolean canAttack =
                !getAvailableTargets(hero, encounter).isEmpty();

        attackButton.setDisable(!canAttack);

        if (canAttack) {
            statusLabel.setText("Turno di " + hero.getName());
        } else {
            statusLabel.setText(
                    "Turno di " + hero.getName()
                            + ": nessun attacco disponibile. "
                            + "Cura e passaggio del turno "
                            + "non sono ancora collegati."
            );
        }
    }

    private void showEvents(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            eventArea.appendText(event.message() + "\n");
        }
    }
}