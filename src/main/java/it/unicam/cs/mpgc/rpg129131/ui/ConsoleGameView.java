package it.unicam.cs.mpgc.rpg129131.ui;

import it.unicam.cs.mpgc.rpg129131.combat.AttackAction;
import it.unicam.cs.mpgc.rpg129131.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg129131.combat.HeroAction;
import it.unicam.cs.mpgc.rpg129131.combat.PotionAction;
import it.unicam.cs.mpgc.rpg129131.game.GameView;
import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.item.Item;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.combat.ActionAvailability;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implementa input e output testuali del contratto GameView.
 * Mostra stato ed eventi e costruisce le richieste di azione dalle scelte
 * dell’utente, senza applicare direttamente danni o cure.
 * La modalità console non espone comandi di persistenza.
 */
public class ConsoleGameView implements GameView {

    /**
     * Scanner fornito dal chiamante per leggere le scelte dalla console.
     */
    private final Scanner scanner;

    /** Criteri condivisi per individuare bersagli e oggetti disponibili. */
    private final ActionAvailability actionAvailability = new ActionAvailability();

    /**
     * Collega la vista allo Scanner fornito. Il suo ciclo di vita è gestito dal chiamante.
     *
     * @param scanner sorgente delle scelte testuali
     * @throws IllegalArgumentException se lo Scanner è null
     */
    public ConsoleGameView(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException(
                    "Lo scanner non può essere null."
            );
        }

        this.scanner = scanner;
    }

    /**
     * Scrive un messaggio seguito da un ritorno a capo.
     *
     * @param message testo da visualizzare
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Mostra livello, numero dell’incontro, salute e sconfitte dei partecipanti.
     *
     * @param level identificatore del livello corrente
     * @param encounterNumber numero da mostrare, normalmente con numerazione da uno
     * @param encounter incontro da visualizzare
     */
    @Override
    public void showStatus(
            LevelNumber level,
            int encounterNumber,
            Encounter encounter
    ) {
        System.out.println(
                "\nLivello " + level
                        + " | Incontro " + encounterNumber
        );

        System.out.println("\n=== STATO DEL COMBATTIMENTO ===");
        System.out.println("\nEroi:");

        for (Hero hero : encounter.getParty().getMembers()) {
            System.out.println(
                    hero.getName()
                            + " - HP: "
                            + hero.getHealth()
                            + "/"
                            + hero.getMaxHealth()
                            + (hero.isAlive() ? "" : " (sconfitto)")
            );
        }

        System.out.println("\nNemici:");

        List<Enemy> enemies = encounter.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            System.out.println(
                    (i + 1)
                            + ") "
                            + enemy.getName()
                            + " - HP: "
                            + enemy.getHealth()
                            + "/"
                            + enemy.getMaxHealth()
                            + (enemy.isAlive() ? "" : " (sconfitto)")
            );
        }
    }

    /**
     * Mostra le azioni disponibili e legge le scelte necessarie per costruirne una.
     * Non esegue l’azione: la richiesta viene restituita al ciclo del gioco.
     *
     * @param hero eroe corrente
     * @param encounter incontro corrente
     * @return richiesta di attacco o cura, oppure null se non esistono azioni
     * @throws IllegalStateException se l’input termina durante una scelta
     */
    @Override
    public HeroAction readAction(Hero hero, Encounter encounter) {
        System.out.println("\nTurno di " + hero.getName());

        List<Enemy> targets = getAvailableTargets(hero, encounter);
        List<Potion> potions = getPotions(hero);

        boolean canAttack = !targets.isEmpty();
        boolean canHeal = !potions.isEmpty()
                && hero.getHealth() < hero.getMaxHealth();

        if (!canAttack && !canHeal) {
            return null;
        }

        if (canAttack && canHeal) {
            System.out.println("1) Attacca");
            System.out.println("2) Usa pozione");

            if (readChoice(2) == 2) {
                return new PotionAction(hero, potions.get(0));
            }
        } else if (canHeal) {
            System.out.println("1) Usa pozione");
            readChoice(1);

            return new PotionAction(hero, potions.get(0));
        } else {
            System.out.println(
                    "Attacco: la cura non è disponibile."
            );
        }

        Enemy target = chooseTarget(targets, encounter);

        List<Weapon> weapons = getUsableWeapons(hero, target);
        Weapon weapon = chooseWeapon(weapons);

        return new AttackAction(hero, weapon, target);
    }

    /**
     * Stampa in ordine i messaggi degli eventi; una lista vuota non produce output.
     *
     * @param events eventi da visualizzare
     */
    @Override
    public void showEvents(List<CombatEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        System.out.println("\n=== EVENTI ===");

        for (CombatEvent event : events) {
            System.out.println(event.message());
        }
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
     * Stampa i bersagli disponibili e legge la scelta.
     * Il numero della scelta è distinto dall’identificatore mostrato nell’incontro.
     *
     * @param targets bersagli selezionabili, lista non vuota
     * @param encounter incontro dal quale ricavare i numeri originali dei nemici
     * @return nemico selezionato
     * @throws IllegalStateException se l’input termina
     */
    private Enemy chooseTarget(
            List<Enemy> targets,
            Encounter encounter
    ) {
        System.out.println("Scegli il nemico:");

        for (int i = 0; i < targets.size(); i++) {
            Enemy enemy = targets.get(i);

            int enemyNumber =
                    encounter.getEnemies().indexOf(enemy) + 1;

            System.out.println(
                    (i + 1)
                            + ") "
                            + enemy.getName()
                            + " #"
                            + enemyNumber
                            + " - HP: "
                            + enemy.getHealth()
                            + "/"
                            + enemy.getMaxHealth()
            );
        }

        return targets.get(readChoice(targets.size()) - 1);
    }

    /**
     * Stampa le armi disponibili e legge una scelta valida.
     *
     * @param weapons armi selezionabili, lista non vuota
     * @return arma selezionata
     * @throws IllegalStateException se l’input termina
     */
    private Weapon chooseWeapon(List<Weapon> weapons) {
        System.out.println("Scegli l'arma:");

        for (int i = 0; i < weapons.size(); i++) {
            System.out.println(
                    (i + 1) + ") " + weapons.get(i).getName()
            );
        }

        return weapons.get(readChoice(weapons.size()) - 1);
    }

    /**
     * Legge un intero tra uno e il massimo, ripetendo la richiesta in caso di errore.
     * Consuma i token non interi per evitare di rileggere indefinitamente lo stesso input.
     *
     * @param max massimo valore accettato, almeno uno
     * @return scelta valida nell’intervallo inclusivo 1..max
     * @throws IllegalArgumentException se max è minore di uno
     * @throws IllegalStateException se non esiste più input da leggere
     */
    private int readChoice(int max) {
        if (max < 1) {
            throw new IllegalArgumentException(
                    "Deve essere disponibile almeno una scelta."
            );
        }

        while (true) {
            System.out.print("> ");

            if (!scanner.hasNext()) {
                throw new IllegalStateException(
                        "Input terminato: impossibile leggere la scelta."
                );
            }

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();

                if (choice >= 1 && choice <= max) {
                    return choice;
                }
            } else {
                // Scarta il token non numerico prima di riproporre la scelta.
                scanner.next();
            }

            System.out.println(
                    "Inserisci un numero intero da 1 a " + max + "."
            );
        }
    }
}
