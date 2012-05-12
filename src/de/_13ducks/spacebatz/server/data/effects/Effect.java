package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.EffectCarrier;

/**
 * Superklasse für Effekte.
 *
 * @author michael
 */
public abstract class Effect {

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird, ruft er regelmäßig diese Funktion auf.
     *
     * @return true, wenn der Effekt noch aktiv ist, oder false wenn er entfernt werden soll
     */
    public abstract boolean tick();

    /**
     * Wird aufgerufen, wenn dieser Effekt auf einen Char angewandt wird
     *
     * @param affectedChar der Char, auf den der Effekt angewandt wird
     */
    public abstract void applyToChar(EffectCarrier affectedChar);

    /**
     * Löst diesen Effekt an einer Position aus.
     * Kann z.B. für AoE-Effekte verwendet werden
     *
     * @param x X-Koordinate der Position
     * @param y Y-Koordinate der Position
     * @param hitChar der Char, der direkt getroffen wurde, oder null fals kein Char direkt getroffen wurde
     */
    public abstract void applyToPosition(double x, double y, EffectCarrier hitChar);

    /**
     * Entfernt den Effekt wieder.
     */
    public abstract void remove();
}
