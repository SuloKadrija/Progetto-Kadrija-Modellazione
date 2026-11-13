package it.unicam.cs.mpgc.rpg129131.persistence;

import it.unicam.cs.mpgc.rpg129131.game.GameSession;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Definisce le operazioni di persistenza di una sessione su file.
 * Separa il contratto usato dall'applicazione dal formato concreto
 * con cui vengono conservati i dati.
 */
public interface GamePersistence {

    /**
     * Salva la sessione sul percorso indicato.
     * L'operazione non deve modificare lo stato della partita.
     *
     * @param file percorso del salvataggio
     * @param session sessione attiva da salvare
     * @throws IllegalArgumentException se il percorso è null
     * @throws IOException se lo stato non è salvabile o la scrittura fallisce
     */
    void save(Path file, GameSession session) throws IOException;

    /**
     * Ricostruisce una sessione indipendente dal salvataggio.
     * Il chiamante decide se sostituire la sessione attualmente aperta.
     *
     * @param file percorso del salvataggio
     * @return sessione ripristinata e pronta a proseguire
     * @throws IllegalArgumentException se il percorso è null
     * @throws IOException se lettura, formato o dati non sono validi
     */
    GameSession load(Path file) throws IOException;
}