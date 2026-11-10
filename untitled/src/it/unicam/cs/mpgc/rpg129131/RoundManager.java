package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RoundManager {

    private final CombatService combatService;
    private final Random random;

    public RoundManager(CombatService combatService) {
        this(combatService, new Random());
    }

    public RoundManager(CombatService combatService, Random random) {
        if (combatService == null) {
            throw new IllegalArgumentException(
                    "Il servizio di combattimento non può essere null."
            );
        }

        if (random == null) {
            throw new IllegalArgumentException(
                    "Il generatore casuale non può essere null."
            );
        }

        this.combatService = combatService;
        this.random = random;
    }

    public List<CombatEvent> playRound(
            Encounter encounter,
            List<HeroAction> heroActions
    ) {
        validateRound(encounter, heroActions);

        List<CombatEvent> events = new ArrayList<>();

        playHeroesTurn(heroActions, events);

        if (!encounter.isCompleted()) {
            playEnemiesTurn(encounter, events);
        }

        return events;
    }

    private void playHeroesTurn(
            List<HeroAction> heroActions,
            List<CombatEvent> events
    ) {
        for (HeroAction action : heroActions) {
            if (action instanceof AttackAction attackAction) {
                playAttackAction(attackAction, events);
            } else if (action instanceof PotionAction potionAction) {
                playPotionAction(potionAction, events);
            }
        }
    }

    private void playAttackAction(
            AttackAction action,
            List<CombatEvent> events
    ) {
        Hero hero = action.hero();
        Enemy target = action.target();

        if (!hero.isAlive()) {
            events.add(new CombatEvent(
                    hero.getName() + " è sconfitto e non può attaccare."
            ));
            return;
        }

        if (!target.isAlive()) {
            events.add(new CombatEvent(
                    target.getName() + " è già sconfitto."
            ));
            return;
        }

        try {
            int damage = combatService.attack(
                    hero,
                    action.weapon(),
                    target
            );

            events.add(new CombatEvent(
                    hero.getName()
                            + " attacca "
                            + target.getName()
                            + " con "
                            + action.weapon().getName()
                            + " e infligge "
                            + damage
                            + " danni."
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            events.add(new CombatEvent(e.getMessage()));
        }
    }

    private void playPotionAction(
            PotionAction action,
            List<CombatEvent> events
    ) {
        Hero hero = action.hero();
        Potion potion = action.potion();

        if (!hero.isAlive()) {
            events.add(new CombatEvent(
                    hero.getName()
                            + " è sconfitto e non può usare la pozione."
            ));
            return;
        }

        if (!hero.getInventory().contains(potion)) {
            events.add(new CombatEvent(
                    hero.getName() + " non possiede più questa pozione."
            ));
            return;
        }

        int healthBefore = hero.getHealth();

        try {
            potion.use(hero);
            hero.getInventory().removeItem(potion);

            int recoveredHealth = hero.getHealth() - healthBefore;

            events.add(new CombatEvent(
                    hero.getName()
                            + " usa una pozione e recupera "
                            + recoveredHealth
                            + " punti vita. HP: "
                            + hero.getHealth()
                            + "/"
                            + hero.getMaxHealth()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            events.add(new CombatEvent(e.getMessage()));
        }
    }

    private void playEnemiesTurn(
            Encounter encounter,
            List<CombatEvent> events
    ) {
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

            Hero target = availableTargets.get(
                    random.nextInt(availableTargets.size())
            );

            int damage = Math.max(
                    1,
                    enemy.getAttack() - target.getDefense()
            );

            target.takeDamage(damage);

            events.add(new CombatEvent(
                    enemy.getName()
                            + " attacca "
                            + target.getName()
                            + " e infligge "
                            + damage
                            + " danni."
            ));
        }
    }

    private void validateRound(
            Encounter encounter,
            List<HeroAction> heroActions
    ) {
        if (encounter == null) {
            throw new IllegalArgumentException(
                    "L'incontro non può essere null."
            );
        }

        if (heroActions == null) {
            throw new IllegalArgumentException(
                    "Le azioni degli eroi non possono essere null."
            );
        }

        Set<Hero> heroesWithAction = new HashSet<>();

        for (HeroAction action : heroActions) {
            if (action == null) {
                throw new IllegalArgumentException(
                        "Le azioni degli eroi non possono contenere null."
                );
            }

            Hero hero;

            if (action instanceof AttackAction attackAction) {
                hero = attackAction.hero();

                if (!encounter.getEnemies().contains(attackAction.target())) {
                    throw new IllegalArgumentException(
                            "Il bersaglio non appartiene all'incontro."
                    );
                }
            } else if (action instanceof PotionAction potionAction) {
                hero = potionAction.hero();
            } else {
                throw new IllegalArgumentException(
                        "Tipo di azione non supportato."
                );
            }

            if (hero == null) {
                throw new IllegalArgumentException(
                        "L'eroe dell'azione non può essere null."
                );
            }

            if (!encounter.getParty().getMembers().contains(hero)) {
                throw new IllegalArgumentException(
                        "L'eroe dell'azione non appartiene al party."
                );
            }

            if (!heroesWithAction.add(hero)) {
                throw new IllegalArgumentException(
                        hero.getName()
                                + " può eseguire una sola azione per round."
                );
            }
        }
    }
}