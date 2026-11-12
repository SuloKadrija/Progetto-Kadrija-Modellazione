package it.unicam.cs.mpgc.rpg129131;

/**
 * Definisce il contratto comune degli oggetti inseribili negli inventari.
 * Ogni oggetto fornisce un nome visualizzabile dalle interfacce del gioco.
 */
public interface Item {
    /**
     * Fornisce il nome da mostrare nelle interfacce.
     *
     * @return nome dell’oggetto
     */
    String getName();
}
