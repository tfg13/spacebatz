package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.EffectCarrier;
import de._13ducks.spacebatz.server.data.entities.Entity;
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
        Iterator<Entity> iter = Server.game.getEntityManager().netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof EffectCarrier) {
                ((EffectCarrier) e).tick();
                // Wenn der Char tot ist, entfernen:
                if (((Char) e).getProperties().getHitpoints() <= 0) {
                    iter.remove();
                }
            }
        }
    }
}
