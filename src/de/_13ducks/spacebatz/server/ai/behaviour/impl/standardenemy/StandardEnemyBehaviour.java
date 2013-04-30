package de._13ducks.spacebatz.server.ai.behaviour.impl.standardenemy;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

/**
 * Ein Standardgegner, der auf den Spieler zurennt.
 *
 * @author michael
 */
public class StandardEnemyBehaviour extends Behaviour {

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
    public StandardEnemyBehaviour(Enemy enemy) {
        super(enemy);
    }

    /**
     * Wird bei Kollisionen aufgerufen.
     *
     * @param other
     */
    @Override
    public Behaviour onCollision(Entity other) {
        super.onCollision(other);
        // Bei Kollision mit Spielern diese verfolgen:
        if (other instanceof Player) {
            myTarget = (Player) other;
        }
        return this;
    }

    /**
     * Berechnet jeden Tick die KI.
     *
     * @param gameTick der GameTick für den berechnet wird.
     */
    @Override
    public Behaviour tick(int gameTick) {
        super.tick(gameTick);
        // Nur alle 20 Ticks berechnen, um Auslastung zu veringern:
        if (gameTick % 20 == 0) {
            return this;
        }

        // Haben wir schon ein Ziel?
        if (myTarget == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (owner.getProperties().getSightrange() > GeoTools.getDistance(owner.getX(), owner.getY(), player.getX(), player.getY())) {
                    myTarget = player;
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (owner.getProperties().getSightrange() * 2 < GeoTools.getDistance(owner.getX(), owner.getY(), myTarget.getX(), myTarget.getY())) {
                myTarget = null;
                owner.move.stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (1.0 > GeoTools.getDistance(owner.getX(), owner.getY(), myTarget.getX(), myTarget.getY())) {
                    owner.move.stopMovement();
                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vectorX = myTarget.getX() - owner.getX();
                    double vectorY = myTarget.getY() - owner.getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vectorX == 0.0) {
                        vectorX = 0.1;
                    }
                    if (vectorY == 0.0) {
                        vectorY = 0.1;
                    }
                    owner.move.setVector(vectorX, vectorY);
                }



            }
        }
        return this;
    }
}
