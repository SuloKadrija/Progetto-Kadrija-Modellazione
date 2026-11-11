package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public class GameFactory {

    public GameState create() {
        Sam sam = new Sam();
        Dean dean = new Dean();
        Bobby bobby = new Bobby();
        Castiel castiel = new Castiel();
        Jack jack = new Jack();

        Party firstParty = createParty(sam, dean);
        Party secondParty = createParty(sam, dean, bobby);
        Party thirdParty = createParty(sam, dean, castiel, jack);

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

    private Party createParty(Hero... heroes) {
        Party party = new Party();

        for (Hero hero : heroes) {
            party.addMember(hero);
        }

        return party;
    }
}
