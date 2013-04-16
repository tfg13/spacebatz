package de._13ducks.spacebatz.server.data.entities.move;

/**
 * Verwaltet die Position und Bewegung einer Entity. Hat also mindestens Methoden, um X und Y zu bekommen.
 *
 * WIE ALLE KLASSEN IN DIESEM PAKET UNTERLIEGT AUCH DIESE EINER SCHREIBSPERRE!
 * NIEMAND AUßER MIR DARF DIESE KLASSE ÄNDERN!
 * ALLE ANDEREN ÄNDERUNGEN WERDEN ZURÜCKGESETZT!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public interface Mover {

    /**
     * Liefert die aktuelle X-Position der zugehörigen Entity.
     * Kann wegen Interpolation vom aktuellen Gametick abhängen (muss aber nicht).
     *
     * @return X-Koordinate
     */
    public double getX();

    /**
     * Liefert die aktuelle Y-Position der zugehörigen Entity.
     * Kann wegen Interpolation vom aktuellen Gametick abhängen (muss aber nicht).
     *
     * @return Y-Koordinate
     */
    public double getY();

    /**
     * Liefert die Geschwindigkeit der zughörigen Entity in Feldern pro Tick.
     *
     * @return Geschwindigkeit der Entity in Feldern pro Tick
     */
    public double getSpeed();

    /**
     * Setzt die Geschwindigkeit der zugehörigen Entity.
     * Implementierungen müssen garantieren, dass diese Methode jederzeit aufgerufen werden darf, auch während Bewegungen.
     * Weiter müssen die neuen Werte sofort übernommen werden.
     * Werte kleiner oder gleich 0 sind verboten.
     *
     * @param speed neue Geschwindigkeit
     */
    public void setSpeed(double speed);

    /**
     * Berechnet die Bewegung/Position für den gegebenen Tick.
     *
     * @param gametick der gametick
     */
    public void tick(int gametick);
}
