package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoundManager {

    private final CombatService combatService;
    private final Random random;

    public RoundManager(CombatService combatService) {
        this(combatService, new Random());
    }

    public RoundManager(CombatService combatService, Random random) {
        if (combatService == null) {
            throw new IllegalArgumentException("Il servizio di combattimento non può essere null.");
        }

        if (random == null) {
            throw new IllegalArgumentException("Il generatore casuale non può essere null.");
        }

        this.combatService = combatService;
        this.random = random;
    }

    public List<CombatEvent> playRound(Encounter encounter, List<AttackAction> heroActions) {
        validateRound(encounter, heroActions);

        List<CombatEvent> events = new ArrayList<>();

        playHeroesTurn(heroActions, events);

        if (!encounter.isCompleted()) {
            playEnemiesTurn(encounter, events);
        }

        return events;
    }

    private void playHeroesTurn(List<AttackAction> heroActions, List<CombatEvent> events) {
        for (AttackAction action : heroActions) {
            Hero hero = action.hero();

            if (!hero.isAlive()) {
                events.add(new CombatEvent(hero.getName() + " è sconfitto e non può attaccare."));
                continue;
            }

            Enemy target = action.target();

            if (!target.isAlive()) {
                events.add(new CombatEvent(target.getName() + " è già sconfitto."));
                continue;
            }

            try {
                int damage = combatService.attack(
                        action.hero(),
                        action.weapon(),
                        action.target()
                );

                events.add(new CombatEvent(
                        hero.getName() + " attacca " + target.getName() + " con " + action.weapon().getName() + " e infligge " + damage + " danni."));
            } catch (IllegalArgumentException | IllegalStateException e) {
                events.add(new CombatEvent(e.getMessage()));
            }
        }
    }

    private void playEnemiesTurn(Encounter encounter, List<CombatEvent> events) {
        Party party = encounter.getParty();

        for (Enemy enemy : encounter.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }

            List<Hero> availableTargets = party.getMembers().stream()
                    .filter(Hero::isAlive)
                    .toList();

            if (availableTargets.isEmpty()) {
                return;
            }

            Hero target = availableTargets.get(random.nextInt(availableTargets.size()));

            int damage = Math.max(1, enemy.getAttack() - target.getDefense());
            target.takeDamage(damage);

            events.add(new CombatEvent(
                    enemy.getName() + " attacca " + target.getName() + " e infligge " + damage + " danni."));
        }
    }

    private void validateRound(Encounter encounter, List<AttackAction> heroActions) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (heroActions == null) {
            throw new IllegalArgumentException("Le azioni degli eroi non possono essere null.");
        }
    }
}