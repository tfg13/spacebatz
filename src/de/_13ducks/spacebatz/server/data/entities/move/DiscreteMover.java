package de._13ducks.spacebatz.server.data.entities.move;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.Collision;
import de._13ducks.spacebatz.shared.Collision.CollisionResult;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Neues Bewegungssystem für von Menschen gesteuerte Players, deren Position sich auf
 * Client-Seite präzise vorhersagen lassen muss.
 *
 * Bewegt sich nicht auf einer interpolierten Bahn, sondern springt immer zum nächsten Punkt weiter.
 *
 * WIE ALLE KLASSEN IN DIESEM PAKET UNTERLIEGT AUCH DIESE EINER SCHREIBSPERRE!
 * NIEMAND AUßER MIR DARF DIESE KLASSE ÄNDERN!
 * ALLE ANDEREN ÄNDERUNGEN WERDEN ZURÜCKGESETZT!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DiscreteMover implements Mover {

    /**
     * Aktuelle Position dieses Players.
     * X-Richtung
     */
    private double x;
    /**
     * Aktuelle Position dieses Players.
     * Y-Richtung
     */
    private double y;
    /**
     * Der zuletzt gemachte Schritt.
     */
    private Vector lastStep = Vector.ZERO;
    /**
     * Aktuelle Geschwindigkeit dieses Players.
     */
    private double speed = CompileTimeParameters.BASE_MOVESPEED;
    /**
     * Die Entity, deren Position wir steuern.
     */
    private Entity entity;

    /**
     * Erstellt einen neuen DiscreteMover mit der gegebenen Startposition.
     *
     * @param x x-Startkoordinate
     * @param y y-Startkoordinate
     */
    public DiscreteMover(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Setzt die verwaltete Entity.
     * Kann nur ein einziges Mal aufgerufen werden.
     * Muss vor allen anderen Methoden aufgerufen werden.
     *
     * @param entity die master-Entity
     */
    public void setEntity(Entity entity) {
        if (this.entity != null) {
            throw new IllegalStateException("Cannot set master entity, this Mover already has one! (Master: " + entity + ")");
        }
        this.entity = entity;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must not be smaller than or equal to zero!");
        }
        this.speed = speed;
    }

    @Override
    public void tick(int gametick) {
        // Do nothing - for now
    }

    /**
     * Läuft einen Schritt in Richtung des gegebenen Ziels.
     * Läuft nur, soweit es etwaige Hindernisse erlauben.
     * Läuft mit der derzeit eingestellten Geschwindigkeit.
     * Läuft maximal bis zum gegebenen Ziel.
     * Muss regelmäßig aufgerufen werden, auch wenn man sich nicht bewegen will!
     *
     * @param tX Ziel in X-Richtung
     * @param tY Ziel in Y-Richtung
     */
    public void step(double tX, double tY) {
        Vector direction = new Vector(tX - x, tY - y);
        if (direction.length() > speed) {
            // Der Client will zu schnell laufen, Pech für seine Prediction aber das geht nicht
            direction = direction.normalize().multiply(speed);
        }
        Vector oldLastStep = lastStep;
        CollisionResult collision = Collision.computeCollision(x, y, x + direction.x, y + direction.y, entity.getSize(), Server.game.getLevel().getCollisionMap());
        // laufen so weit es geht:
        lastStep = new Vector(collision.maxX - x, collision.maxY - y);
        x = collision.maxX;
        y = collision.maxY;
        if (collision.collides) {
            // Kollision!
            entity.onWallCollision(collision.collidingBlock);
        }
        if (!oldLastStep.equals(lastStep)) {
            // Änderung der Bewegung --> SYNC
            Server.sync.updateMovement(entity);
        }
    }

    @Override
    public boolean positionUpdateRequired() {
        return (!lastStep.equals(Vector.ZERO));
    }

    @Override
    public Movement getMovement() {
        if (lastStep.equals(Vector.ZERO)) {
            // Steht
            return new Movement(x, y, 0, 0, -1, speed);
        } else {
            return new Movement(x - lastStep.x, y - lastStep.y, lastStep.normalize().x, lastStep.normalize().y, Server.game.getTick() - 1, speed);
        }
    }
}
