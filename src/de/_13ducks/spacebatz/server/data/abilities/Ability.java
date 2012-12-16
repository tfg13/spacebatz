/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;

/**
 * Schnittstelle für Fähigkeiten.
 *
 * Fähigkeiten können von Chars auf Positionen benutzt werden.
 *
 * @author michael
 */
public abstract class Ability {

    private int cooldown;
    private int lastActivation;

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Gibt zurück, ob die Fähigkeit noch auf cooldown ist.
     *
     * @return
     */
    public boolean isOnCooldown() {
        return (Server.game.getTick() - lastActivation) < cooldown;
    }

    /**
     * Benutzt die Fähigkeit auf eine Position, WENN der cooldown abgelaufen
     * ist. Macht niochts wenn der cooldown noch läuft.
     *
     * @param user
     * @param x
     * @param y
     */
    public void tryUseOnPosition(Char user, double x, double y) {
        if (!isOnCooldown()) {
            lastActivation = Server.game.getTick();
            useOnPosition(user, x, y);
        }
    }

    /**
     * Benutzt die Fähigkeitin einem Winkel, WENN der cooldown abgelaufen ist.
     * Macht niochts wenn der cooldown noch läuft.
     *
     * @param user
     * @param angle
     */
    public void tryUseInAngle(Char user, double angle) {
        if (!isOnCooldown()) {
            lastActivation = Server.game.getTick();
            useInAngle(user, angle);
        }
    }

    /**
     * Benutzt die Fähigkeit auf eine Position.
     *
     * @param user der Char, der die Fähigkeit benutzt
     * @param x die X-Koordinate der Zielposition
     * @param y die Y-Koordinate der Zielposition
     */
    protected abstract void useOnPosition(Char user, double x, double y);

    /**
     * Benutzt die Fähigkeit in einem Winkel
     *
     * @param user der Benutzer
     * @param angle der Zielwinkel
     */
    protected abstract void useInAngle(Char user, double angle);
}
