package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.ExplosionDamageEffect;
import de._13ducks.spacebatz.server.data.effects.StandardDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Die Fähigkeit, mehrere Bullets auf einmal zu schießen. Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author jjkk
 */
public class FireMultipleBulletAbility extends WeaponAbility {

    private static final long serialVersionUID = 1L;
    /**
     * Anzahl Bullets pro Schuss, fester Wert, wird nicht geändert
     */
    private int amount;

    /**
     * Legt eine FireBulletAbility an
     *
     * @param damage der Schaden des Bullets
     * @param damagespread Um wieviel der zufällige Schaden vom Mittelwert abweichen darf
     * @param attackspeed Angriffsgeschwindigkeit in Schüsse / Tick
     * @param range Reichweite der Waffe
     * @param bulletpic Bild des Bullets
     * @param bulletspeed Geschwindigkeit des Bullets
     * @param amount Anzahl Bullets, die verschossen werden
     * @param spread Streuung der Bullets
     * @param explosionradius Flächenschaden
     * @param maxoverheat Wie oft die Waffe schiessen kann, bis sie überhitzt ist
     * @param reduceoverheat
     */
    public FireMultipleBulletAbility(double damage, double damagespread, double attackspeed, double range, int bulletpic, double bulletspeed, int amount, double spread, double explosionradius, double maxoverheat, double reduceoverheat) {
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
        this.amount = amount;
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        double angle = GeoTools.toAngle(x - user.getX(), y - user.getY());
        STC_CHAR_ATTACK.sendCharAttack(user.netID, (float) angle, false);

        double range = getWeaponStats().getRange();
        int bulletpic = getWeaponStats().getBulletpic();
        double bulletspeed = getWeaponStats().getBulletspeed();
        double spread = getWeaponStats().getSpread();
        double explosionradius = getWeaponStats().getExplosionRadius();
        int lifetime = (int) (range / bulletspeed);

        for (int i = 1; i <= amount; i++) {
            int damagespread = (int) ((getWeaponStats().getDamagespread() * 2 + 2) * Math.random() - getWeaponStats().getDamagespread() - 1);
            int damage = (int) ((getWeaponStats().getDamage() + damagespread) * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getWeaponStats().getDamageMultiplicatorBonus()));
            double newangle = angle + (i - (amount + 1) / 2.0) * spread;

            double spawnX = user.getX() + getWeaponStats().getAttackOffset() * Math.cos(newangle);
            double spawnY = user.getY() + getWeaponStats().getAttackOffset() * Math.sin(newangle);

            //Vector newTarget = new Vector(user.getX(), user.getY()).add(new Vector(newangle).multiply(GeoTools.getDistance(x, y, user.getX(), user.getY())));
            double dist = new Vector(x - spawnX, y - spawnY).length();

            Vector newTarget = new Vector(spawnX, spawnY).add(new Vector(newangle).multiply(dist));

            Bullet bullet = new Bullet(lifetime, spawnX, spawnY, newTarget.x, newTarget.y, bulletspeed, bulletpic, Server.game.newNetID(), user);

            if (explosionradius > 0) {
                bullet.addEffect(new ExplosionDamageEffect(damage, explosionradius));
            }
            bullet.addEffect(new StandardDamageEffect(damage));

            Server.game.getEntityManager().addEntity(bullet.netID, bullet);
        }
    }
}