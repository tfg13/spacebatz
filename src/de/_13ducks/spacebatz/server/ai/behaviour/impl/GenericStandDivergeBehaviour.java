package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.Divergation;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
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
    private final int randomTick = (int) (Math.random() * (1 / CompileTimeParameters.SERVER_TICKRATE));

    @Override
    public Behaviour tick(int gameTick) {
        if (Server.game.getTick() % CompileTimeParameters.SERVER_TICKRATE == randomTick) {
            LinkedList<Entity> entities = Server.entityMap.getEntitiesAroundPoint(owner.move.getX(), owner.move.getY(), 5);
            ArrayList<Vector> positions = new ArrayList<>();
            for (Entity e : entities) {
                if (e instanceof Enemy && e != owner && GeoTools.getDistance(e.getX(), e.getY(), owner.getX(), owner.getY()) < (owner.getSize() + e.getSize())) {
                    positions.add(new Vector(e.getX(), e.getY()));
                }
            }
            Vector enemyPositions[] = new Vector[positions.size()];
            enemyPositions = positions.toArray(enemyPositions);
            Vector divergeVector = Divergation.computeDivergationVector(new Vector(owner.move.getX(), owner.move.getY()), enemyPositions);
            if (divergeVector.x != 0 || divergeVector.y != 0) {
                owner.move.setVector(divergeVector.x, divergeVector.y);
            } else {
                owner.move.stopMovement();
            }
        }
        return this;

    }
}
