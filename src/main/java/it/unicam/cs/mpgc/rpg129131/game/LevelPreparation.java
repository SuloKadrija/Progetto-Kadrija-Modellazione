package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.model.Inventory;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.hero.Hero;
import it.unicam.cs.mpgc.rpg129131.model.item.AngelicArrow;
import it.unicam.cs.mpgc.rpg129131.model.item.AngelicKnife;
import it.unicam.cs.mpgc.rpg129131.model.item.AngelicSword;
import it.unicam.cs.mpgc.rpg129131.model.item.DemoniacArrow;
import it.unicam.cs.mpgc.rpg129131.model.item.DemoniacKnife;
import it.unicam.cs.mpgc.rpg129131.model.item.FireWeapon;
import it.unicam.cs.mpgc.rpg129131.model.item.HolyWater;
import it.unicam.cs.mpgc.rpg129131.model.item.Item;
import it.unicam.cs.mpgc.rpg129131.model.item.Knife;
import it.unicam.cs.mpgc.rpg129131.model.item.Potion;
import it.unicam.cs.mpgc.rpg129131.model.item.Quote;
import it.unicam.cs.mpgc.rpg129131.model.item.Rifle;
import it.unicam.cs.mpgc.rpg129131.model.item.Salt;
import it.unicam.cs.mpgc.rpg129131.model.item.Weapon;
import it.unicam.cs.mpgc.rpg129131.model.item.WoodenStake;

import java.util.ArrayList;
import java.util.List;

/**
 * Prepara sblocchi e dotazioni all'inizio di un livello e assegna
 * le pozioni all'inizio di ciascun incontro.
 * Non gestisce turni, danni o persistenza.
 * Il caricamento di una partita non deve ripetere questa preparazione.
 */
public class LevelPreparation {

    /**
     * Sblocca gli eroi del gruppo e sostituisce gli inventari con le armi del livello.
     * Crea una dotazione distinta per ciascun eroe.
     * Le pozioni vengono assegnate separatamente all'inizio dell'incontro.
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
            replaceInventory(hero, number);
            hero.unlock();
        }
    }

    /**
     * Sostituisce tutte le pozioni residue con una nuova pozione per eroe.
     * Conserva gli altri oggetti, mantenendone ordine e identità.
     *
     * @param party gruppo che entra nell'incontro
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
            List<Item> preparedItems = new ArrayList<>();

            for (Item item : inventory.getItems()) {
                if (!(item instanceof Potion)) {
                    preparedItems.add(item);
                }
            }

            preparedItems.add(new Potion());
            inventory.restoreItems(preparedItems);
        }
    }

    /**
     * Sostituisce l'inventario con nuove istanze delle armi previste.
     * Costruisce la dotazione prima di modificare l'inventario dell'eroe.
     *
     * @param hero eroe da equipaggiare
     * @param number livello che determina la dotazione
     */
    private void replaceInventory(Hero hero, LevelNumber number) {
        List<Item> preparedItems = new ArrayList<>(createWeapons(number));
        hero.getInventory().restoreItems(preparedItems);
    }

    /**
     * Costruisce una nuova dotazione di armi per il livello indicato.
     * Ogni chiamata crea oggetti distinti, evitando la condivisione
     * della dotazione fra eroi.
     *
     * @param number livello richiesto
     * @return armi previste per il livello
     */
    private List<Weapon> createWeapons(LevelNumber number) {
        return switch (number) {
            case ONE -> List.of(
                    new Knife(),
                    new Rifle(),
                    new WoodenStake(),
                    new Salt(),
                    new FireWeapon()
            );

            case TWO -> List.of(
                    new HolyWater(),
                    new DemoniacKnife(),
                    new DemoniacArrow(),
                    new AngelicSword()
            );

            case THREE -> List.of(
                    new AngelicSword(),
                    new AngelicArrow(),
                    new AngelicKnife(),
                    new Quote()
            );
        };
    }
}