package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Superklasse für alle Fähigkeiten
 * Fähigkeiten sind alle Aktionen, wie z.B. Schießen, Bohren und Sprinten
 *
 * @author michael
 */
public abstract class Ability {

    /**
     * Der Charakter, der diese Fähigkeit hat
     */
    private Char owner;

    /**
     * Setzt den Besitzer dieser Fähigkeit
     *
     * @param owner der Char, der die Fähigkeit besitzt
     */
    public void setOwner(Char owner) {
        this.owner = owner;
    }

    /**
     * Aktiviert die Fähigkeit
     */
    public void useAbility() {
        if (owner == null) {
            throw new IllegalStateException("Der Fähigkeit wurde kein Besitzer zugewiesen!");
        } else if (!isReady()) {
            throw new IllegalStateException("Die Fähigkeit ist noch nicht bereit!");
        } else {
            onUse();
        }
    }

    /**
     * Wird aufgerufen, wenn die Fähigkeit eingesetzt wird
     *
     * Klasen, die Ability erweitern müssen diese Methode überschreiben
     */
    public abstract void onUse();

    /**
     * Gibt true zurück, wenn alle Bedingungen erfüllt sind um diese Fähigkeit zu verwenden (z.B. Cooldown, Energiekosten, ...)
     *
     * Klassen, die Ability erweitern müssen diese Methode überschreiben.
     *
     * @return true, wenn die Fähigkeit benutzt werden kann
     */
    public abstract boolean isReady();
}
