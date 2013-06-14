package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Generisches Rukmsteh-Behaviour. Der Enemy mit diesem Behaviour entfernt sich
 * von anderen Enemies denen er zu nahe steht.
 *
 * @author michael
 */
public class GenericStandDivergeBehaviour extends Behaviour {

    /**
     * Die Zahl der Ticks zwischen Diverge-Vorgängen.
     */
    private final int DIVERGE_TIMEOUT_TICKS = 25;
    /**
     * Zufälliger Wert zwischen 0 und DIVERGE_TIMEOUT für Lastverteilung.
     */
    private final int randomTick = (int) (Math.random() * (DIVERGE_TIMEOUT_TICKS));

    public GenericStandDivergeBehaviour(Enemy owner) {
        super(owner);
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (Server.game.getTick() % DIVERGE_TIMEOUT_TICKS == randomTick) {

            Divergation div = computeDivergationVector();
            if (div.speed > 0 && (div.direction.x != 0 || div.direction.y != 0)) {
                owner.move.setVector(div.direction.x, div.direction.y);
                owner.setSpeed(div.speed);
            } else {
                if (owner.move.isMoving()) {
                    owner.move.stopMovement();
                }
            }
        }
        return this;
    }

    /**
     * Berechnet einen Vektor, der die Entity an der gegebenen Position von
     * allen anderen gegebenen Positionen weggehen lässt.
     *
     * @param position die Üosition der Entity , für die der Vektor berechnet
     * werden soll.
     * @param otherPositions die Positionen, von denen die Entity sich entfernen
     * soll.
     * @return ein Vektor, der die Entity von den Positionen wegbewegt.
     */
    private Divergation computeDivergationVector() {
        Vector direction = new Vector(0, 0);
        double speed = 0;
        for (Entity e : Server.entityMap.getEntitiesAroundPoint(owner.getX(), owner.getY(), 5)) {
            if (e instanceof Char && e != owner) {
                double distance = GeoTools.getDistance(owner.getX(), owner.getY(), e.getX(), e.getY());
                double maxDistance = (owner.getSize() + e.getSize()) / 2;
                if (distance < maxDistance) {
                    double divergationFactor = 1 - (distance / maxDistance);
                    if (divergationFactor <= 1.0) {
                        Vector singleDivergation = new Vector(owner.getX() - e.getX(), owner.getY() - e.getY());
                        speed = Math.max(speed, owner.maxSpeed * divergationFactor);
                        direction = direction.add(singleDivergation.multiply(divergationFactor));
                    }
                }
            }
        }

        return new Divergation(speed, direction);
    }

    private class Divergation {

        public Divergation(double speed, Vector divergation) {
            this.speed = speed;
            this.direction = divergation;
        }
        public double speed;
        public Vector direction;
    }
}
