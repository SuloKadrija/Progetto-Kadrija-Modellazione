package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

/**
 * Descrive un’arma mediante nome, moltiplicatore base e moltiplicatori
 * di efficacia per tipo di nemico. Non applica direttamente danni:
 * il calcolo e l’esecuzione dell’attacco spettano al servizio di combattimento.
 */
public class Weapon implements Item {

    /**
     * Nome visualizzato dell’arma.
     */
    private final String name;
    /**
     * Moltiplicatore base applicato all’attacco dell’eroe.
     */
    private final double baseMultiplier;
    /**
     * Moltiplicatori specifici per categoria di nemico.
     */
    private final Map<EnemyType, Double> effectiveness;

    /**
     * Crea un’arma e conserva una copia non modificabile della mappa di efficacia.
     *
     * @param name nome visualizzato dell’arma
     * @param baseMultiplier moltiplicatore base finito e positivo
     * @param effectiveness efficacia per tipo di nemico, con valori finiti e non negativi
     * @throws IllegalArgumentException se nome, moltiplicatori o mappa non sono validi
     */
    public Weapon(String name, double baseMultiplier, Map<EnemyType, Double> effectiveness) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'arma non può essere nullo o vuoto.");
        }

        if (!Double.isFinite(baseMultiplier) || baseMultiplier <= 0) {
            throw new IllegalArgumentException("Il moltiplicatore base deve essere finito e positivo.");
        }

        if (effectiveness == null) {
            throw new IllegalArgumentException("La mappa di efficacia non può essere null.");
        }

        Map<EnemyType, Double> effectivenessCopy = new java.util.HashMap<>(effectiveness);

        for (Map.Entry<EnemyType, Double> entry : effectivenessCopy.entrySet()) {

            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Il tipo di nemico nella mappa non può essere null.");
            }

            Double multiplier = entry.getValue();

            if (multiplier == null || !Double.isFinite(multiplier) || multiplier < 0) {

                throw new IllegalArgumentException("I moltiplicatori di efficacia devono essere " + "finiti e non negativi.");
            }
        }

        this.name = name;
        this.baseMultiplier = baseMultiplier;
        this.effectiveness = Map.copyOf(effectivenessCopy);
    }

    /**
     * Restituisce il nome visualizzato dell’arma.
     *
     * @return nome dell’arma
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Restituisce il moltiplicatore applicato alla statistica di attacco.
     *
     * @return moltiplicatore base
     */
    public double getBaseMultiplier() {
        return baseMultiplier;
    }

    /**
     * Restituisce l’efficacia contro il tipo del nemico.
     * Se il tipo non è nella mappa, restituisce 1.0; un valore zero
     * indica inefficacia contro quel tipo.
     *
     * @param enemy nemico da valutare
     * @return moltiplicatore di efficacia, distinto dal moltiplicatore base
     * @throws IllegalArgumentException se il nemico è null
     */
    public double getMultiplierAgainst(Enemy enemy) {
        if (enemy == null) {
            throw new IllegalArgumentException("Il nemico non può essere null.");
        }

        return effectiveness.getOrDefault(enemy.getType(), 1.0);
    }
}
