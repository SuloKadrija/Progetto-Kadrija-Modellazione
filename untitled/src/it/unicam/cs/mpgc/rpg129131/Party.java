package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party {
    private final List<Hero> members;

    public Party() {
        this.members = new ArrayList<>();
    }

    public void addMember(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Il personaggio non può essere null.");
        }

        if (members.contains(hero)) {
            throw new IllegalArgumentException("Il personaggio è già presente nel gruppo.");
        }

        members.add(hero);
    }

    public boolean removeMember(Hero hero) {
        return members.remove(hero);
    }

    public List<Hero> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public boolean contains(Hero hero) {
        return members.contains(hero);
    }

    public boolean hasLivingMembers() {
        for (Hero hero : members) {
            if (hero.isAlive()) {
                return true;
            }
        }

        return false;
    }
}
