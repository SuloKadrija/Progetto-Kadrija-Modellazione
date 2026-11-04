package it.unicam.cs.mpgc.rpg129131;

import java.util.Map;

public class Weapon extends Item {

    private final String name;
    private final double baseMultiplier;
    private final Map<EnemyType, Double> effectiveness;

    public Weapon(String name, double baseMultiplier, Map<EnemyType, Double> effectiveness) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'arma non può essere nullo o vuoto.");
        }

        if (baseMultiplier <= 0) {
            throw new IllegalArgumentException("Il moltiplicatore base deve essere positivo.");
        }

        if (effectiveness == null) {
            throw new IllegalArgumentException("La mappa di efficacia non può essere null.");
        }

        this.name = name;
        this.baseMultiplier = baseMultiplier;
        this.effectiveness = Map.copyOf(effectiveness);
    }

    public String getName() {
        return name;
    }

    public double getBaseMultiplier() {
        return baseMultiplier;
    }

    public double getMultiplierAgainst(Enemy enemy) {
        return effectiveness.getOrDefault(enemy.getType(), 1.0);
    }
}