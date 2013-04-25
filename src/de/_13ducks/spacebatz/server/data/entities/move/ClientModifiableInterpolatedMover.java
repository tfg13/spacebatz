package de._13ducks.spacebatz.server.data.entities.move;

import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.EntityLinearTargetObserver;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Arbeitet grundsätzlich wie der InterpolatedMover, unterstütz aber nur dessn Modus 2.
 * Dafür wird diese Koordinate auch an den Client übertragen, der dann die Spawnposition und die Richtung ändern kann.
 *
 * Das ist beispielsweise für Bullets notwendig, die von predicteten Spielern abgefeuert werden.
 *
 * WIE ALLE KLASSEN IN DIESEM PAKET UNTERLIEGT AUCH DIESE EINER SCHREIBSPERRE!
 * NIEMAND AUßER MIR DARF DIESE KLASSE ÄNDERN!
 * ALLE ANDEREN ÄNDERUNGEN WERDEN ZURÜCKGESETZT!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientModifiableInterpolatedMover extends InterpolatedMover {

    /**
     * Erzeugt eine neuen ClientModifiableInterpolatedMover.
     * Die übergebenen Koordinaten sind die Start-Koodinaten.
     *
     * @param x die x-Startkoordinate
     * @param y die y-Startkoodinate
     *
     */
    public ClientModifiableInterpolatedMover(double x, double y) {
        super(x, y);
    }

    /**
     * Setzt den Bewegungsvektor dieses Chars neu.
     * Nicht erlaubt für dieses Bewegungssystem.
     */
    @Override
    public void setVector(double x, double y) {
        throw new UnsupportedOperationException("Cannot setVector: Not supported by this Mover!");
    }

    /**
     * Lässt diese Entity die gegebene verfolgen.
     * Nicht erlaubt für dieses Bewegungssystem.
     *
     * @param target
     */
    @Override
    public void setFollowTarget(Entity target) {
        throw new UnsupportedOperationException("Cannot setFollowTarget: Not supported by this Mover!");
    }

    /**
     * Lässt die Einheit auf einer linearen Strecke zum Ziel laufen.
     * Im Gegensatz zum Original wird diese Implementierung nicht stoppen, sondern immer weiter laufen.
     * Technisch gesehen läuft die Einheit nicht unendlich weit, aber sehr sehr weit (Die Länge der Strecke ist etwa 1.000.000) gesetzt.
     *
     * @param tx Ziel-X
     * @param ty Ziel-Y
     * @param obs Observer
     */
    @Override
    public void setLinearTarget(double tx, double ty, EntityLinearTargetObserver obs) {
        // Ziel so ändern, dass Richtung gleich bleibt
        Vector moveVector = new Vector(tx - getX(), ty - getY()).normalize().multiply(1000000.0);
        super.setLinearTarget(getX() + moveVector.x, getY() + moveVector.y, obs);
    }

    @Override
    protected Movement computeMovement() {
        Movement orig = super.computeMovement();
        return new Movement(orig.startX, orig.startY, getTargetX(), getTargetY(), orig.startTick, orig.speed);
    }
}
