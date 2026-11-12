package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un gruppo ordinato di eroi.
 * Non permette di aggiungere due volte lo stesso eroe e consente
 * di verificare se almeno un membro è ancora vivo.
 */
public class Party {
    /**
     * Membri del gruppo nell’ordine usato per i turni.
     */
    private final List<Hero> members;

    /**
     * Crea un gruppo inizialmente vuoto.
     */
    public Party() {
        this.members = new ArrayList<>();
    }

    /**
     * Aggiunge un eroe in fondo al gruppo, senza duplicare un membro già presente.
     *
     * @param hero eroe da aggiungere
     * @throws IllegalArgumentException se l’eroe è null o è già presente
     */
    public void addMember(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Il personaggio non può essere null.");
        }

        if (members.contains(hero)) {
            throw new IllegalArgumentException("Il personaggio è già presente nel gruppo.");
        }

        members.add(hero);
    }

    /**
     * Rimuove dal gruppo l’eroe indicato, se presente.
     *
     * @param hero eroe da rimuovere
     * @return true se l’eroe è stato rimosso
     */
    public boolean removeMember(Hero hero) {
        return members.remove(hero);
    }

    /**
     * Restituisce una vista non modificabile dei membri nell’ordine del gruppo.
     * Gli eroi contenuti non vengono copiati.
     *
     * @return vista dei membri
     */
    public List<Hero> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Verifica l’appartenenza di un eroe al gruppo.
     *
     * @param hero eroe cercato
     * @return true se l’eroe è presente
     */
    public boolean contains(Hero hero) {
        return members.contains(hero);
    }

    /**
     * Verifica se almeno un membro è vivo; un gruppo vuoto restituisce false.
     *
     * @return true se esiste un eroe con salute maggiore di zero
     */
    public boolean hasLivingMembers() {
        for (Hero hero : members) {
            if (hero.isAlive()) {
                return true;
            }
        }

        return false;
    }
}
