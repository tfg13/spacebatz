package de._13ducks.spacebatz.server.data.entities.move;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.EntityLinearTargetObserver;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Altes, default-Bewegungssystem.
 * Interpoliert Einheitenposition während der Bewegung.
 * Ermöglicht dem Server, Einheiten einfach zu bewegen, z.B. in dem ein Ziel angegeben wird.
 *
 * WIE ALLE KLASSEN IN DIESEM PAKET UNTERLIEGT AUCH DIESE EINER SCHREIBSPERRE!
 * NIEMAND AUßER MIR DARF DIESE KLASSE ÄNDERN!
 * ALLE ANDEREN ÄNDERUNGEN WERDEN ZURÜCKGESETZT!
 *
 * Das Bewegungssystem unterstützt 3 Laufmodi.
 * Der erste wird mit setVector gestartet und lässt die Entity einfach in eine Richtung laufen.
 * Die Entity hält nur an, falls ein Hindernis auftaucht, oder die Bewegung mit stopMovement angehalten, bzw. mit setVector geändert wird.
 * Der zweite Modus wird mit setLinearTarget aufgerufen und startet eine lineare Bewegung bis zum Ziel. Hier meldet sich die Entity beim
 * Listener zurück, wenn das Ziel erreicht wird, oder wenn ein Hindernis den Weg blockiert.
 * Aufrufen von setVector im zweiten Modus oder setLinearTarget im ersten bricht die jeweilige Bewegung ab.
 * Der dritte Modus ist der Zielverfolgungsmodus. In diesem Verfolgt die Einheit ein (bewegliches) Ziel, also eine andere Entity.
 * Es wird einfach in Richtung des Ziels gelaufen, solange keine Hinternisse im Weg sind.
 *
 * Mit einer Bewegung im zweiten Modus kann, im Gegensatz zum ersten, ein bestimmter Zielpunkt exakt erreicht werden.
 * Hier werden vom Bewegungssystem alle Verschiebungen wegen Tickdelay oder Fließkomma-Ungenauigkeiten rausgerechnet.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class InterpolatedMover implements Mover {

    /**
     * Die verwaltete Entity.
     */
    private Entity entity;
    /**
     * Die Position dieser Einheit in X-Richtung.
     */
    private double posX;
    /**
     * Die Position dieser Einheit in Y-Richtung.
     */
    private double posY;
    /**
     * Die Startposition der aktuellen Bewegung. Nur für Client.
     */
    private double moveStartX;
    /**
     * Die Startposition der aktuellen Bewegung. Nur für Client.
     */
    private double moveStartY;
    /**
     * Restlänge des aktuellen Streckenabschnitts.
     * -1, wenn kein Streckenfahren aktiv
     */
    private double remainingPathLength = -1;
    /**
     * Das Ziel des aktuellen Steckenabschnitts in X-Richtung.
     */
    private double targetX;
    /**
     * Das Ziel des aktuellen Streckenabschnitts in Y-Richtung.
     */
    private double targetY;
    /**
     * Das Ziel im Bewegungsmodus 3.
     * null, wenn kein Verfolgungsmodus aktiv
     */
    private Entity targetEntity;
    /**
     * Der Observer der aktuellen target-Bewegung.
     * Muss immer gesetzt sein, wenn die Einheit in Modus 2 läuft.
     * Ansonsten nicht definiert.
     */
    private EntityLinearTargetObserver observer;
    /**
     * Die Geschwindigkeit der Bewegung
     */
    private double speed = .17;
    /**
     * Der Gametick in dem die Bewegung gestartet wurde (Muss für den Client gespeichert werden)
     */
    private int moveStartTick;
    /**
     * Die X-Koordinate des Bewegungsvektors
     */
    private double vecX;
    /**
     * Die Y-Koordinate des Bewegungsvektors
     */
    private double vecY;
    /**
     * True, wenn die Einheit sich gerade bewegt.
     */
    private boolean moving = false;

    /**
     * Erzeugt eine neuen InterpolatedMover.
     * Die übergebenen Koordinaten sind die Start-Koodinaten.
     *
     * @param x die x-Startkoordinate
     * @param y die y-Startkoodinate
     *
     */
    public InterpolatedMover(double x, double y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * Setzt die Entity, dessen Position dieses Bewegungssystem verwaltet.
     * Kann nur ein einziges Mal aufgerufen werden.
     *
     * @param entity die master-Entity
     */
    public void setEntity(Entity entity) {
        if (entity != null) {
            throw new IllegalStateException("Cannot set master entity, this Mover already has one! (Master: " + entity + ")");
        }
        this.entity = entity;
    }

    /**
     * Liefert true, wenn die Einheit sich gerade bewegt.
     *
     * @return true, wenn die Einheit sich gerade bewegt
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Setzt die Position dieser Einheit auf den angegebenen Wert. Wenn entweder x oder y (nicht beide!) NaN sind, wird die Bewegung nur in eine Richtung angehalten. Darf nicht aufgerufen werden, wenn
     * sich die Einheit gar nicht bewegt.
     *
     * @param x die X-Stop-Koordinate oder NaN
     * @param y die Y-Stop-Koordinate oder NaN
     */
    private void setStopXY(double x, double y) {
        boolean xCont = Double.isNaN(x);
        boolean yCont = Double.isNaN(y);
        if (xCont && yCont) {
            throw new IllegalArgumentException("Cannot setStop without stopping at all! (x=y=NaN)");
        }
        if (!moving) {
            throw new IllegalArgumentException("Cannot setStop, Entity is not moving at all!");
        }
        // Linearmodus abschalten, damit stopMovement() nicht (nochmal) den Observer benachrichtigt.
        boolean pathColBlocked = false;
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            pathColBlocked = true;
        }
        // Gegnerverfolgung abschalten
        targetEntity = null;
        if (xCont) {
            posY = y;
            vecY = 0;
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            if (vecX == 0) {
                moving = false;
            }
        } else if (yCont) {
            posX = x;
            vecX = 0;
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            if (vecY == 0) {
                moving = false;
            }
        } else {
            stopMovement();
            posX = x;
            posY = y;
        }
        if (pathColBlocked) {
            observer.movementBlocked();
        }
    }

    /**
     * Stoppt die Einheit sofort. Wenn die Einheit sich gar nicht bewegt passiert nichts.
     */
    public void stopMovement() {
        moving = false;
        moveStartTick = -1;
        Server.sync.updateMovement(entity);
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            observer.movementAborted();
        }
        targetEntity = null;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Einheiten verschieben höchstens ein mal pro Tick ihre Position. (das ist normale Bewegung) Ansonsten ist diese Position konstant.
     *
     * @return Die X-Position dieses Chars
     */
    @Override
    public double getX() {
        return posX;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Einheiten verschieben häcshtens ein mal pro Tick ihre Position. (das ist normale Bewegung) Ansonsten ist diese Position konstant.
     *
     * @return Die Y-Position dieses Chars
     */
    @Override
    public double getY() {
        return posY;
    }

    /**
     * Wird aufgerufen, wenn die Bewegungsrichtuing der Entity sich ändert.
     */
    protected void directionChanged(double newVecX, double newVecY) {
    }

    /**
     * Setzt den Bewegungsvektor dieses Chars neu.
     * Die Einheit bewegt sich nach dem Aufruf in diese Richtung im Modus 1.
     * Der Vektor wird normalisiert, kann also die Geschwindigkeit nicht beeinflussen.
     * Das geht nur mit setSpeed. x und y dürfen nicht beide 0 sein!
     */
    public void setVector(double x, double y) {
        if (x == 0 && y == 0) {
            throw new IllegalArgumentException("Cannot set moveVector, x = y = 0 is not allowed!");
        }
        normalizeAndSetVector(x, y);
        moving = true;
        // Das ist eine neue Client-Bewegung
        moveStartTick = Server.game.getTick();
        moveStartX = posX;
        moveStartY = posY;
        directionChanged(vecX, vecY);
        Server.sync.updateMovement(entity);
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            observer.movementAborted();
        }
        targetEntity = null;
    }

    /**
     * Liefert die Geschwindigkeit dieser Einheit zurück.
     *
     * @return die Geschwindigkeit dieser Einheit
     */
    @Override
    public double getSpeed() {
        return speed;
    }

    /**
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt.
     * Kann auch während einer Bewegung aufgerufen werden.
     *
     * @param speed die neue Geschwindigkeit > 0
     */
    @Override
    public void setSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Cannot set speed: Must be greater than zero");
        }
        this.speed = speed;
        if (moving) {
            // Neue Client-Bewegung simulieren
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            Server.sync.updateMovement(entity);
        }
    }

    /**
     * Lässt die Einheit auf einer linearen Strecke zum Ziel laufen.
     * Wenn die Bewegung nicht unterbrochen wird, und der Weg zum Ziel frei ist,
     * wird die Einheit exakt auf dem Ziel angehalten.
     * Staret eine neue Bewegung (Modus 2), stoppt aktuell laufende.
     *
     * @param tx Ziel-X
     * @param ty Ziel-Y
     * @param obs Observer
     */
    public void setLinearTarget(double tx, double ty, EntityLinearTargetObserver obs) {
        if (Double.isInfinite(tx) || Double.isInfinite(ty) || Double.isNaN(tx) || Double.isNaN(ty)) {
            throw new IllegalArgumentException("Illegal target!");
        }
        if (obs == null) {
            throw new IllegalArgumentException("Observer must not be null!");
        }
        if (remainingPathLength != -1) {
            observer.movementAborted();
        }
        // Sofort da?
        if (Math.abs(tx - getX()) < CompileTimeParameters.DOUBLE_EQUALS_DIST && Math.abs(ty - getY()) < CompileTimeParameters.DOUBLE_EQUALS_DIST) {
            // Gar nicht erst bewegen.
            obs.targetReached();
            return;
        }
        observer = obs;
        // Vektor neu?
        Vector oldVector = new Vector(vecX, vecY);
        // Vektor zum Ziel und Länge berechnen:
        normalizeAndSetVector(tx - posX, ty - posY);
        remainingPathLength = Math.sqrt((tx - posX) * (tx - posX) + (ty - posY) * (ty - posY));
        targetX = tx;
        targetY = ty;
        if (!oldVector.equals(new Vector(vecX, vecY)) || targetEntity != null || moving == false) {
            // Das ist eine neue Client-Bewegung
            moving = true;
            targetEntity = null;
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            Server.sync.updateMovement(entity);
        }
    }

    /**
     * Normalisiert den Vektor (x, y) und setzt ihn anschließend.
     *
     * @param x X-Richtung
     * @param y Y-Richtung
     */
    private void normalizeAndSetVector(double x, double y) {
        // Länge berechnen (Pythagoras)
        double length = Math.sqrt((x * x) + (y * y));
        // Normalisieren und setzen
        vecX = x / length;
        vecY = y / length;
        directionChanged(vecX, vecY);
    }

    public Movement getMovement() {
        return computeMovement();
    }

    private Movement computeMovement() {
        if (isMoving()) {
            if (targetEntity == null) {
                // Modus 1 + 2
                return new Movement(moveStartX, moveStartY, vecX, vecY, moveStartTick, speed);
            } else {
                // Modus 3
                return new Movement(moveStartX, moveStartY, targetEntity.netID, moveStartTick, speed);
            }
        } else {
            return new Movement(posX, posY, 0, 0, -1, 0);
        }
    }

    /**
     * Berechnet einen gameTick für die Entity.
     */
    @Override
    public void tick(int gameTick) {
        if (moving) { // Einheit bewegt sich
            // Im Verfolgungsmodus Richtung anpassen:
            if (targetEntity != null) {
                normalizeAndSetVector(targetEntity.getX() - posX, targetEntity.getY() - posY);
            }
            double oldX = posX;
            double oldY = posY;
            //System.out.println("SCALC: " + speed * vecX + " " + speed * vecY + " at " + Server.game.getTick());
            double predictedX = posX + speed * vecX;
            double predictedY = posY + speed * vecY;
            // Bewegung einfach mal setzen, und dann die Kollission das prüfen lassen
            posX = predictedX;
            posY = predictedY;
            // Ist das weiter als wir wollen?
            boolean targetReached = false;
            if (remainingPathLength != -1) {
                remainingPathLength -= Math.sqrt((predictedX - oldX) * (predictedX - oldX) + (predictedY - oldY) * (predictedY - oldY));
                if (remainingPathLength <= 0) {
                    targetReached = true;
                    posX = targetX;
                    posY = targetY;
                    predictedX = posX;
                    predictedY = posY;
                }
            }
            int[] colBlock = computeCollision(oldX, oldY, posX, posY);
            // Hat sich unsere Bewegung geändert?
            if (posX != predictedX || posY != predictedY) {
                // Client(s) informieren
                Server.sync.updateMovement(entity);
                // Unterklassen informieren
                entity.onWallCollision(colBlock);
            } else if (targetReached) {
                // Wir sind am Ziel!
                remainingPathLength = -1;
                stopMovement();
                observer.targetReached();
            }
        }
    }

    /**
     * Liefert die Richtung in die sich diese Einheit gerade bewegt. Die Angabe ist eine Fließkommazahl von 0 bis 2PI im üblichen Einheitskreisverfahren. Das Verhalten, wenn die Einheit sich nicht
     * bewegt ist nicht definiert.
     *
     * @return die Richtung in die sich diese Einheit gerade bewegt
     */
    public double getDirection() {
        double dir = Math.atan2(vecY, vecX);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        return dir;
    }

    /**
     * Berechnet, ob wir uns vom angegebenen Startpunkt gefahrlos zum angegebenen Zielpunkt bewegen können. Geht davon aus, das wir uns bereits bewegen - nimmt sofort Korrekturen an der aktuellen
     * Bewegung vor. Liefert den Block zurück, mit dem wir als nächstes kollidieren.
     *
     * @param fromX Startpunkt X (muss frei sein)
     * @param fromY Startpunkt Y (muss frei sein)
     * @param toX Zielpunkt X
     * @param toY Zielpunkt Y
     * @return falls Kollision: Erster Kollisionsblock, sonst undefiniert
     */
    private int[] computeCollision(double fromX, double fromY, double toX, double toY) {
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - entity.getSize() / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + entity.getSize() / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - entity.getSize() / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + entity.getSize() / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Den Block, mit dem wir kollidieren zwischenspeichern
        int[] collisionBlock = new int[2];
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((entity.getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
                        }
                    }
                }
            }
        }
        double sx = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann ohne kollision:
            sx = fromX + smallestD * deltaX;
        }

        // Für die Y-Berechung die Werte zurücksetzten, für die Block-Berechung aber behalten!
        double globalsmallestD = smallestD;
        smallestD = Double.MAX_VALUE;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((entity.getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                        // Näher als die von X?
                        if (d < globalsmallestD) {
                            globalsmallestD = d;
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
                        }
                    }
                }
            }
        }
        double sy = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann
            sy = fromY + smallestD * deltaY;
        }

        // Bewegung koorigieren?
        if (!(Double.isNaN(sx) && Double.isNaN(sy))) {
            setStopXY(sx, sy);
        }

        return collisionBlock;
    }

    /**
     * Liefert den Vektor, in den sich diese Einheit gerade bewegt. Undefiniert, falls die Einheit sich nicht bewegt.
     *
     * @return X-Richtungsvektor
     */
    protected double getVecX() {
        return vecX;
    }

    /**
     * Liefert den Vektor, in den sich diese Einheit gerade bewegt. Undefiniert, falls die Einheit sich nicht bewegt.
     *
     * @return Y-Richtungsvektor
     */
    protected double getVecY() {
        return vecY;
    }

    /**
     * Beantwortet, ob die übergeben Einheit gerade verfolgt wird.
     *
     * @param target die fragliche Einheit
     * @return true, wenn genau diese verfolgt wird, sonst false
     */
    public boolean isFollowingTarget(Entity target) {
        return (target != null && target.equals(targetEntity));
    }

    /**
     * Lässt diese Entity die gegebene verfolgen.
     * Versetzt die Entity dazu in Bewegungsmodus 3.
     *
     * @param target die Zielentity
     */
    public void setFollowTarget(Entity target) {
        if (target == null) {
            throw new IllegalArgumentException("Cannot set followTarget, is null!");
        }
        targetEntity = target;
        normalizeAndSetVector(target.getX() - posX, target.getY() - posY);
        moving = true;
        // Das ist eine neue Client-Bewegung
        moveStartTick = Server.game.getTick();
        moveStartX = posX;
        moveStartY = posY;
        Server.sync.updateMovement(entity);
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            observer.movementAborted();
        }
    }

    /**
     * @return the moveStartX
     */
    protected double getMoveStartX() {
        return moveStartX;
    }

    /**
     * @return the moveStartY
     */
    protected double getMoveStartY() {
        return moveStartY;
    }

    @Override
    public boolean positionUpdateRequired() {
        // Ausreichend präzise
        return moving;
    }
}
