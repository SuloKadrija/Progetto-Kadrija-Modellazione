package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleGameView implements GameView {

    private final Scanner scanner;

    public ConsoleGameView(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException(
                    "Lo scanner non può essere null."
            );
        }

        this.scanner = scanner;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

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

    private Weapon chooseWeapon(List<Weapon> weapons) {
        System.out.println("Scegli l'arma:");

        for (int i = 0; i < weapons.size(); i++) {
            System.out.println(
                    (i + 1) + ") " + weapons.get(i).getName()
            );
        }

        return weapons.get(readChoice(weapons.size()) - 1);
    }

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
                scanner.next();
            }

            System.out.println(
                    "Inserisci un numero intero da 1 a " + max + "."
            );
        }
    }
}