package it.unicam.cs.mpgc.rpg129131;

import java.util.List;

public interface GameView {

    void showMessage(String message);

    void showStatus(
            LevelNumber level,
            int encounterNumber,
            Encounter encounter
    );

    List<HeroAction> readActions(Encounter encounter);

    void showEvents(List<CombatEvent> events);
}
