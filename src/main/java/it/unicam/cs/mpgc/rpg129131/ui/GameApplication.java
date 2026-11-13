package it.unicam.cs.mpgc.rpg129131.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import it.unicam.cs.mpgc.rpg129131.combat.ActionAvailability;
import it.unicam.cs.mpgc.rpg129131.combat.*;
import it.unicam.cs.mpgc.rpg129131.game.GameSession;
import it.unicam.cs.mpgc.rpg129131.game.GameState;
import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.persistence.GameSaveService;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import javafx.util.Duration;
import it.unicam.cs.mpgc.rpg129131.game.GameSessionFactory;
import it.unicam.cs.mpgc.rpg129131.persistence.GamePersistence;

/**
 * Implementa l’interfaccia JavaFX e collega i comandi del giocatore alla sessione.
 * Gestisce schermate, finestre di dialogo e ritardo fra gli attacchi nemici.
 * Delega regole e avanzamento a GameSession e persistenza al contratto GamePersistence.
 * Le operazioni sui file sono eseguite sincronicamente dai gestori JavaFX.
 */
public class GameApplication extends Application {

    /** Contratto di persistenza usato dai comandi di salvataggio e caricamento. */
    private final GamePersistence saveService;


    /** Criteri condivisi per individuare bersagli e oggetti disponibili. */
    private final ActionAvailability actionAvailability = new ActionAvailability();

    /**
     * Unico slot di salvataggio, relativo alla directory di avvio del programma.
     */
    private final Path saveFile =
            Path.of("saves", "partita.json");

    /**
     * Attesa di 0,7 secondi prima di eseguire una singola azione nemica.
     */
    private final PauseTransition enemyPause =
            new PauseTransition(Duration.seconds(0.7));

    /**
     * Sessione corrente; null prima di avvio o caricamento.
     */
    private GameSession session;
    /**
     * Finestra principale, proprietaria dei dialoghi.
     */
    private Stage window;
    /**
     * Scena nella quale alternare menu e combattimento.
     */
    private Scene scene;
    /** Schermata che contiene i controlli del combattimento. */
    private CombatView combatView;


    /**
     * Configura la persistenza JSON predefinita.
     * Il costruttore pubblico senza argomenti permette l'avvio tramite JavaFX.
     */
    public GameApplication() {
        this(new GameSaveService());
    }

    /**
     * Configura l'applicazione con il servizio di persistenza fornito.
     * Consente di sostituire l'implementazione senza modificare
     * i gestori dei comandi di salvataggio e caricamento.
     *
     * @param saveService implementazione della persistenza
     * @throws IllegalArgumentException se il servizio è null
     */
    public GameApplication(GamePersistence saveService) {
        if (saveService == null) {
            throw new IllegalArgumentException(
                    "Il servizio di persistenza non può essere null."
            );
        }

        this.saveService = saveService;
    }

    /**
     * Crea la finestra principale, registra i gestori degli eventi e mostra il menu.
     * La chiusura di una partita attiva richiede conferma e non salva automaticamente.
     *
     * @param stage finestra principale fornita da JavaFX
     */
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

    /**
     * Ferma l’attesa degli attacchi nemici quando JavaFX arresta l’applicazione.
     */
    @Override
    public void stop() {
        enemyPause.stop();
    }

    /**
     * Costruisce la schermata e collega i suoi comandi ai gestori dell'applicazione.
     */
    private void createCombatView() {
        combatView = new CombatView(
                this::showMainMenu,
                this::attack,
                this::usePotion,
                this::skipTurn,
                this::saveGame,
                this::requestExit
        );

        refreshView();
    }

    /**
     * Ferma le azioni automatiche e sostituisce la schermata con un nuovo menu.
     * Abilita Continua in base alla sessione e Carica in base all’esistenza del file;
     * la validità del contenuto viene verificata soltanto al caricamento.
     */
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

    /**
     * Riprende la sessione attiva già in memoria, senza leggere il file JSON.
     */
    private void continueGame() {
        if (!hasActiveGame()) {
            return;
        }

        showCombat();
    }

    /**
     * Mostra il pannello del combattimento e aggiorna controlli e turno.
     */
    private void showCombat() {
        scene.setRoot(combatView);
        refreshView();
    }

    /**
     * Chiede conferma prima di abbandonare una sessione attiva.
     * Crea e avvia una nuova partita, svuota il registro e mostra il combattimento.
     * Non elimina il file salvato.
     */
    private void startNewGame() {
        if (hasActiveGame()
                && !confirm(
                "Iniziare una nuova partita?",
                "La partita in corso verrà abbandonata."
        )) {
            return;
        }

        enemyPause.stop();

        session = new GameSessionFactory().create();

        combatView.clearEvents();
        showEvents(session.startNewGame());
        showCombat();
    }

    /**
     * Salva manualmente la partita attiva, chiedendo conferma prima della sovrascrittura.
     * Ferma il timer durante l’operazione e mostra successo o errore. Alla fine
     * aggiorna la vista, riattivando la fase nemica se necessario.
     */
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

            combatView.appendEvent("Partita salvata.");
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

    /**
     * Chiede conferma prima di sostituire una sessione attiva e prova a leggere il file.
     * Assegna la nuova sessione soltanto dopo il caricamento riuscito;
     * un errore di lettura o validazione mantiene quella precedente.
     */
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

            // Sostituisce la sessione aperta soltanto dopo un caricamento riuscito.
            session = loadedSession;

            combatView.clearEvents();
            combatView.appendEvent("Partita caricata dal salvataggio.");

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

    /**
     * Mostra una finestra informativa o di errore e attende la chiusura.
     * Non gestisce autonomamente il timer: questa responsabilità resta al chiamante.
     *
     * @param type tipo di avviso JavaFX
     * @param header intestazione della finestra
     * @param message testo; se null viene mostrato un messaggio generico
     */
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

    /**
     * Fa scegliere bersaglio e arma fra quelli disponibili e invia l’attacco alla sessione.
     * Annullare una delle scelte non invia alcuna azione e non avanza il turno.
     */
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

    /**
     * Invia l’uso della prima pozione disponibile se l’eroe corrente è ferito.
     * Cura e consumo effettivi sono eseguiti dalla logica di combattimento.
     */
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

    /**
     * Richiede il passaggio del turno soltanto se non risultano attacchi o cure disponibili.
     * La sessione ripete il controllo per proteggere la regola anche fuori dalla vista.
     */
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

    /**
     * Esegue una sola azione nemica allo scadere del timer, se il combattimento
     * è visibile e la fase nemica è ancora attiva. Poi aggiorna la schermata.
     */
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

    /**
     * Individua l’eroe utilizzabile dai gestori dei comandi.
     *
     * @return eroe corrente oppure null se non è possibile agire dalla vista
     */
    private Hero getActingHero() {
        if (!isCombatVisible() || !hasActiveGame()) {
            return null;
        }

        return session.getCurrentHero();
    }

    /**
     * Verifica presenza della sessione, avvio e mancata conclusione.
     *
     * @return true se la sessione è avviata e non terminata
     */
    private boolean hasActiveGame() {
        return session != null
                && session.isStarted()
                && !session.isFinished();
    }

    /**
     * Verifica se il pannello di combattimento è la radice della scena corrente.
     *
     * @return true se la scena esiste e mostra il combattimento
     */
    private boolean isCombatVisible() {
        return scene != null && scene.getRoot() == combatView;
    }

    /**
     * Delega al servizio condiviso la selezione dei bersagli attaccabili.
     *
     * @param hero eroe da valutare
     * @param encounter incontro corrente
     * @return bersagli selezionabili nell'ordine dell'incontro
     */
    private List<Enemy> getAvailableTargets(Hero hero, Encounter encounter) {
        return actionAvailability.getAvailableTargets(hero, encounter);
    }

    /**
     * Delega al servizio condiviso la selezione delle armi utilizzabili.
     *
     * @param hero proprietario dell'inventario
     * @param target bersaglio scelto
     * @return armi selezionabili nell'ordine dell'inventario
     */
    private List<Weapon> getUsableWeapons(Hero hero, Enemy target) {
        return actionAvailability.getUsableWeapons(hero, target);
    }

    /**
     * Delega al servizio condiviso la ricerca delle pozioni possedute.
     *
     * @param hero proprietario dell'inventario
     * @return pozioni nell'ordine dell'inventario
     */
    private List<Potion> getPotions(Hero hero) {
        return actionAvailability.getPotions(hero);
    }

    /**
     * Mostra una scelta modale fra le opzioni e restituisce la posizione selezionata.
     *
     * @param title titolo della finestra
     * @param header intestazione della scelta
     * @param choices opzioni visualizzate
     * @return indice da zero, oppure -1 se la lista è vuota o la scelta è annullata
     */
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

    /**
     * Ferma il timer e attende una conferma. Se l’utente annulla, aggiorna
     * il combattimento visibile per consentire la ripresa della fase nemica.
     *
     * @param header intestazione della richiesta
     * @param message spiegazione dell’operazione
     * @return true soltanto se viene premuto OK
     */
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

    /**
     * Richiede conferma per abbandonare una sessione attiva, poi ferma il timer
     * e chiude la finestra. Non esegue un salvataggio automatico.
     */
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

    /**
     * Aggiorna testi e comandi in base alla sessione.
     * Mantiene nell'applicazione il coordinamento del timer dei nemici.
     */
    private void refreshView() {
        enemyPause.stop();

        combatView.setActionsEnabled(false, false, false);
        combatView.setSaveEnabled(hasActiveGame());

        if (session == null) {
            combatView.setEncounterText("Nessuna partita avviata.");
            combatView.setTurnText("Premi Nuova partita per iniziare.");
            combatView.setStatusText("");
            return;
        }

        GameState state = session.getGameState();

        combatView.setEncounterText(
                "Livello " + (state.getCurrentLevelIndex() + 1)
                        + " | Combattimento "
                        + (state.getCurrentEncounterIndex() + 1)
        );

        Encounter encounter = state.getCurrentEncounter();

        if (encounter != null) {
            updateStatus(encounter);
        } else {
            combatView.setStatusText(
                    "Tutti i combattimenti sono stati completati."
            );
        }

        if (session.isFinished()) {
            if (state.isGameCompleted()) {
                combatView.setTurnText(
                        "Vittoria! Hai completato tutti i livelli."
                );
            } else {
                combatView.setTurnText(
                        "Sconfitta. Puoi iniziare una nuova partita."
                );
            }

            return;
        }

        Hero hero = session.getCurrentHero();

        if (hero == null) {
            combatView.setTurnText(
                    "Turno dei nemici: azioni automatiche in corso."
            );

            if (isCombatVisible()) {
                enemyPause.playFromStart();
            }

            return;
        }

        boolean canAttack =
                !getAvailableTargets(hero, encounter).isEmpty();

        boolean canHeal =
                hero.getHealth() < hero.getMaxHealth()
                        && !getPotions(hero).isEmpty();

        combatView.setActionsEnabled(
                canAttack,
                canHeal,
                !canAttack && !canHeal
        );

        String text = "Turno di " + hero.getName()
                + " - HP: " + hero.getHealth()
                + "/" + hero.getMaxHealth();

        if (!canAttack && !canHeal) {
            text += " — Nessuna azione disponibile: passa il turno.";
        }

        combatView.setTurnText(text);
    }

    /**
     * Mostra salute, pozioni e stato di sconfitta degli eroi e dei nemici.
     *
     * @param encounter incontro del quale visualizzare i partecipanti
     */
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

        combatView.setStatusText(text.toString());
    }

    /**
     * Invia alla schermata i messaggi prodotti dalla sessione.
     *
     * @param events eventi da visualizzare nell'ordine ricevuto
     */
    private void showEvents(List<CombatEvent> events) {
        for (CombatEvent event : events) {
            combatView.appendEvent(event.message());
        }
    }
}
