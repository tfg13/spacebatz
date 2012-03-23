package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Superklasse für Effekte, die auf Chars wirken (z.B. Schaden, Verlangsamung oder Attributboni).
 *
 * @author michael
 */
public abstract class CharEffect {

    /**
     * Der Char der von diesem Effekt beeinflusst wird
     */
    private Char affectedChar;

    /**
     * Wendet diesen Effekt auf einen Char an
     *
     * @param affectedChar der Char auf den der Effekt angewendet wird
     */
    public void applyToChar(Char affectedChar) {
        this.affectedChar = affectedChar;
        onApply();
    }

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird ruft er regelmäßig diese Funktion auf
     */
    public void tick() {
        if (affectedChar == null) {
            throw new IllegalStateException("Dieser Effekt wurde noch nicht auf einen Char angewendet!");
        } else {
            onTick();
        }
    }

    /**
     * Wird aufgerufen, wenn dieser Effekt auf einen Char angewandt wird
     */
    public abstract void onApply();

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird ruft er regelmäßig diese Funktion auf
     */
    public abstract void onTick();
}
