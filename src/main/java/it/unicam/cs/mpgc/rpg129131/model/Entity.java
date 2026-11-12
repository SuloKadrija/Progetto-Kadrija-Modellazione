package it.unicam.cs.mpgc.rpg129131.model;

/**
 * Rappresenta le statistiche e la salute comuni a eroi e nemici.
 * La salute massima viene fissata alla costruzione; la salute corrente può variare
 * tra zero e il massimo. Non gestisce turni, inventari o interfacce utente.
 */
public abstract class Entity {

    /**
     * Nome assegnato all’entità.
     */
    private final String name;
    /**
     * Salute corrente: zero indica un’entità sconfitta.
     */
    private int health;
    /**
     * Statistica base di attacco.
     */
    private final int attack;
    /**
     * Statistica base di difesa.
     */
    private final int defense;
    /**
     * Salute massima, uguale alla salute iniziale.
     */
    private final int maxHealth;

    /**
     * Inizializza statistiche valide e imposta la salute corrente e massima allo stesso valore.
     *
     * @param name nome non nullo e non vuoto
     * @param health salute iniziale e massima, strettamente positiva
     * @param attack attacco non negativo
     * @param defense difesa non negativa
     * @throws IllegalArgumentException se una statistica o il nome non è valido
     */
    protected Entity(String name, int health, int attack, int defense) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Il nome non può essere nullo o vuoto."
            );
        }

        if (health <= 0) {
            throw new IllegalArgumentException("La vita iniziale deve essere maggiore di zero.");
        }

        if (attack < 0) {
            throw new IllegalArgumentException("L'attacco non può essere negativo.");
        }

        if (defense < 0) {
            throw new IllegalArgumentException("La difesa non può essere negativa.");
        }

        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.maxHealth = health;
    }

    /**
     * Restituisce il nome dell’entità.
     *
     * @return nome assegnato alla costruzione
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce la salute corrente, che può essere zero.
     *
     * @return punti salute rimasti
     */
    public int getHealth() {
        return health;
    }

    /**
     * Ripristina la salute corrente letta da un salvataggio, senza modificare il massimo.
     *
     * @param savedHealth salute da ripristinare, compresa tra zero e il massimo
     * @throws IllegalArgumentException se il valore è fuori intervallo
     */
    public void restoreHealth(int savedHealth) {
        if (savedHealth < 0 || savedHealth > maxHealth) {
            throw new IllegalArgumentException("La salute salvata deve essere compresa tra 0 e " + maxHealth + ".");
        }

        this.health = savedHealth;
    }

    /**
     * Restituisce il limite massimo della salute.
     *
     * @return salute massima fissata alla costruzione
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Restituisce la statistica di attacco.
     *
     * @return valore di attacco
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Restituisce la statistica di difesa.
     *
     * @return valore di difesa
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Verifica se l’entità dispone ancora di punti salute.
     *
     * @return true se la salute corrente è maggiore di zero
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Sottrae il danno ricevuto senza portare la salute sotto zero.
     * Il valore ricevuto è già il danno da applicare: qui non viene sottratta la difesa.
     *
     * @param damage danno non negativo da applicare
     * @throws IllegalArgumentException se il danno è negativo
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo.");
        }

        health = Math.max(0, health - damage);
    }

    /**
     * Riporta la salute corrente al massimo.
     * Questo metodo non verifica se l’entità sia viva; le regole delle pozioni
     * sono controllate dalla classe Potion.
     */
    public void healToMax() {
        health = maxHealth;
    }
}
