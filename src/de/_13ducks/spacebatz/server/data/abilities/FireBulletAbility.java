package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import java.util.Random;

/**
 * Die Fähigkeit, Bullets zu schießen. Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility implements Ability {

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
    public void useOnPosition(Char user, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(Char user, double angle) {
        if (user.attackCooldownTick <= Server.game.getTick()) {
            user.attackCooldownTick = (Server.game.getTick() + (int) attackspeed);
            Random random = new Random();
            angle += random.nextGaussian() * spread;
            int lifetime = (int) (range / bulletspeed);

            Bullet bullet = new Bullet(Server.game.getTick(), lifetime, user.getX(), user.getY(), angle, bulletspeed, bulletpic, Server.game.newNetID(), user);
            bullet.addEffect(new TrueDamageEffect((int) damage));
            Server.game.netIDMap.put(bullet.netID, bullet);
        }
    }
}