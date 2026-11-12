package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.model.Inventory;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.item.*;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;

import java.util.ArrayList;
import java.util.List;

/**
 * Prepara sblocchi e dotazioni all’inizio di un livello e le pozioni
 * all’inizio di ciascun incontro. Non gestisce turni, danni o persistenza.
 * Il caricamento di un salvataggio non deve ripetere questa preparazione.
 */
public class LevelPreparation {

    /**
     * Sblocca gli eroi del gruppo e sostituisce gli inventari con le armi del livello.
     * Non assegna le pozioni: queste sono preparate separatamente per ogni incontro.
     *
     * @param number livello del quale preparare le dotazioni
     * @param party gruppo che entra nel livello
     * @throws IllegalArgumentException se livello o gruppo è null
     */
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

    /**
     * Rimuove tutte le pozioni residue e ne assegna una nuova a ciascun eroe.
     * Le armi restano invariate; le pozioni non usate non si accumulano.
     *
     * @param party gruppo che entra nell’incontro
     * @throws IllegalArgumentException se il gruppo è null
     */
    public void prepareEncounter(Party party) {
        if (party == null) {
            throw new IllegalArgumentException(
                    "Il gruppo non può essere null."
            );
        }

        for (Hero hero : party.getMembers()) {
            Inventory inventory = hero.getInventory();

            // Itera su una copia per poter rimuovere elementi dall’inventario originale.
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

    /**
     * Svuota l’inventario e aggiunge nuove istanze delle armi previste per il livello.
     *
     * @param hero eroe da equipaggiare
     * @param number livello che determina la dotazione
     */
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

    /**
     * Costruisce una nuova dotazione di armi per il livello indicato.
     * Ogni chiamata crea oggetti distinti per evitare di condividere la dotazione.
     *
     * @param number livello richiesto
     * @return lista delle armi previste
     * @throws IllegalArgumentException se non è definita una dotazione
     */
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
