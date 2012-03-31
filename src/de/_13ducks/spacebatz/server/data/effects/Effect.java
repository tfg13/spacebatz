package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Superklasse für Effekte.
 *
 * @author michael
 */
public abstract class Effect {

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird, ruft er regelmäßig diese Funktion auf.
     */
    public abstract void tick();

    /**
     * Wird aufgerufen, wenn dieser Effekt auf einen Char angewandt wird
     *
     * @param affectedChar der Char, auf den der Effekt angewandt wird
     */
    public abstract void applyToChar(Char affectedChar);

    /**
     * Löst diesen Effekt an einer Position aus.
     * Kann z.B. für AoE-Effekte verwendet werden
     *
     * @param x X-Koordinate der Position
     * @param y Y-Koordinate der Position
     */
    public abstract void applyToPosition(double x, double y);

    /**
     * Entfernt den Effekt wieder
     */
    public abstract void remove();
}
