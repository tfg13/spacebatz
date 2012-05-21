package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.ExplosionDamageEffect;
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

    public static final int FIREBULLETABILITY = 0;

    /**
     * Lässt einen Char ein Bullet auf ein Ziel schießen.
     *
     * @param user der Char, der diese Fähigkeit verwendet
     * @param targetX X-Koordinate des Ziels
     * @param targetY Y-Koordinate des Ziels
     * @deprecated useOnPosition stattdessen verwenden!
     */
    public static void fireBullet(Char user, double targetX, double targetY) {
        double dx = targetX - user.getX();
        double dy = targetY - user.getY();
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }


        double damage = user.getProperty("shootDamage");
        double range = user.getProperty("shootRange");
        int bulletpic = (int) user.getProperty("shootBulletpic");
        double bulletspeed = user.getProperty("shootBulletspeed");
        double spread = user.getProperty("shootSpread");
        double explosionradius = user.getProperty("shootExplosionradius");

//        if (user.getAttackCooldownTick()
//                <= Server.game.getTick()) {
//            user.setAttackCooldownTick(Server.game.getTick() + (int) attackspeed);
        Random random = new Random();
        dir += random.nextGaussian() * spread;
        int lifetime = (int) (range / bulletspeed);

        Bullet bullet = new Bullet(Server.game.getTick(), lifetime, user.getX(), user.getY(), dir, bulletspeed, bulletpic, Server.game.newNetID(), user);
        if (explosionradius > 0) {
            bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
        } else {
            bullet.addEffect(new TrueDamageEffect((int) damage));
        }

        Server.game.netIDMap.put(bullet.netID, bullet);
//        }
    }

    /**
     * Lässt einen Char ein Bullet auf ein Ziel schießen.
     *
     * @param user der Char, der diese Fähigkeit verwendet
     * @param targetX X-Koordinate des Ziels
     * @param targetY Y-Koordinate des Ziels
     * @deprecated
     */
    public static void fireBulletInAngle(Char user, double dir) {


        double damage = user.getProperty("shootDamage");
        double range = user.getProperty("shootRange");
        int bulletpic = (int) user.getProperty("shootBulletPic");
        double bulletspeed = user.getProperty("shootBulletSpeed");
        double spread = user.getProperty("shootSpread");
        double explosionradius = user.getProperty("shootExplosionRadius");

//        if (user.getAttackCooldownTick()
//                <= Server.game.getTick()) {
//            user.setAttackCooldownTick(Server.game.getTick() + (int) attackspeed);
        Random random = new Random();
        dir += random.nextGaussian() * spread;
        int lifetime = (int) (range / bulletspeed);

        Bullet bullet = new Bullet(Server.game.getTick(), lifetime, user.getX(), user.getY(), dir, bulletspeed, bulletpic, Server.game.newNetID(), user);
        if (explosionradius > 0) {
            bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
        } else {
            bullet.addEffect(new TrueDamageEffect((int) damage));
        }

        Server.game.netIDMap.put(bullet.netID, bullet);
//        }
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(Char user, double angle) {
        fireBulletInAngle(user, angle);
    }
}
