package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameApplication extends Application {

    private final GameSaveService saveService =
            new GameSaveService();

    private final Path saveFile =
            Path.of("saves", "partita.json");

    private final PauseTransition enemyPause =
            new PauseTransition(Duration.seconds(0.7));

    private GameSession session;
    private Stage window;
    private Scene scene;
    private VBox combatView;

    private Label encounterLabel;
    private Label turnLabel;

    private TextArea statusArea;
    private TextArea eventArea;

    private Button attackButton;
    private Button potionButton;
    private Button skipButton;
    private Button saveButton;

    @Override
    public void start(Stage stage) {
        window = stage;

        enemyPause.setOnFinished(event -> playEnemyAction());

        createCombatView();

        scene = new Scene(combatView, 1100, 800);

        stage.setTitle("Supernatural RPG");
        stage.setMinWidth(750);
        stage.setMinHeight(700);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            if (hasActiveGame()
                    && !confirm(
                    "Uscire dal gioco?",
                    "Le modifiche successive all'ultimo salvataggio andranno perse."
            )) {
                event.consume();
                return;
            }

            enemyPause.stop();
        });

        showMainMenu();
        stage.show();
    }

    @Override
    public void stop() {
        enemyPause.stop();
    }

    private void createCombatView() {
        Label title = new Label("SUPERNATURAL RPG");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        encounterLabel = new Label();
        turnLabel = new Label();
        turnLabel.setWrapText(true);

        statusArea = new TextArea();
        statusArea.setEditable(false);
        statusArea.setWrapText(true);
        statusArea.setPrefRowCount(9);

        eventArea = new TextArea();
        eventArea.setEditable(false);
        eventArea.setWrapText(true);
        eventArea.setPrefRowCount(10);

        Button menuButton = new Button("Menu principale");
        menuButton.setOnAction(event -> showMainMenu());

        attackButton = new Button("Attacca");
        attackButton.setOnAction(event -> attack());

        potionButton = new Button("Usa pozione");
        potionButton.setOnAction(event -> usePotion());

        skipButton = new Button("Passa turno");
        skipButton.setOnAction(event -> skipTurn());

        saveButton = new Button("Salva partita");
        saveButton.setOnAction(event -> saveGame());

        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> requestExit());

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

        combatView = new VBox(
                12,
                title,
                encounterLabel,
                turnLabel,
                controls,
                new Label("Stato del combattimento"),
                statusArea,
                new Label("Eventi"),
                eventArea
        );

        combatView.setAlignment(Pos.TOP_CENTER);
        combatView.setPadding(new Insets(24));

        VBox.setVgrow(statusArea, Priority.ALWAYS);
        VBox.setVgrow(eventArea, Priority.ALWAYS);

        refreshView();
    }

    private void showMainMenu() {
        enemyPause.stop();

        MainMenuView menu = new MainMenuView(
                hasActiveGame(),
                Files.isRegularFile(saveFile),
                this::startNewGame,
                this::continueGame,
                this::loadGame,
                this::requestExit
        );

        scene.setRoot(menu);
    }

    private void continueGame() {
        if (!hasActiveGame()) {
            return;
        }

        showCombat();
    }

    private void showCombat() {
        scene.setRoot(combatView);
        refreshView();
    }

    private void startNewGame() {
        if (hasActiveGame()
                && !confirm(
                "Iniziare una nuova partita?",
                "La partita in corso verrà abbandonata."
        )) {
            return;
        }

        enemyPause.stop();

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
        showCombat();
    }

    private void saveGame() {
        if (!hasActiveGame()) {
            return;
        }

        enemyPause.stop();

        try {
            if (Files.exists(saveFile)
                    && !confirm(
                    "Sovrascrivere il salvataggio?",
                    "Il salvataggio precedente verrà sostituito."
            )) {
                return;
            }

            saveService.save(saveFile, session);

            eventArea.appendText("Partita salvata.\n");

            showNotice(
                    Alert.AlertType.INFORMATION,
                    "Partita salvata",
                    "Salvataggio completato in:\n"
                            + saveFile.toAbsolutePath().normalize()
            );
        } catch (IOException e) {
            showNotice(
                    Alert.AlertType.ERROR,
                    "Salvataggio non riuscito",
                    e.getMessage()
            );
        } finally {
            if (isCombatVisible()) {
                refreshView();
            }
        }
    }

    private void loadGame() {
        if (hasActiveGame()
                && !confirm(
                "Caricare la partita salvata?",
                "La sessione aperta verrà sostituita. "
                        + "Le modifiche non salvate andranno perse."
        )) {
            return;
        }

        enemyPause.stop();

        try {
            GameSession loadedSession = saveService.load(saveFile);

            session = loadedSession;

            eventArea.clear();
            eventArea.appendText("Partita caricata dal salvataggio.\n");

            showCombat();
        } catch (IOException e) {
            showNotice(
                    Alert.AlertType.ERROR,
                    "Caricamento non riuscito",
                    e.getMessage()
            );

            if (isCombatVisible()) {
                refreshView();
            }
        }
    }

    private void showNotice(
            Alert.AlertType type,
            String header,
            String message
    ) {
        Alert alert = new Alert(type);
        alert.initOwner(window);
        alert.setTitle("Supernatural RPG");
        alert.setHeaderText(header);
        alert.setContentText(
                message == null ? "Operazione non riuscita." : message
        );
        alert.showAndWait();
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
            Weapon weapon = weapons.get(i);

            weaponNames.add(
                    (i + 1) + ") " + weapon.getName()
                            + " - Moltiplicatore: "
                            + weapon.getMultiplierAgainst(target)
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

        showEvents(
                session.playHeroAction(
                        new AttackAction(
                                hero,
                                weapons.get(weaponIndex),
                                target
                        )
                )
        );

        refreshView();
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

        refreshView();
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
        refreshView();
    }

    private void playEnemyAction() {
        if (!isCombatVisible() || !hasActiveGame()) {
            return;
        }

        if (session.getCurrentHero() != null) {
            return;
        }

        showEvents(session.playNextEnemyAction());
        refreshView();
    }

    private Hero getActingHero() {
        if (!isCombatVisible() || !hasActiveGame()) {
            return null;
        }

        return session.getCurrentHero();
    }

    private boolean hasActiveGame() {
        return session != null
                && session.isStarted()
                && !session.isFinished();
    }

    private boolean isCombatVisible() {
        return scene != null && scene.getRoot() == combatView;
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

        dialog.initOwner(window);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText("Seleziona:");

        Optional<String> selected = dialog.showAndWait();

        if (selected.isEmpty()) {
            return -1;
        }

        return choices.indexOf(selected.get());
    }

    private boolean confirm(String header, String message) {
        enemyPause.stop();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(window);
        alert.setTitle("Supernatural RPG");
        alert.setHeaderText(header);
        alert.setContentText(message);

        boolean confirmed = alert.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .isPresent();

        if (!confirmed && isCombatVisible()) {
            refreshView();
        }

        return confirmed;
    }

    private void requestExit() {
        if (hasActiveGame()
                && !confirm(
                "Uscire dal gioco?",
                "Le modifiche successive all'ultimo salvataggio andranno perse."
        )) {
            return;
        }

        enemyPause.stop();
        window.close();
    }

    private void refreshView() {
        enemyPause.stop();

        attackButton.setDisable(true);
        potionButton.setDisable(true);
        skipButton.setDisable(true);
        saveButton.setDisable(!hasActiveGame());

        if (session == null) {
            encounterLabel.setText("Nessuna partita avviata.");
            turnLabel.setText("Premi Nuova partita per iniziare.");
            statusArea.clear();
            return;
        }

        GameState state = session.getGameState();

        encounterLabel.setText(
                "Livello " + (state.getCurrentLevelIndex() + 1)
                        + " | Combattimento "
                        + (state.getCurrentEncounterIndex() + 1)
        );

        Encounter encounter = state.getCurrentEncounter();

        if (encounter != null) {
            updateStatus(encounter);
        } else {
            statusArea.setText(
                    "Tutti i combattimenti sono stati completati."
            );
        }

        if (session.isFinished()) {
            if (state.isGameCompleted()) {
                turnLabel.setText(
                        "Vittoria! Hai completato tutti i livelli."
                );
            } else {
                turnLabel.setText(
                        "Sconfitta. Puoi iniziare una nuova partita."
                );
            }

            return;
        }

        Hero hero = session.getCurrentHero();

        if (hero == null) {
            turnLabel.setText(
                    "Turno dei nemici: azioni automatiche in corso."
            );

            if (isCombatVisible()) {
                enemyPause.playFromStart();
            }

            return;
        }

        boolean canAttack =
                !getAvailableTargets(
                        hero,
                        state.getCurrentEncounter()
                ).isEmpty();

        boolean canHeal =
                hero.getHealth() < hero.getMaxHealth()
                        && !getPotions(hero).isEmpty();

        attackButton.setDisable(!canAttack);
        potionButton.setDisable(!canHeal);
        skipButton.setDisable(canAttack || canHeal);

        String text = "Turno di " + hero.getName()
                + " - HP: " + hero.getHealth()
                + "/" + hero.getMaxHealth();

        if (!canAttack && !canHeal) {
            text += " — Nessuna azione disponibile: passa il turno.";
        }

        turnLabel.setText(text);
    }

    private void updateStatus(Encounter encounter) {
        StringBuilder text = new StringBuilder("EROI\n");

        for (Hero hero : encounter.getParty().getMembers()) {
            text.append(hero.getName())
                    .append(" - HP: ")
                    .append(hero.getHealth())
                    .append("/")
                    .append(hero.getMaxHealth())
                    .append(" | Pozioni: ")
                    .append(getPotions(hero).size());

            if (!hero.isAlive()) {
                text.append(" | Sconfitto");
            }

            text.append("\n");
        }

        text.append("\nNEMICI\n");

        List<Enemy> enemies = encounter.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            text.append(i + 1)
                    .append(") ")
                    .append(enemy.getName())
                    .append(" - HP: ")
                    .append(enemy.getHealth())
                    .append("/")
                    .append(enemy.getMaxHealth());

            if (!enemy.isAlive()) {
                text.append(" | Sconfitto");
            }

            text.append("\n");
        }

        statusArea.setText(text.toString());
        statusArea.positionCaret(0);
    }

    private void showEvents(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            eventArea.appendText(event.message() + "\n");
        }

        eventArea.positionCaret(eventArea.getLength());
    }
}
