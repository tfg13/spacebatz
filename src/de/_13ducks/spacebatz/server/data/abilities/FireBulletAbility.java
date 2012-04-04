package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Bullet;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.shared.Properties;

/**
 * Die Fähigkeit, Bullets zu schießen.
 * Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility extends Ability {

    @Override
    public void use() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useOnPosition(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useOnTarget(Entity target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(double angle) {
        Bullet bullet = new Bullet(Server.game.getTick(), 300, owner.getX(), owner.getY(), angle, 0.5, 1, Server.game.newNetID(), owner);
        Server.game.netIDMap.put(bullet.netID, bullet);
    }

    @Override
    public boolean isReady() {
        // IMMER BEREIT!
        return true;
    }

    @Override
    public void refreshProperties(Properties properties) {
        // Werte des Trägers werden ignoriert.
    }
}
