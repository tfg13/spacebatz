package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.util.Distance;

/**
 * Ein Standardgegner, der auf den Spieler zurennt.
 *
 * @author michael
 */
public class StandardEnemy extends Enemy {

    /**
     * Der Char, den dieser Enemy gerade verfolgt
     */
    private Char myTarget;

    /**
     * Erzeugt einen neuen StandardGegner.
     *
     * @param x
     * @param y
     * @param netID
     * @param enemyTypeID
     */
    public StandardEnemy(double x, double y, int netID, int enemyTypeID) {
        super(x, y, netID, enemyTypeID);
    }

    /**
     * Wird bei Kollisionen aufgerufen.
     *
     * @param other
     */
    @Override
    public void onCollision(Entity other) {
        super.onCollision(other);
        // Bei Kollision mit Spielern diese verfolgen:
        if (other instanceof Player) {
            myTarget = (Player) other;
        }
    }

    /**
     * Berechnet jeden Tick die KI.
     *
     * @param gameTick der GameTick für den berechnet wird.
     */
    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        // Nur alle 20 Ticks berechnen, um Auslastung zu veringern:
        if (gameTick % 20 == 0) {
            return;
        }

        // Haben wir schon ein Ziel?
        if (myTarget == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (getProperties().getSightrange() > Distance.getDistance(getX(), getY(), player.getX(), player.getY())) {
                    myTarget = player;
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (getProperties().getSightrange() * 2 < Distance.getDistance(getX(), getY(), myTarget.getX(), myTarget.getY())) {
                myTarget = null;
                stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (1.0 > Distance.getDistance(getX(), getY(), myTarget.getX(), myTarget.getY())) {
                    stopMovement();
                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vectorX = myTarget.getX() - getX();
                    double vectorY = myTarget.getY() - getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vectorX == 0.0) {
                        vectorX = 0.1;
                    }
                    if (vectorY == 0.0) {
                        vectorY = 0.1;
                    }
                    setVector(vectorX, vectorY);
                }



            }
        }
    }
}
