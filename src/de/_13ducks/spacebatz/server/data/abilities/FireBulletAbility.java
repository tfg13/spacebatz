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
public class FireBulletAbility extends WeaponAbility {

    private static final long serialVersionUID = 1L;

    public FireBulletAbility(double damage, double attackspeed, double range, int bulletpic, double bulletspeed, double spread, double explosionradius, double maxoverheat, double reduceoverheat) {
        setDamage(damage);
        setAttackspeed(attackspeed);
        setRange(range);
        setBulletpic(bulletpic);
        setBulletspeed(bulletspeed);
        setSpread(spread);
        setExplosionRadius(explosionradius);
        setMaxoverheat(maxoverheat);
        setReduceoverheat(reduceoverheat);
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(Char user, double angle) {

        double damage = getDamage() * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getDamageMultiplicatorBonus());
        double range = getRange();
        int bulletpic = getBulletpic();
        double bulletspeed = getBulletspeed();
        double spread = getSpread();
        double explosionradius = getExplosionRadius();

        Random random = new Random();
        angle += random.nextGaussian() * spread;
        int lifetime = (int) (range / bulletspeed);

        Bullet bullet = new Bullet(lifetime, user.getX(), user.getY(), angle, bulletspeed, bulletpic, Server.game.newNetID(), user);
        bullet.addEffect(new TrueDamageEffect((int) damage));
        if (explosionradius > 0) {
            bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
        }
        Server.game.getEntityManager().addEntity(bullet.netID, bullet);

    }
}