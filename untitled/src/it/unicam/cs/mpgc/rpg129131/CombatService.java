package it.unicam.cs.mpgc.rpg129131;

/**
 * Calcola e applica i danni degli attacchi degli eroi.
 * Tiene separata la formula del danno dai turni, dagli eventi testuali
 * e dall’interfaccia utente.
 */
public class CombatService {

    /**
     * Calcola il danno teorico senza modificare la salute.
     * Moltiplica attacco, fattore base ed efficacia; arrotonda prima di
     * sottrarre la difesa. Con efficacia zero il danno è zero, altrimenti
     * è almeno uno e non supera Integer.MAX_VALUE.
     * Non verifica possesso dell’arma o stato vitale dei partecipanti.
     *
     * @param hero eroe dal quale leggere l’attacco
     * @param weapon arma da valutare
     * @param enemy nemico del quale leggere tipo e difesa
     * @return danno calcolato, non limitato alla salute residua del nemico
     * @throws IllegalArgumentException se un parametro è null
     */
    public int calculateDamage(Hero hero, Weapon weapon, Enemy enemy) {
        validateParameters(hero, weapon, enemy);

        double effectiveness = weapon.getMultiplierAgainst(enemy);

        // L’inefficacia completa prevale sulla regola del danno minimo.
        if (effectiveness == 0.0) {
            return 0;
        }

        double baseDamage = hero.getAttack() * weapon.getBaseMultiplier();

        double totalDamage = baseDamage * effectiveness;
        // Arrotondamento al più vicino intero prima di applicare la difesa.
        double roundedDamage = Math.floor(totalDamage + 0.5);

        double damageAfterDefense = roundedDamage - enemy.getDefense();

        if (damageAfterDefense <= 1.0) {
            return 1;
        }

        if (damageAfterDefense >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) damageAfterDefense;
    }

    /**
     * Valida ed esegue l’attacco, riducendo la salute del nemico.
     * Restituisce la perdita effettiva di salute, non il solo danno teorico.
     *
     * @param hero eroe attaccante
     * @param weapon arma posseduta dall’eroe
     * @param enemy nemico vivo da attaccare
     * @return punti salute effettivamente persi dal nemico
     * @throws IllegalArgumentException se un parametro è null
     * @throws IllegalStateException se un partecipante è sconfitto o l’arma non è posseduta
     */
    public int attack(Hero hero, Weapon weapon, Enemy enemy) {
        validateAttack(hero, weapon, enemy);

        int calculatedDamage = calculateDamage(hero, weapon, enemy);
        int healthBefore = enemy.getHealth();

        enemy.takeDamage(calculatedDamage);

        // Il danno effettivo non può superare la salute che il bersaglio aveva.
        return healthBefore - enemy.getHealth();
    }

    /**
     * Verifica che i tre riferimenti necessari al calcolo siano presenti.
     *
     * @param hero eroe da verificare
     * @param weapon arma da verificare
     * @param enemy nemico da verificare
     * @throws IllegalArgumentException se un riferimento è null
     */
    private void validateParameters(
            Hero hero,
            Weapon weapon,
            Enemy enemy
    ) {
        if (hero == null || weapon == null || enemy == null) {
            throw new IllegalArgumentException(
                    "Eroe, arma e nemico non possono essere null."
            );
        }
    }

    /**
     * Verifica riferimenti, stato vitale e possesso dell’arma.
     * L’appartenenza dei partecipanti all’incontro è controllata da RoundManager.
     *
     * @param hero eroe attaccante
     * @param weapon arma richiesta
     * @param enemy bersaglio dell’attacco
     * @throws IllegalArgumentException se un parametro è null
     * @throws IllegalStateException se l’attacco non può essere eseguito
     */
    private void validateAttack(Hero hero, Weapon weapon, Enemy enemy) {
        validateParameters(hero, weapon, enemy);

        if (!hero.isAlive()) {
            throw new IllegalStateException("Un eroe sconfitto non può attaccare.");
        }

        if (!enemy.isAlive()) {
            throw new IllegalStateException("Un nemico sconfitto non può essere attaccato.");
        }

        if (!hero.getInventory().contains(weapon)) {
            throw new IllegalStateException("L'eroe non possiede quest'arma.");
        }
    }
}
