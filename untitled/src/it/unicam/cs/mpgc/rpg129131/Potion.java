package it.unicam.cs.mpgc.rpg129131;

/**
 * Oggetto curativo che riporta al massimo la salute di un eroe vivo e ferito.
 * La cura non rimuove direttamente l’oggetto dall’inventario: il consumo
 * è gestito da RoundManager dopo l’uso riuscito.
 */
public class Potion implements Item {

    /**
     * Restituisce il nome visualizzato della pozione.
     *
     * @return stringa Pozione
     */
    @Override
    public String getName() {
        return "Pozione";
    }

    /**
     * Cura completamente un eroe vivo che non abbia già tutta la salute.
     * Non consuma direttamente la pozione e non avanza il turno.
     *
     * @param hero eroe da curare
     * @throws IllegalArgumentException se l’eroe è null
     * @throws IllegalStateException se l’eroe è sconfitto o ha già salute piena
     */
    public void use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException(
                    "L'eroe non può essere null."
            );
        }

        if (!hero.isAlive()) {
            throw new IllegalStateException(
                    "Un eroe sconfitto non può usare una pozione."
            );
        }

        if (hero.getHealth() >= hero.getMaxHealth()) {
            throw new IllegalStateException(
                    hero.getName() + " ha già tutta la vita."
            );
        }

        hero.healToMax();
    }
}
