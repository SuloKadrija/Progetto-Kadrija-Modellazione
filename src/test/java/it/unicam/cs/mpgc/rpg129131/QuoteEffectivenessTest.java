package it.unicam.cs.mpgc.rpg129131;

import it.unicam.cs.mpgc.rpg129131.model.LevelNumber;
import it.unicam.cs.mpgc.rpg129131.model.enemy.Enemy;
import it.unicam.cs.mpgc.rpg129131.model.enemy.EnemyType;
import it.unicam.cs.mpgc.rpg129131.model.item.Quote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifica l'efficacia esclusiva dei Passi della Bibbia contro Dio.
 * Attraversa tutti i valori di EnemyType, includendo automaticamente
 * le categorie che saranno eventualmente aggiunte all'enumerazione.
 */
class QuoteEffectivenessTest {

    /**
     * Ogni categoria diversa da GOD deve avere efficacia zero.
     */
    @Test
    void isIneffectiveAgainstEveryNonGodType() {
        Quote quote = new Quote();

        for (EnemyType type : EnemyType.values()) {
            if (type != EnemyType.GOD) {
                assertEquals(
                        0.0,
                        quote.getMultiplierAgainst(createEnemy(type)),
                        0.000001,
                        "Efficacia non nulla contro " + type
                );
            }
        }
    }

    /**
     * L'efficacia contro GOD e il moltiplicatore base restano invariati.
     */
    @Test
    void preservesGodEffectivenessAndBaseMultiplier() {
        Quote quote = new Quote();

        assertEquals(
                2.0,
                quote.getMultiplierAgainst(createEnemy(EnemyType.GOD)),
                0.000001
        );

        assertEquals(
                1.2,
                quote.getBaseMultiplier(),
                0.000001
        );
    }

    /**
     * Crea un nemico minimo per isolare la categoria esaminata.
     * Non viene inserito in un livello o in un incontro.
     *
     * @param type categoria da verificare
     * @return nemico di prova della categoria richiesta
     */
    private Enemy createEnemy(EnemyType type) {
        return new Enemy(
                "Nemico di prova",
                1,
                0,
                0,
                type,
                LevelNumber.ONE
        ) {
        };
    }
}