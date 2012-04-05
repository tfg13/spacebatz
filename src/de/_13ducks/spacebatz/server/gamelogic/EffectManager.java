package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.EffectCarrier;
import de._13ducks.spacebatz.server.data.Entity;
import java.util.Iterator;

/**
 * Verwaltet temporäre Effekte auf EffectCarriern.
 *
 * @author michael
 */
public class EffectManager {

    /**
     * Berechnet alle Effekte.
     */
    public static void computeEffects() {
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                ((Char) e).tick();
                // Wenn der Char tot ist, entfernen:
                if (((Char) e).getProperty("hitpoints") < 0) {
                    iter.remove();
                }
            }
        }
    }
}
