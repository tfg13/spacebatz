package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.ExplosionDamageEffect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.Random;

/**
 * Die Fähigkeit, Bullets zu schießen. Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility extends WeaponAbility {

    private static final long serialVersionUID = 1L;

    /**
     * Legt eine FireBulletAbility an
     *
     * @param damage der Schaden des Bullets
     * @param damagespread Um wieviel der zufällige Schaden vom Mittelwert abweichen darf
     * @param attackspeed Angriffsgeschwindigkeit in Schüsse / Tick
     * @param range Reichweite der Waffe
     * @param bulletpic Bild des Bullets
     * @param bulletspeed Geschwindigkeit des Bulltes
     * @param spread Streuung der Bullets
     * @param explosionradius Flächenschaden
     * @param maxoverheat Wie oft die Waffe schiessen kann, bis sie überhitzt ist
     * @param reduceoverheat
     */
    public FireBulletAbility(double damage, double damagespread, double attackspeed, double range, int bulletpic, double bulletspeed, double spread, double explosionradius, double maxoverheat, double reduceoverheat, boolean hitEnemies) {
        getWeaponStats().setDamage(damage);
        getWeaponStats().setDamagespread(damagespread);
        getWeaponStats().setAttackspeed(attackspeed);
        getWeaponStats().setRange(range);
        getWeaponStats().setBulletpic(bulletpic);
        getWeaponStats().setBulletspeed(bulletspeed);
        getWeaponStats().setSpread(spread);
        getWeaponStats().setExplosionRadius(explosionradius);
        getWeaponStats().setMaxoverheat(maxoverheat);
        getWeaponStats().setReduceoverheat(reduceoverheat);
    }

    @Override
    public void useOnPosition(Char user, double targetX, double targetY) {
        double angle = GeoTools.toAngle(targetX - user.getX(), targetY - user.getY());

        double damage = (getWeaponStats().getDamage() + getWeaponStats().getDamagespread() * 2 * (Math.random() - 0.5)) * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getWeaponStats().getDamageMultiplicatorBonus());
        double range = getWeaponStats().getRange();
        int bulletpic = getWeaponStats().getBulletpic();
        double bulletspeed = getWeaponStats().getBulletspeed();
        double spread = getWeaponStats().getSpread();
        double explosionradius = getWeaponStats().getExplosionRadius();

        Random random = new Random();
        angle += random.nextGaussian() * spread;
        int lifetime = (int) (range / bulletspeed);

        double startX = user.getX() + getWeaponStats().getAttackOffset() * Math.cos(angle);
        double startY = user.getY() + getWeaponStats().getAttackOffset() * Math.sin(angle);

        double dist = new Vector(targetX - startX, targetY - startY).length();

        Vector newTarget = new Vector(startX, startY).add(new Vector(angle).multiply(dist));

        Bullet bullet = new Bullet(lifetime, startX, startY, newTarget.x, newTarget.y, bulletspeed, bulletpic, Server.game.newNetID(), user);


        if (explosionradius > 0) {
            bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
        }
        bullet.addEffect(new TrueDamageEffect((int) damage));

        Server.game.getEntityManager().addEntity(bullet.netID, bullet);
        STC_CHAR_ATTACK.sendCharAttack(user.netID, (float) angle, false);
    }
}