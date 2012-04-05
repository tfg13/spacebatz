package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Bullet;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.shared.Properties;
import java.util.Random;

/**
 * Die Fähigkeit, Bullets zu schießen.
 * Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility extends Ability {

    private double damage;
    private double attackspeed;
    private double range;
    private int bulletpic;
    private double bulletspeed;
    private double spread;
    private double explosionradius;

    public FireBulletAbility(double damage, double attackspeed, double range, int bulletpic, double bulletspeed, double spread, double explosionradius) {
        this.damage = damage;
        this.attackspeed = attackspeed;
        this.range = range;
        this.bulletpic = bulletpic;
        this.bulletspeed = bulletspeed;
        this.spread = spread;
        this.explosionradius = explosionradius;
    }

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
        if (owner.getAttackCooldownTick() <= Server.game.getTick()) {
            owner.setAttackCooldownTick(Server.game.getTick() + (int) attackspeed);
            Random random = new Random();
            angle += random.nextGaussian() * spread;
            int lifetime = (int) (range / bulletspeed);

            Bullet bullet = new Bullet(Server.game.getTick(), lifetime, owner.getX(), owner.getY(), angle, bulletspeed, bulletpic, Server.game.newNetID(), owner);
            bullet.addEffect(new TrueDamageEffect((int) damage));
            Server.game.netIDMap.put(bullet.netID, bullet);
        }
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
