package de._13ducks.spacebatz.server.data;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann nur einen Player pro Client geben.
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     * @param client der Client, dem dieser Player gehören soll.
     */
    public Player(double x, double y, int id) {
        super(x, y, id);
    }
}
