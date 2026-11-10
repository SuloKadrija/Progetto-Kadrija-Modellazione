package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class Weapon implements Item {

    private final String name;
    private final double baseMultiplier;
    private final Map<EnemyType, Double> effectiveness;

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

    @Override
    public String getName() {
        return name;
    }

    public double getBaseMultiplier() {
        return baseMultiplier;
    }

    public double getMultiplierAgainst(Enemy enemy) {
        if (enemy == null) {
            throw new IllegalArgumentException("Il nemico non può essere null.");
        }

        return effectiveness.getOrDefault(enemy.getType(), 1.0);
    }
}