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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameApplication extends Application {

    private GameSession session;

    private TextArea eventArea;
    private Label statusLabel;

    private Button attackButton;
    private Button potionButton;
    private Button skipButton;
    private Button enemyActionButton;

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

        potionButton = new Button("Usa pozione");
        potionButton.setDisable(true);
        potionButton.setOnAction(event -> usePotion());

        skipButton = new Button("Passa turno");
        skipButton.setDisable(true);
        skipButton.setOnAction(event -> skipTurn());

        enemyActionButton = new Button("Prossima azione nemica");
        enemyActionButton.setDisable(true);
        enemyActionButton.setOnAction(event -> playEnemyAction());

        HBox heroControls = new HBox(
                12,
                attackButton,
                potionButton,
                skipButton
        );

        heroControls.setAlignment(Pos.CENTER);

        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> stage.close());

        VBox root = new VBox(
                16,
                title,
                statusLabel,
                newGameButton,
                heroControls,
                enemyActionButton,
                eventArea,
                exitButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        VBox.setVgrow(eventArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 650);

        stage.setTitle("Supernatural RPG");
        stage.setMinWidth(600);
        stage.setMinHeight(550);
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
        Hero hero = getActingHero();

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
            int enemyNumber =
                    encounter.getEnemies().indexOf(enemy) + 1;

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

    private void usePotion() {
        Hero hero = getActingHero();

        if (hero == null || hero.getHealth() >= hero.getMaxHealth()) {
            return;
        }

        List<Potion> potions = getPotions(hero);

        if (potions.isEmpty()) {
            return;
        }

        showEvents(
                session.playHeroAction(
                        new PotionAction(hero, potions.get(0))
                )
        );

        updateControls();
    }

    private void skipTurn() {
        Hero hero = getActingHero();

        if (hero == null) {
            return;
        }

        Encounter encounter =
                session.getGameState().getCurrentEncounter();

        boolean canAttack =
                !getAvailableTargets(hero, encounter).isEmpty();

        boolean canHeal =
                hero.getHealth() < hero.getMaxHealth()
                        && !getPotions(hero).isEmpty();

        if (canAttack || canHeal) {
            return;
        }

        showEvents(session.skipCurrentHero());
        updateControls();
    }

    private void playEnemyAction() {
        if (session == null || session.isFinished()) {
            return;
        }

        if (session.getCurrentHero() != null) {
            return;
        }

        showEvents(session.playNextEnemyAction());
        updateControls();
    }

    private Hero getActingHero() {
        if (session == null || session.isFinished()) {
            return null;
        }

        return session.getCurrentHero();
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

    private List<Potion> getPotions(Hero hero) {
        List<Potion> potions = new ArrayList<>();

        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Potion potion) {
                potions.add(potion);
            }
        }

        return potions;
    }

    private int chooseIndex(
            String title,
            String header,
            List<String> choices
    ) {
        if (choices.isEmpty()) {
            return -1;
        }

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
        potionButton.setDisable(true);
        skipButton.setDisable(true);
        enemyActionButton.setDisable(true);

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
                    "Turno dei nemici: premi Prossima azione nemica."
            );

            enemyActionButton.setDisable(false);
            return;
        }

        Encounter encounter =
                session.getGameState().getCurrentEncounter();

        boolean canAttack =
                !getAvailableTargets(hero, encounter).isEmpty();

        boolean canHeal =
                hero.getHealth() < hero.getMaxHealth()
                        && !getPotions(hero).isEmpty();

        attackButton.setDisable(!canAttack);
        potionButton.setDisable(!canHeal);
        skipButton.setDisable(canAttack || canHeal);

        String status =
                "Turno di " + hero.getName()
                        + " - HP: " + hero.getHealth()
                        + "/" + hero.getMaxHealth();

        if (!canAttack && !canHeal) {
            status += " — Nessuna azione disponibile: passa il turno.";
        }

        statusLabel.setText(status);
    }

    private void showEvents(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            eventArea.appendText(event.message() + "\n");
        }
    }
}