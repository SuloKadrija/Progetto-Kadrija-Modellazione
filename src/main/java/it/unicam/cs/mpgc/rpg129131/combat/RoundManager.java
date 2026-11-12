package it.unicam.cs.mpgc.rpg129131.combat;

import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Esegue una singola azione di un eroe o di un nemico e produce gli eventi.
 * Non decide quale personaggio debba agire: l’ordine del round e la
 * progressione degli incontri sono responsabilità di GameSession.
 */
public class RoundManager {

    /**
     * Servizio che calcola e applica gli attacchi degli eroi.
     */
    private final CombatService combatService;
    /**
     * Generatore usato per selezionare un eroe vivo come bersaglio.
     */
    private final Random random;

    /**
     * Crea il gestore usando un nuovo generatore casuale.
     *
     * @param combatService servizio per gli attacchi degli eroi
     * @throws IllegalArgumentException se il servizio è null
     */
    public RoundManager(CombatService combatService) {
        this(combatService, new Random());
    }

    /**
     * Crea il gestore con dipendenze esplicite.
     * Fornire il generatore permette ai test di controllare la casualità.
     *
     * @param combatService servizio per gli attacchi degli eroi
     * @param random generatore per scegliere i bersagli nemici
     * @throws IllegalArgumentException se una dipendenza è null
     */
    public RoundManager(CombatService combatService, Random random) {
        if (combatService == null || random == null) {
            throw new IllegalArgumentException("Il servizio di combattimento e il generatore casuale " + "non possono essere null.");
        }

        this.combatService = combatService;
        this.random = random;
    }

    /**
     * Valida l’incontro e l’appartenenza dell’eroe, poi interpreta l’azione.
     * Per un attacco verifica anche che il bersaglio appartenga all’incontro.
     * Alcuni rifiuti operativi vengono restituiti come eventi, non come eccezioni;
     * questo metodo non avanza né conserva l’indice del turno.
     *
     * @param encounter incontro corrente
     * @param action attacco o uso di pozione richiesto
     * @return eventi prodotti, compresi eventuali messaggi di rifiuto
     * @throws IllegalArgumentException se l’azione o i partecipanti non sono validi
     * @throws IllegalStateException se l’incontro è terminato
     */
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

    /**
     * Esegue un attacco del nemico scegliendo casualmente fra gli eroi vivi.
     * Il danno è almeno uno dopo la sottrazione della difesa.
     * Se il nemico è già sconfitto non produce eventi.
     *
     * @param encounter incontro corrente
     * @param enemy nemico appartenente all’incontro
     * @return eventi con danno effettivo ed eventuale sconfitta del bersaglio
     * @throws IllegalArgumentException se incontro o nemico non sono validi
     * @throws IllegalStateException se l’incontro è terminato
     */
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

    /**
     * Esegue l’attacco o aggiunge un messaggio che ne spiega il rifiuto.
     * Le eccezioni di validazione del servizio vengono convertite in eventi.
     *
     * @param action richiesta di attacco
     * @param events lista nella quale aggiungere gli eventi
     */
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

    /**
     * Controlla salute e possesso della pozione, quindi prova a curare l’eroe.
     * Rimuove la pozione soltanto dopo una cura riuscita; i rifiuti
     * vengono aggiunti alla lista degli eventi.
     *
     * @param action richiesta di cura
     * @param events lista nella quale aggiungere gli eventi
     */
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
            // Consuma l’oggetto soltanto se potion.use non ha sollevato eccezioni.
            hero.getInventory().removeItem(potion);

            events.add(new CombatEvent(
                    hero.getName() + " usa una pozione e recupera " + (hero.getHealth() - healthBefore)
                            + " punti vita. HP: " + hero.getHealth() + "/" + hero.getMaxHealth()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            events.add(new CombatEvent(e.getMessage()));
        }
    }

    /**
     * Richiede un incontro esistente con almeno un eroe e un nemico vivi.
     *
     * @param encounter incontro da verificare
     * @throws IllegalArgumentException se l’incontro è null
     * @throws IllegalStateException se il combattimento è terminato
     */
    private void validateEncounter(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (encounter.isCompleted() || !encounter.getParty().hasLivingMembers()) {
            throw new IllegalStateException("Il combattimento è terminato.");
        }
    }

    /**
     * Verifica che l’eroe appartenga al gruppo dell’incontro.
     * Non verifica qui la sua salute o il suo diritto ad agire in questo turno.
     *
     * @param encounter incontro già verificato
     * @param hero eroe richiesto dall’azione
     * @throws IllegalArgumentException se l’eroe è null o estraneo al gruppo
     */
    private void validateHero(Encounter encounter, Hero hero) {
        if (hero == null || !encounter.getParty().getMembers().contains(hero)) {
            throw new IllegalArgumentException("L'eroe deve appartenere al party dell'incontro.");
        }
    }
}
