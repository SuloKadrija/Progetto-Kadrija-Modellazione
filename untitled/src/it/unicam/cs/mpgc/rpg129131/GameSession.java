package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private final GameState gameState;
    private final CombatController combatController;
    private final LevelPreparation levelPreparation;

    private boolean started;
    private boolean enemiesTurn;
    private int heroIndex;
    private int enemyIndex;

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

    public List<CombatEvent> playHeroAction(HeroAction action) {
        requireActiveGame();

        Hero currentHero = getCurrentHero();

        if (currentHero == null) {
            throw new IllegalStateException(
                    "È il turno dei nemici."
            );
        }

        Hero actingHero;

        if (action instanceof AttackAction attackAction) {
            actingHero = attackAction.hero();
        } else if (action instanceof PotionAction potionAction) {
            actingHero = potionAction.hero();
        } else {
            throw new IllegalArgumentException(
                    "L'azione non è supportata o è null."
            );
        }

        if (actingHero != currentHero) {
            throw new IllegalArgumentException(
                    "Deve agire " + currentHero.getName() + "."
            );
        }

        Encounter encounter = gameState.getCurrentEncounter();

        List<CombatEvent> events = new ArrayList<>(
                combatController.executeHeroAction(encounter, action)
        );

        if (resolveEncounter(encounter, events)) {
            return events;
        }

        heroIndex++;
        moveToNextLivingHero();

        return events;
    }

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

    public Hero getCurrentHero() {
        requireActiveGame();

        if (enemiesTurn) {
            return null;
        }

        return gameState.getCurrentParty()
                .getMembers()
                .get(heroIndex);
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isStarted() {
        return started;
    }

    public TurnState getTurnState() {
        requireActiveGame();

        return new TurnState(
                enemiesTurn,
                heroIndex,
                enemyIndex
        );
    }

    public void restoreTurnState(TurnState savedTurn) {
        if (savedTurn == null) {
            throw new IllegalArgumentException("Lo stato del turno non può essere null.");
        }

        if (started) {
            throw new IllegalStateException("Il ripristino richiede una sessione non ancora avviata.");
        }

        if (isFinished()) {
            throw new IllegalStateException("Non è possibile riprendere una partita terminata.");
        }

        Encounter encounter = gameState.getCurrentEncounter();
        List<Hero> heroes = encounter.getParty().getMembers();
        List<Enemy> enemies = encounter.getEnemies();

        if (savedTurn.enemiesTurn()) {
            if (savedTurn.heroIndex() != heroes.size()) {
                throw new IllegalArgumentException("Nel turno dei nemici tutti gli eroi devono avere concluso il turno.");
            }

            if (savedTurn.enemyIndex() >= enemies.size()) {
                throw new IllegalArgumentException("L'indice del nemico salvato non è valido.");
            }

            boolean hasNextLivingEnemy = false;

            for (int i = savedTurn.enemyIndex(); i < enemies.size(); i++) {
                if (enemies.get(i).isAlive()) {
                    hasNextLivingEnemy = true;
                    break;
                }
            }

            if (!hasNextLivingEnemy) {
                throw new IllegalArgumentException("Non ci sono nemici vivi che debbano ancora agire.");
            }
        } else {
            if (savedTurn.heroIndex() >= heroes.size() || !heroes.get(savedTurn.heroIndex()).isAlive()) {
                throw new IllegalArgumentException("L'eroe del turno salvato non è valido o è sconfitto.");
            }

            if (savedTurn.enemyIndex() != 0) {
                throw new IllegalArgumentException("Nel turno degli eroi l'indice del nemico deve essere zero.");
            }
        }

        enemiesTurn = savedTurn.enemiesTurn();
        heroIndex = savedTurn.heroIndex();
        enemyIndex = savedTurn.enemyIndex();
        started = true;
    }

    public boolean isFinished() {
        return gameState.isGameCompleted()
                || gameState.isGameOver();
    }

    private void beginRound() {
        enemiesTurn = false;
        heroIndex = 0;
        enemyIndex = 0;

        moveToNextLivingHero();
    }

    private void moveToNextLivingHero() {
        List<Hero> heroes =
                gameState.getCurrentParty().getMembers();

        while (heroIndex < heroes.size()
                && !heroes.get(heroIndex).isAlive()) {
            heroIndex++;
        }

        if (heroIndex >= heroes.size()) {
            enemiesTurn = true;
        }
    }

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

        int previousLevelIndex =
                gameState.getCurrentLevelIndex();

        if (!gameState.advanceEncounter()) {
            throw new IllegalStateException(
                    "Impossibile avanzare dopo l'incontro vinto."
            );
        }

        if (gameState.getCurrentLevelIndex()
                != previousLevelIndex) {
            prepareCurrentLevel(events);
        }

        prepareCurrentEncounter(events);
        beginRound();

        return true;
    }

    private boolean hasAvailableAction(Hero hero) {
        for (Item item : hero.getInventory().getItems()) {
            if (item instanceof Potion
                    && hero.getHealth() < hero.getMaxHealth()) {
                return true;
            }

            if (item instanceof Weapon weapon) {
                for (Enemy enemy
                        : gameState.getCurrentEncounter().getEnemies()) {
                    if (enemy.isAlive()
                            && weapon.getMultiplierAgainst(enemy) > 0.0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void prepareCurrentLevel(List<CombatEvent> events) {
        LevelNumber number =
                gameState.getCurrentLevel().getNumber();

        Party party = gameState.getCurrentParty();

        if (party == null) {
            throw new IllegalStateException(
                    "Non è disponibile un gruppo "
                            + "per il livello corrente."
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

    private void prepareCurrentEncounter(
            List<CombatEvent> events
    ) {
        Party party = gameState.getCurrentParty();

        levelPreparation.prepareEncounter(party);

        int encounterNumber =
                gameState.getCurrentEncounterIndex() + 1;

        events.add(
                new CombatEvent(
                        "Inizio incontro " + encounterNumber
                                + ": ogni eroe riceve una pozione. "
                                + "Le pozioni non usate "
                                + "non si accumulano."
                )
        );
    }

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
