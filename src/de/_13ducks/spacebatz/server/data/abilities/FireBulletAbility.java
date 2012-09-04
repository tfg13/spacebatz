package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.ExplosionDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import java.util.Random;

/**
 * Die Fähigkeit, Bullets zu schießen. Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility extends Ability {

    private static final long serialVersionUID = 1L;

    public FireBulletAbility(double damage, double attackspeed, double range, int bulletpic, double bulletspeed, double spread, double explosionradius) {
        setDamage(damage);
        setAttackspeed(attackspeed);
        setRange(range);
        setBulletpic(bulletpic);
        setBulletspeed(bulletspeed);

        setSpread(spread);
        setExplosionRadius(explosionradius);

    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(Char user, double angle) {

        double damage = getDamage();
        double attackspeed = getAttackspeed();
        double range = getRange();
        int bulletpic = (int) getBulletpic();
        double bulletspeed = getBulletspeed();
        double spread = getSpread();
        double explosionradius = getExplosionRadius();



        if (user.attackCooldownTick <= Server.game.getTick()) {
            user.attackCooldownTick = (Server.game.getTick() + (int) attackspeed);
            Random random = new Random();
            angle += random.nextGaussian() * spread;
            int lifetime = (int) (range / bulletspeed);

            Bullet bullet = new Bullet(Server.game.getTick(), lifetime, user.getX(), user.getY(), angle, bulletspeed, bulletpic, Server.game.newNetID(), user);
            //bullet.addEffect(new TrueDamageEffect((int) damage));
            bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
            Server.game.getEntityManager().netIDMap.put(bullet.netID, bullet);
        }
    }
}