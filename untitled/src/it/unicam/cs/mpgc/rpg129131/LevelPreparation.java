package it.unicam.cs.mpgc.rpg129131;

import java.util.ArrayList;
import java.util.List;

public class LevelPreparation {

    public void prepare(LevelNumber number, Party party) {
        if (number == null || party == null) {
            throw new IllegalArgumentException(
                    "Livello e gruppo non possono essere null."
            );
        }

        for (Hero hero : party.getMembers()) {
            hero.unlock();
            replaceInventory(hero, number);
        }
    }

    public void prepareEncounter(Party party) {
        if (party == null) {
            throw new IllegalArgumentException(
                    "Il gruppo non può essere null."
            );
        }

        for (Hero hero : party.getMembers()) {
            Inventory inventory = hero.getInventory();

            List<Item> previousItems =
                    new ArrayList<>(inventory.getItems());

            for (Item item : previousItems) {
                if (item instanceof Potion) {
                    inventory.removeItem(item);
                }
            }

            inventory.addItem(new Potion());
        }
    }

    private void replaceInventory(
            Hero hero,
            LevelNumber number
    ) {
        Inventory inventory = hero.getInventory();

        List<Item> previousItems =
                new ArrayList<>(inventory.getItems());

        for (Item item : previousItems) {
            inventory.removeItem(item);
        }

        for (Weapon weapon : createWeapons(number)) {
            inventory.addItem(weapon);
        }
    }

    private List<Weapon> createWeapons(LevelNumber number) {
        switch (number) {
            case ONE:
                return List.of(
                        new Knife(),
                        new Rifle(),
                        new WoodenStake(),
                        new Salt(),
                        new FireWeapon()
                );

            case TWO:
                return List.of(
                        new HolyWater(),
                        new DemoniacKnife(),
                        new DemoniacArrow(),
                        new AngelicSword()
                );

            case THREE:
                return List.of(
                        new AngelicSword(),
                        new AngelicArrow(),
                        new AngelicKnife(),
                        new Quote()
                );

            default:
                throw new IllegalArgumentException(
                        "Dotazione non definita per il livello "
                                + number
                );
        }
    }
}
