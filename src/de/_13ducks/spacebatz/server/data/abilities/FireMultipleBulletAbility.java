package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.ExplosionDamageEffect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(Char user, double angle) {

        double range = getWeaponStats().getRange();
        int bulletpic = getWeaponStats().getBulletpic();
        double bulletspeed = getWeaponStats().getBulletspeed();
        double spread = getWeaponStats().getSpread();
        double explosionradius = getWeaponStats().getExplosionRadius();
        int lifetime = (int) (range / bulletspeed);

        for (int i = 1; i <= amount; i++) {
            double damage = (getWeaponStats().getDamage() + getWeaponStats().getDamagespread() * 2 * (Math.random() - 0.5)) * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getWeaponStats().getDamageMultiplicatorBonus());
            double newangle = angle + (i - (amount + 1) / 2.0) * spread;

            Bullet bullet = new Bullet(lifetime, user.getX(), user.getY(), newangle, bulletspeed, bulletpic, Server.game.newNetID(), user);

            if (explosionradius > 0) {
                bullet.addEffect(new ExplosionDamageEffect((int) damage, explosionradius));
            }
            bullet.addEffect(new TrueDamageEffect((int) damage));

            Server.game.getEntityManager().addEntity(bullet.netID, bullet);
        }
    }
}