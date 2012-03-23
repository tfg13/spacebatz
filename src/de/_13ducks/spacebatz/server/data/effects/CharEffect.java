package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Superklasse für Effekte, die auf Chars wirken (z.B. Schaden, Verlangsamung oder Attributboni).
 *
 * @author michael
 */
public abstract class CharEffect implements Effect {

    /**
     * Der Char der von diesem Effekt beeinflusst wird
     */
    protected Char affectedChar;

    /**
     * Wendet diesen Effekt auf einen Char an
     *
     * @param affectedChar der Char auf den der Effekt angewendet wird
     */
    public void applyEffectToChar(Char affectedChar) {
        if (affectedChar != null) {
            throw new IllegalStateException("Dieser Effekt wurde bereits einem Char zugewiesen.");
        } else {
            this.affectedChar = affectedChar;
            applyToChar(affectedChar);
        }

    }

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird ruft er regelmäßig diese Funktion auf
     */
    public void tick() {
        if (affectedChar == null) {
            throw new IllegalStateException("Dieser Effekt muss auf einen Char angewendet werden bevor seine tick()-Funktion aufgerufen wird!");
        } else {
            onTick();
        }
    }

    /**
     * Wird aufgerufen, wenn dieser Effekt auf einen Char angewandt wird
     *
     * @param affectedChar der Char auf den der Effekt angewandt wird
     */
    public abstract void applyToChar(Char affectedChar);

    /**
     * Wenn ein Char von diesem Effekt beeinflust wird ruft er regelmäßig diese Funktion auf
     */
    public abstract void onTick();

    /**
     * Entfernt den Effekt wieder
     */
    public void removeEffect() {
        if (affectedChar == null) {
            throw new IllegalStateException("Der Effekt wurde noch nicht angewandt, also kann er auch nicht entfernt werden!");
        } else {
            remove();
        }
    }

    /**
     * Entfernt den Effekt wieder
     */
    public abstract void remove();
}
