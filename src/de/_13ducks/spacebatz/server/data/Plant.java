package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;

/**
 * Eine Pflanze
 *
 * @author michael
 */
public class Plant {

    /**
     * Die Position der Pflanze
     */
    private int x, y;
    /**
     * Die Bodentextur der Pflanze
     */
    private int groundTex;

    /**
     * Konstrukto, erstell eine neue Pflanze
     */
    public Plant(int x, int y) {
        this.x = x;
        this.y = y;
        groundTex = 5 + (int) (Math.random() * 3);
        Server.game.getLevel().setSingleTile(x, y, groundTex);
        Server.msgSender.broadcastGroundChange(x, y, groundTex);
    }

    /**
     * Gibt die X-Koordinate der Pfalze zurück
     *
     * @return X-Koordinate der Pfalnze
     */
    public int getX() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate der Pfalze zurück
     *
     * @return Y-Koordinate der Pfalnze
     */
    public int getY() {
        return y;
    }

    /**
     * Gibt die Bodentextur dieser Pflanze zurück
     *
     * @return der Int der die Bodentextur beschreibt
     */
    public int getTex() {
        return groundTex;
    }
}
