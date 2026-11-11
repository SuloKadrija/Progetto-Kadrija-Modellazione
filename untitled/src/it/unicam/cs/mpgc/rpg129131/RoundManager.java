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
        if (combatService == null || random == null) {
            throw new IllegalArgumentException("Il servizio di combattimento e il generatore casuale " + "non possono essere null.");
        }

        this.combatService = combatService;
        this.random = random;
    }

    public List<CombatEvent> executeHeroAction(Encounter encounter, HeroAction action) {
        validateEncounter(encounter);

        if (action == null) {
            throw new IllegalArgumentException("L'azione non può essere null.");
        }

        List<CombatEvent> events = new ArrayList<>();

        if (action instanceof AttackAction attackAction) {
            validateHero(encounter, attackAction.hero());

            if (!encounter.getEnemies().contains(attackAction.target())) {
                throw new IllegalArgumentException("Il bersaglio non appartiene all'incontro.");
            }

            playAttackAction(attackAction, events);
        } else if (action instanceof PotionAction potionAction) {
            validateHero(encounter, potionAction.hero());
            playPotionAction(potionAction, events);
        } else {
            throw new IllegalArgumentException("Tipo di azione non supportato.");
        }

        return events;
    }

    public List<CombatEvent> executeEnemyAction(Encounter encounter, Enemy enemy) {
        validateEncounter(encounter);

        if (enemy == null || !encounter.getEnemies().contains(enemy)) {
            throw new IllegalArgumentException("Il nemico deve appartenere all'incontro.");
        }

        List<CombatEvent> events = new ArrayList<>();

        if (!enemy.isAlive() || encounter.isCompleted()) {
            return events;
        }

        List<Hero> targets = encounter.getParty().getMembers().stream().filter(Hero::isAlive).toList();

        if (targets.isEmpty()) {
            return events;
        }

        Hero target = targets.get(random.nextInt(targets.size()));

        int calculatedDamage = Math.max(1, enemy.getAttack() - target.getDefense());

        int healthBefore = target.getHealth();

        target.takeDamage(calculatedDamage);

        int actualDamage = healthBefore - target.getHealth();

        events.add(new CombatEvent(
                enemy.getName() + " attacca " + target.getName() + " e infligge " + actualDamage + " danni. HP: " + target.getHealth() + "/" + target.getMaxHealth()));

        if (!target.isAlive()) {
            events.add(new CombatEvent(target.getName() + " è stato sconfitto."));
        }

        return events;
    }

    private void playAttackAction(
            AttackAction action,
            List<CombatEvent> events
    ) {
        Hero hero = action.hero();
        Enemy target = action.target();

        if (!hero.isAlive()) {
            events.add(new CombatEvent(hero.getName() + " è sconfitto e non può attaccare."));
            return;
        }

        if (!target.isAlive()) {
            events.add(new CombatEvent(target.getName() + " è già sconfitto."));
            return;
        }

        try {
            int damage = combatService.attack(hero, action.weapon(), target);

            events.add(new CombatEvent(
                    hero.getName() + " attacca " + target.getName() + " con " + action.weapon().getName()
                            + " e infligge " + damage + " danni. HP: " + target.getHealth() + "/" + target.getMaxHealth()));

            if (!target.isAlive()) {
                events.add(new CombatEvent(target.getName() + " è stato sconfitto."));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            events.add(new CombatEvent(e.getMessage()));
        }
    }

    private void playPotionAction(PotionAction action, List<CombatEvent> events) {
        Hero hero = action.hero();
        Potion potion = action.potion();

        if (!hero.isAlive()) {
            events.add(new CombatEvent(hero.getName() + " è sconfitto e non può usare la pozione."));
            return;
        }

        if (!hero.getInventory().contains(potion)) {
            events.add(new CombatEvent(hero.getName() + " non possiede questa pozione."));
            return;
        }

        int healthBefore = hero.getHealth();

        try {
            potion.use(hero);
            hero.getInventory().removeItem(potion);

            events.add(new CombatEvent(
                    hero.getName() + " usa una pozione e recupera " + (hero.getHealth() - healthBefore)
                            + " punti vita. HP: " + hero.getHealth() + "/" + hero.getMaxHealth()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            events.add(new CombatEvent(e.getMessage()));
        }
    }

    private void validateEncounter(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (encounter.isCompleted() || !encounter.getParty().hasLivingMembers()) {
            throw new IllegalStateException("Il combattimento è terminato.");
        }
    }

    private void validateHero(Encounter encounter, Hero hero) {
        if (hero == null || !encounter.getParty().getMembers().contains(hero)) {
            throw new IllegalArgumentException("L'eroe deve appartenere al party dell'incontro.");
        }
    }
}