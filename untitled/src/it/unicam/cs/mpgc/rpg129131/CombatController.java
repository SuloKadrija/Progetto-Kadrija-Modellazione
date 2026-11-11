package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public class CombatController {

    private final RoundManager roundManager;

    public CombatController(RoundManager roundManager) {
        if (roundManager == null) {
            throw new IllegalArgumentException("Il gestore dei round non può essere null.");
        }

        this.roundManager = roundManager;
    }

    public List<CombatEvent> executeHeroAction(Encounter encounter, HeroAction action) {
        return roundManager.executeHeroAction(encounter, action);
    }

    public List<CombatEvent> executeEnemyAction(Encounter encounter, Enemy enemy) {
        return roundManager.executeEnemyAction(encounter, enemy);
    }

    public String getCombatResult(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (!encounter.getParty().hasLivingMembers()) {
            return "SCONFITTA: tutti gli eroi sono stati sconfitti.";
        }

        if (encounter.isCompleted()) {
            return "VITTORIA: tutti i nemici sono stati sconfitti.";
        }

        return "COMBATTIMENTO IN CORSO";
    }
}