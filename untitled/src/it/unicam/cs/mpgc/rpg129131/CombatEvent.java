package it.unicam.cs.mpgc.rpg129131;

public record CombatEvent(String message) {

    public CombatEvent {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Il messaggio dell'evento non può essere nullo o vuoto.");
        }
    }
}