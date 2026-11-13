package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.combat.ActionResult;
import it.unicam.cs.mpgc.rpg129131.combat.CombatController;
import it.unicam.cs.mpgc.rpg129131.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg129131.combat.HeroAction;
import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.combat.ActionAvailability;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina il ciclo della partita: preparazione, turni e avanzamento degli incontri.
 * Delega le azioni al controllore di combattimento e restituisce eventi alle viste.
 * Non dipende da JavaFX o dalla console e non gestisce direttamente i file JSON.
 */
public class GameSession {

    /** Criteri condivisi usati per verificare se il turno può essere saltato. */
    private final ActionAvailability actionAvailability = new ActionAvailability();

    /** Stato della partita coordinata dalla sessione. */
    private final GameState gameState;

    /** Controllore al quale delegare le singole azioni. */
    private final CombatController combatController;

    /** Servizio per armi, sblocchi e pozioni. */
    private final LevelPreparation levelPreparation;

    /** Indica che avvio o ripristino sono stati completati. */
    private boolean started;

    /** True durante la fase dei nemici, false durante quella degli eroi. */
    private boolean enemiesTurn;

    /** Indice dell'eroe corrente o dimensione del gruppo nella fase nemica. */
    private int heroIndex;

    /** Indice dal quale cercare il prossimo nemico vivo che deve agire. */
    private int enemyIndex;

    /**
     * Crea una sessione non ancora avviata usando lo stato e i servizi forniti.
     *
     * @param gameState stato sul quale operare
     * @param combatController controllore delle singole azioni
     * @param levelPreparation servizio di preparazione delle dotazioni
     * @throws IllegalArgumentException se una dipendenza è null
     */
    public GameSession(
            GameState gameState,
            CombatController combatController,
            LevelPreparation levelPreparation
    ) {
        if (gameState == null
                || combatController == null
                || levelPreparation == null) {
            throw new IllegalArgumentException(
                    "I componenti della partita non possono essere null."
            );
        }

        this.gameState = gameState;
        this.combatController = combatController;
        this.levelPreparation = levelPreparation;
    }

    /**
     * Avvia una sola volta una partita in posizione iniziale.
     * Prepara armi e pozioni e inizia il primo round.
     * Non deve essere chiamato per riprendere un salvataggio.
     *
     * @return eventi iniziali della partita
     * @throws IllegalStateException se la sessione è già avviata
     *                               o lo stato iniziale non è valido
     */
    public List<CombatEvent> startNewGame() {
        if (started) {
            throw new IllegalStateException(
                    "La sessione è già stata avviata."
            );
        }

        if (gameState.getCurrentLevelIndex() != 0
                || gameState.getCurrentEncounterIndex() != 0
                || gameState.isGameCompleted()
                || gameState.isGameOver()) {
            throw new IllegalStateException(
                    "Per avviare una nuova partita "
                            + "serve uno stato iniziale valido."
            );
        }

        List<CombatEvent> events = new ArrayList<>();
        events.add(new CombatEvent("=== SUPERNATURAL RPG ==="));

        prepareCurrentLevel(events);
        prepareCurrentEncounter(events);

        started = true;
        beginRound();

        return events;
    }

    /**
     * Verifica che la richiesta appartenga all'eroe corrente e la esegue.
     * Controlla subito la fine dell'incontro; se continua, passa al prossimo eroe.
     * Una richiesta rifiutata restituisce i messaggi senza cambiare turno.
     * Un attacco eseguito consuma il turno anche se infligge zero danni.
     *
     * @param action azione richiesta dall'eroe corrente
     * @return eventi dell'azione e delle eventuali transizioni
     * @throws IllegalArgumentException se l'azione è null, riguarda un altro eroe
     *                                  o non è supportata dal sistema di combattimento
     * @throws IllegalStateException se la partita non è attiva
     *                               o è la fase nemica
     */
    public List<CombatEvent> playHeroAction(HeroAction action) {
        requireActiveGame();

        Hero currentHero = getCurrentHero();

        if (currentHero == null) {
            throw new IllegalStateException(
                    "È il turno dei nemici."
            );
        }

        if (action == null) {
            throw new IllegalArgumentException(
                    "L'azione non può essere null."
            );
        }

        if (action.hero() != currentHero) {
            throw new IllegalArgumentException(
                    "Deve agire " + currentHero.getName() + "."
            );
        }

        Encounter encounter = gameState.getCurrentEncounter();

        ActionResult result =
                combatController.executeHeroActionResult(encounter, action);

        List<CombatEvent> events = new ArrayList<>(result.events());

        if (!result.executed()) {
            return events;
        }

        if (resolveEncounter(encounter, events)) {
            return events;
        }

        heroIndex++;
        moveToNextLivingHero();

        return events;
    }

    /**
     * Passa al prossimo eroe soltanto se quello corrente non può attaccare né curarsi.
     *
     * @return evento che descrive il passaggio del turno
     * @throws IllegalStateException se la partita non è attiva, è la fase nemica
     *                               o esiste un'azione disponibile
     */
    public List<CombatEvent> skipCurrentHero() {
        requireActiveGame();

        Hero hero = getCurrentHero();

        if (hero == null) {
            throw new IllegalStateException(
                    "È il turno dei nemici."
            );
        }

        if (hasAvailableAction(hero)) {
            throw new IllegalStateException(
                    hero.getName() + " ha ancora azioni disponibili."
            );
        }

        List<CombatEvent> events = List.of(
                new CombatEvent(
                        hero.getName()
                                + " non ha azioni disponibili "
                                + "e passa il turno."
                )
        );

        heroIndex++;
        moveToNextLivingHero();

        return events;
    }

    /**
     * Esegue l'azione del prossimo nemico vivo nella fase nemica.
     * Controlla la fine dell'incontro e inizia un nuovo round
     * quando tutti i nemici ancora vivi hanno agito.
     *
     * @return eventi dell'attacco e delle eventuali transizioni
     * @throws IllegalStateException se la sessione non è attiva,
     *                               devono agire gli eroi o non esiste un nemico disponibile
     */
    public List<CombatEvent> playNextEnemyAction() {
        requireActiveGame();

        if (!enemiesTurn) {
            throw new IllegalStateException(
                    "Devono ancora agire gli eroi."
            );
        }

        Encounter encounter = gameState.getCurrentEncounter();
        List<Enemy> enemies = encounter.getEnemies();

        while (enemyIndex < enemies.size()
                && !enemies.get(enemyIndex).isAlive()) {
            enemyIndex++;
        }

        if (enemyIndex >= enemies.size()) {
            throw new IllegalStateException(
                    "Non è disponibile un nemico che possa agire."
            );
        }

        Enemy enemy = enemies.get(enemyIndex);

        List<CombatEvent> events = new ArrayList<>(
                combatController.executeEnemyAction(encounter, enemy)
        );

        if (resolveEncounter(encounter, events)) {
            return events;
        }

        enemyIndex++;

        while (enemyIndex < enemies.size()
                && !enemies.get(enemyIndex).isAlive()) {
            enemyIndex++;
        }

        if (enemyIndex >= enemies.size()) {
            events.add(new CombatEvent("Round terminato."));
            beginRound();
        }

        return events;
    }

    /**
     * Individua l'eroe corrente nella partita attiva.
     *
     * @return eroe che deve agire oppure null durante la fase nemica
     * @throws IllegalStateException se la sessione non è avviata o è terminata
     */
    public Hero getCurrentHero() {
        requireActiveGame();

        if (enemiesTurn) {
            return null;
        }

        return gameState.getCurrentParty()
                .getMembers()
                .get(heroIndex);
    }

    /**
     * Espone lo stato associato alla sessione senza duplicarlo.
     *
     * @return stato della partita
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Indica se la sessione è stata avviata o ripristinata.
     *
     * @return true dopo avvio o ripristino riuscito
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Crea una fotografia della fase corrente e dei relativi indici.
     *
     * @return dati del turno da inserire nel salvataggio
     * @throws IllegalStateException se la sessione non è attiva
     */
    public TurnState getTurnState() {
        requireActiveGame();

        return new TurnState(
                enemiesTurn,
                heroIndex,
                enemyIndex
        );
    }

    /**
     * Ripristina il turno e avvia una sessione non ancora iniziata.
     * Richiede che posizione, salute e inventari siano già stati ricostruiti.
     * Non assegna nuove dotazioni e non ricomincia il round.
     *
     * @param savedTurn fase e indici da ripristinare
     * @throws IllegalArgumentException se i dati del turno sono null o incoerenti
     * @throws IllegalStateException se la sessione è già avviata
     *                               o la partita è terminata
     */
    public void restoreTurnState(TurnState savedTurn) {
        if (savedTurn == null) {
            throw new IllegalArgumentException(
                    "Lo stato del turno non può essere null."
            );
        }

        if (started) {
            throw new IllegalStateException(
                    "Il ripristino richiede una sessione non ancora avviata."
            );
        }

        if (isFinished()) {
            throw new IllegalStateException(
                    "Non è possibile riprendere una partita terminata."
            );
        }

        Encounter encounter = gameState.getCurrentEncounter();
        List<Hero> heroes = encounter.getParty().getMembers();
        List<Enemy> enemies = encounter.getEnemies();

        if (savedTurn.enemiesTurn()) {
            if (savedTurn.heroIndex() != heroes.size()) {
                throw new IllegalArgumentException(
                        "Nel turno dei nemici tutti gli eroi "
                                + "devono avere concluso il turno."
                );
            }

            if (savedTurn.enemyIndex() >= enemies.size()) {
                throw new IllegalArgumentException(
                        "L'indice del nemico salvato non è valido."
                );
            }

            // Un indice relativo a un nemico sconfitto verrà saltato all'esecuzione.
            boolean hasNextLivingEnemy = false;

            for (int i = savedTurn.enemyIndex(); i < enemies.size(); i++) {
                if (enemies.get(i).isAlive()) {
                    hasNextLivingEnemy = true;
                    break;
                }
            }

            if (!hasNextLivingEnemy) {
                throw new IllegalArgumentException(
                        "Non ci sono nemici vivi che debbano ancora agire."
                );
            }
        } else {
            if (savedTurn.heroIndex() >= heroes.size()
                    || !heroes.get(savedTurn.heroIndex()).isAlive()) {
                throw new IllegalArgumentException(
                        "L'eroe del turno salvato non è valido o è sconfitto."
                );
            }

            if (savedTurn.enemyIndex() != 0) {
                throw new IllegalArgumentException(
                        "Nel turno degli eroi l'indice del nemico deve essere zero."
                );
            }
        }

        // Assegna lo stato soltanto dopo aver completato tutti i controlli.
        enemiesTurn = savedTurn.enemiesTurn();
        heroIndex = savedTurn.heroIndex();
        enemyIndex = savedTurn.enemyIndex();
        started = true;
    }

    /**
     * Ricava la fine della partita dalla vittoria finale o dalla sconfitta del gruppo.
     *
     * @return true se il gioco è completato o il gruppo corrente è sconfitto
     */
    public boolean isFinished() {
        return gameState.isGameCompleted()
                || gameState.isGameOver();
    }

    /**
     * Inizia la fase degli eroi azzerando gli indici e saltando gli eroi sconfitti.
     */
    private void beginRound() {
        enemiesTurn = false;
        heroIndex = 0;
        enemyIndex = 0;

        moveToNextLivingHero();
    }

    /**
     * Avanza oltre gli eroi sconfitti.
     * Se non restano eroi da far agire, passa alla fase nemica.
     */
    private void moveToNextLivingHero() {
        List<Hero> heroes = gameState.getCurrentParty().getMembers();

        while (heroIndex < heroes.size()
                && !heroes.get(heroIndex).isAlive()) {
            heroIndex++;
        }

        if (heroIndex >= heroes.size()) {
            enemiesTurn = true;
        }
    }

    /**
     * Valuta sconfitta o vittoria dopo un'azione.
     * In caso di vittoria cura il gruppo e, se il gioco non è terminato,
     * avanza, prepara il nuovo incontro e ricomincia il round.
     *
     * @param encounter incontro nel quale è stata eseguita l'azione
     * @param events lista alla quale aggiungere esito e transizioni
     * @return true se l'incontro è risolto; false se continua
     * @throws IllegalStateException se non è possibile avanzare dopo una vittoria non finale
     */
    private boolean resolveEncounter(
            Encounter encounter,
            List<CombatEvent> events
    ) {
        if (!encounter.getParty().hasLivingMembers()) {
            events.add(
                    new CombatEvent(
                            combatController.getCombatResult(encounter)
                    )
            );

            return true;
        }

        if (!encounter.isCompleted()) {
            return false;
        }

        events.add(
                new CombatEvent(
                        combatController.getCombatResult(encounter)
                )
        );

        for (Hero hero : encounter.getParty().getMembers()) {
            hero.restoreHealth();
        }

        events.add(
                new CombatEvent(
                        "Incontro superato! "
                                + "Tutto il gruppo recupera la vita."
                )
        );

        if (gameState.isGameCompleted()) {
            return true;
        }

        // Le armi cambiano a ogni livello, le pozioni a ogni incontro.
        int previousLevelIndex = gameState.getCurrentLevelIndex();

        if (!gameState.advanceEncounter()) {
            throw new IllegalStateException(
                    "Impossibile avanzare dopo l'incontro vinto."
            );
        }

        if (gameState.getCurrentLevelIndex() != previousLevelIndex) {
            prepareCurrentLevel(events);
        }

        prepareCurrentEncounter(events);
        beginRound();

        return true;
    }

    /**
     * Verifica tramite il servizio condiviso se l'eroe può attaccare o curarsi.
     *
     * @param hero eroe corrente
     * @return true se esiste almeno un'azione disponibile
     */
    private boolean hasAvailableAction(Hero hero) {
        return actionAvailability.hasAvailableAction(
                hero,
                gameState.getCurrentEncounter()
        );
    }

    /**
     * Sblocca il gruppo e prepara le armi per il livello corrente.
     *
     * @param events lista nella quale registrare l'inizio del livello
     * @throws IllegalStateException se il gruppo corrente non è disponibile
     */
    private void prepareCurrentLevel(List<CombatEvent> events) {
        LevelNumber number = gameState.getCurrentLevel().getNumber();
        Party party = gameState.getCurrentParty();

        if (party == null) {
            throw new IllegalStateException(
                    "Non è disponibile un gruppo per il livello corrente."
            );
        }

        levelPreparation.prepare(number, party);

        events.add(
                new CombatEvent(
                        "Inizio livello " + number
                                + ": gruppo sbloccato "
                                + "e dotazioni preparate."
                )
        );
    }

    /**
     * Assegna una pozione per eroe senza accumulare quelle residue.
     *
     * @param events lista nella quale registrare l'inizio dell'incontro
     */
    private void prepareCurrentEncounter(List<CombatEvent> events) {
        Party party = gameState.getCurrentParty();
        levelPreparation.prepareEncounter(party);

        int encounterNumber = gameState.getCurrentEncounterIndex() + 1;

        events.add(
                new CombatEvent(
                        "Inizio incontro " + encounterNumber
                                + ": ogni eroe riceve una pozione. "
                                + "Le pozioni non usate non si accumulano."
                )
        );
    }

    /**
     * Richiede che la sessione sia stata avviata e che la partita non sia terminata.
     *
     * @throws IllegalStateException se manca una di queste condizioni
     */
    private void requireActiveGame() {
        if (!started) {
            throw new IllegalStateException(
                    "La sessione non è ancora stata avviata."
            );
        }

        if (isFinished()) {
            throw new IllegalStateException(
                    "La partita è terminata."
            );
        }
    }
}
