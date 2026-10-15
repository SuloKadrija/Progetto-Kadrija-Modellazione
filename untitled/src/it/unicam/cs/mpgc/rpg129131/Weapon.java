package it.unicam.cs.mpgc.rpg129131;

public abstract class Weapon implements Item {

    private final String name;
    private final double multiplier;

    protected Weapon(String name, double multiplier) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'arma non può essere vuoto");
        }

        if (!Double.isFinite(multiplier) || multiplier <= 0) {
            throw new IllegalArgumentException("Il moltiplicatore deve essere positivo e finito");
        }

        this.name = name;
        this.multiplier = multiplier;
    }

    @Override
    public String getName() {
        return name;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
