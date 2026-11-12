package it.unicam.cs.mpgc.rpg129131.model.enemy;

/**
 * Elenca le categorie dei nemici utilizzate per stabilire l’efficacia delle armi.
 * Non rappresenta singole istanze: due nemici distinti possono avere lo stesso tipo.
 */
public enum EnemyType {
    /**
     * Fantasma.
     */
    GHOST,
    /**
     * Vampiro.
     */
    VAMPIRE,
    /**
     * Wendigo.
     */
    WENDIGO,
    /**
     * Demone.
     */
    DEMON,
    /**
     * Demone dagli occhi gialli.
     */
    YELLOW_EYED_DEMON,
    /**
     * Lucifero.
     */
    LUCIFER,
    /**
     * Leviatano.
     */
    LEVIATHAN,
    /**
     * Angelo.
     */
    ANGEL,
    /**
     * Dio, avversario finale.
     */
    GOD
}
