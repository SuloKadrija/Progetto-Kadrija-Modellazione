package it.unicam.cs.mpgc.rpg129131.game;

import it.unicam.cs.mpgc.rpg129131.model.Encounter;
import it.unicam.cs.mpgc.rpg129131.model.Level;
import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.Party;
import it.unicam.cs.mpgc.rpg129131.model.enemy.*;
import it.unicam.cs.mpgc.rpg129131.model.hero.*;

import java.util.List;

/**
 * Costruisce una nuova struttura del gioco con eroi, gruppi e tre livelli.
 * Riutilizza le stesse istanze degli eroi nei gruppi in cui compaiono.
 * Non prepara gli inventari e non avvia la sessione; viene usata anche
 * come base per ricostruire una partita salvata.
 */
public class GameFactory {

    /**
     * Costruisce una nuova partita con cinque eroi e nove incontri.
     * Sam e Dean sono condivisi fra i gruppi; ogni nemico è una nuova istanza.
     * Le successive chiamate producono partite indipendenti.
     *
     * @return nuovo stato in posizione iniziale, non ancora preparato dalla sessione
     */
    public GameState create() {
        // Crea una sola istanza per eroe da riutilizzare nei diversi gruppi.
        Sam sam = new Sam();
        Dean dean = new Dean();
        Bobby bobby = new Bobby();
        Castiel castiel = new Castiel();
        Jack jack = new Jack();

        // Definisce composizione e ordine dei gruppi dei tre livelli.
        Party firstParty = createParty(sam, dean);
        Party secondParty = createParty(sam, dean, bobby);
        Party thirdParty = createParty(sam, dean, castiel, jack);

        // Primo livello: fantasma, vampiro e wendigo.
        Level firstLevel = new Level(
                LevelNumber.ONE,
                List.of(
                        new Encounter(
                                firstParty,
                                List.of(new Ghost())
                        ),
                        new Encounter(
                                firstParty,
                                List.of(new Vampire())
                        ),
                        new Encounter(
                                firstParty,
                                List.of(new Wendigo())
                        )
                )
        );

        // Secondo livello: due demoni, demone dagli occhi gialli e Lucifero.
        Level secondLevel = new Level(
                LevelNumber.TWO,
                List.of(
                        new Encounter(
                                secondParty,
                                List.of(new Demon(), new Demon())
                        ),
                        new Encounter(
                                secondParty,
                                List.of(new YellowEyedDemon())
                        ),
                        new Encounter(
                                secondParty,
                                List.of(new Lucifer())
                        )
                )
        );

        // Terzo livello: due leviatani, due angeli e avversario finale.
        Level thirdLevel = new Level(
                LevelNumber.THREE,
                List.of(
                        new Encounter(
                                thirdParty,
                                List.of(
                                        new Leviathan(),
                                        new Leviathan()
                                )
                        ),
                        new Encounter(
                                thirdParty,
                                List.of(new Angel(), new Angel())
                        ),
                        new Encounter(
                                thirdParty,
                                List.of(new God())
                        )
                )
        );

        return new GameState(
                List.of(firstLevel, secondLevel, thirdLevel)
        );
    }

    /**
     * Crea un gruppo usando gli eroi forniti nell’ordine indicato, senza copiarli.
     *
     * @param heroes eroi che compongono il gruppo
     * @return nuovo gruppo con i riferimenti forniti
     * @throws IllegalArgumentException se un eroe è null o duplicato
     */
    private Party createParty(Hero... heroes) {
        Party party = new Party();

        for (Hero hero : heroes) {
            party.addMember(hero);
        }

        return party;
    }
}
