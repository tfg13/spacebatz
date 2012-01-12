package de._13ducks.spacebatz.server.data;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Die normale Geschwindigkeit dieses Players.
     */
    private double speed;

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

    /**
     * Ein move-Request vom Client ist eingegangen. Teil der Netz-Infrastruktur, muss schnell verarbeitet werden
     *
     * @param w W-Button gedrückt.
     * @param a A-Button gedrückt.
     * @param s S-Button gedrückt.
     * @param d D-Button gedrückt.
     */
    public void clientMove(boolean w, boolean a, boolean s, boolean d) {
        if (w) {
            posX += speed;
        }
        if (a) {
            posY -= speed;
        }
        if (s) {
            posX -= speed;
        }
        if (d) {
            posY += speed;
        }
        System.out.println("Char at " + posX + " " + posY);
    }
}
