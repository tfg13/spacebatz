/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.entities.Char;

/**
 * Schnittstelle für Fähigkeiten.
 *
 * Fähigkeiten können von Chars auf Positionen benutzt werden.
 *
 * @author michael
 */
public interface Ability {

    /**
     * Benutzt die Fähigkeit auf eine Position.
     *
     * @param user der Char, der die Fähigkeit benutzt
     * @param x die X-Koordinate der Zielposition
     * @param y die Y-Koordinate der Zielposition
     */
    public abstract void useOnPosition(Char user, double x, double y);

    /**
     * Benutzt die Fähigkeit in einem Winkel
     *
     * @param user der Benutzer
     * @param angle der Zielwinkel
     */
    public abstract void useInAngle(Char user, double angle);
}
