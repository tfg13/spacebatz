package de._13ducks.spacebatz.server.data.effects;

/**
 * Superklasse für Effekte, in einem Gebiet oder auf einen Punkt wirken
 *
 * @author michael
 */
public abstract class AreaEffect implements Effect {

    /**
     * Löst den Effekt an der gegebenen Position aus
     *
     * @param x die X-Koordinate, an der der Effekt ausgelöst wird
     * @param y die Y-Koordinate, an der der Effekt ausgelöst wird
     */
    public abstract void trigger(double x, double y);
}
