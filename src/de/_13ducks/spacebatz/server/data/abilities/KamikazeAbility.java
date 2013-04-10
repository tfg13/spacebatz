package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.Iterator;

/**
 * Beschädigt ale Chars in der Umgebung.
 *
 * @author michael
 */
public class KamikazeAbility extends Ability {
    
    private double range;
    private int damage;
    
    public KamikazeAbility(double range, int damage) {
        this.range = range;
        this.damage = damage;
    }
    
    @Override
    protected void useOnPosition(Char user, double x, double y) {
        for (Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(user.getX(), user.getY(), range).iterator(); iter.hasNext();) {
            Entity target = iter.next();
            if (target instanceof Char) {
                Char targetChar = (Char) target;
                targetChar.decreaseHitpoints(damage);
            }
        }
    }
    
    @Override
    protected void useInAngle(Char user, double angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
