package it.unicam.cs.mpgc.rpg129131;

public class CombatController {

    private final CombatService combatService;

    public CombatController() {
        this.combatService = new CombatService();
    }

    public int performAttack(Hero hero, Weapon weapon, Enemy enemy, Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        if (!encounter.getParty().contains(hero)) {
            throw new IllegalArgumentException(
                    "Questo eroe non appartiene al gruppo dell'incontro."
            );
        }

        if (!encounter.getEnemies().contains(enemy)) {
            throw new IllegalArgumentException(
                    "Questo nemico non appartiene all'incontro corrente."
            );
        }

        int damage = combatService.attack(hero, weapon, enemy);

        if (!encounter.isCompleted() && encounter.getParty().hasLivingMembers()) {
            enemyTurn(encounter);
        }

        return damage;
    }

    public void enemyTurn(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("L'incontro non può essere null.");
        }

        Party party = encounter.getParty();

        if (!party.hasLivingMembers() || encounter.isCompleted()) {
            return;
        }

        for (Enemy enemy : encounter.getEnemies()) {
            if (!enemy.isAlive()) { continue;
            }

            Hero target = null;

            for (Hero hero : party.getMembers()) {
                if (hero.isAlive()) {
                    target = hero; break;
                }
            }

            if (target == null) {
                return;
            }

            int damage = Math.max(1, enemy.getAttack() - target.getDefense());
            target.takeDamage(damage);
        }
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

    public boolean advanceAfterVictory(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("Lo stato del gioco non può essere null.");
        }

        if (gameState.isGameOver()) {
            return false;
        }

        Encounter encounter = gameState.getCurrentEncounter();

        if (encounter == null || !encounter.isCompleted()) {
            return false;
        }

        return gameState.advanceEncounter();
    }
}
