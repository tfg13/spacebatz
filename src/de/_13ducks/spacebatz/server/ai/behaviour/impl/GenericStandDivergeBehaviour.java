package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Generisches Rukmsteh-Behaviour. Der Enemy mit diesem Behaviour entfernt sich von anderen Enemies denen er zu nahe steht.
 *
 * @author michael
 */
public class GenericStandDivergeBehaviour extends Behaviour {

    public GenericStandDivergeBehaviour(Enemy owner) {
        super(owner);
    }

    @Override
    public Behaviour tick(int gameTick) {
        LinkedList<Entity> entities = Server.entityMap.getEntitiesAroundPoint(owner.move.getX(), owner.move.getY(), 5);
        ArrayList<Vector> positions = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Enemy && e != owner && GeoTools.getDistance(e.getX(), e.getY(), owner.getX(), owner.getY()) < (owner.getSize() + e.getSize())) {
                positions.add(new Vector(e.getX(), e.getY()));
            }
        }
        Vector enemyPositions[] = new Vector[positions.size()];
        enemyPositions = positions.toArray(enemyPositions);
        Vector divergeVector = computeDivergationVector(new Vector(owner.move.getX(), owner.move.getY()), enemyPositions);
        if (divergeVector.x != 0 || divergeVector.y != 0) {
            owner.move.setVector(divergeVector.x, divergeVector.y);
        } else {
            owner.move.stopMovement();
        }
        return this;

    }

    /**
     * Berechnet einen Vektor, der die Entity an der gegebenen Position von allen anderen gegebenen Positionen weggehen lässt.
     *
     * @param position die Üosition der Entity , für die der Vektor berechnet werden soll.
     * @param otherPositions die Positionen, von denen die Entity sich entfernen soll.
     * @return ein Vektor, der die Entity von den Positionen wegbewegt.
     */
    private static Vector computeDivergationVector(Vector position, Vector otherPositions[]) {
        Vector divergation = new Vector(0, 0);
        for (int i = 0; i < otherPositions.length; i++) {
            Vector singleDivergation = new Vector(-(otherPositions[i].x - position.x), -(otherPositions[i].y - position.y));
            divergation = divergation.add((singleDivergation));
        }
        return divergation;
    }

    public static void main(String args[]) {
        Vector others[] = {new Vector(10, 10), new Vector(5, 10)};
        Vector test = computeDivergationVector(new Vector(5, 5), others);
        System.out.println(test.x + " " + test.y);
    }
}
