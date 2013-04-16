package de._13ducks.spacebatz.server.data.entities.move;

import de._13ducks.spacebatz.server.data.entities.Entity;

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
    }

    /**
     * Setzt die verwaltete Entity.
     * Kann nur ein einziges Mal aufgerufen werden.
     * Muss vor allen anderen Methoden aufgerufen werden.
     *
     * @param entity die master-Entity
     */
    public void setEntity(Entity entity) {
        if (entity != null) {
            throw new IllegalStateException("Cannot set master entity, this Mover already has one! (Master: " + entity + ")");
        }
        this.entity = entity;
    }

    @Override
    public double getX() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getY() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpeed(double speed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void tick(int gametick) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
