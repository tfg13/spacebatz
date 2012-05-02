package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.Properties;
import java.util.Random;

/**
 * Die Fähigkeit, Bullets zu schießen.
 * Erzeugt neue Bullets und regisriert sie beim Server.
 *
 * @author michael
 */
public class FireBulletAbility extends Ability {

    /**
     * Erzeugt eine neue FireBulletAbility mit den angegebenen Grundwerten.
     *
     * @param damage der angerichtete Schaden
     * @param attackspeed der Cooldown in Ticks
     * @param range die Reichweite der Bullets
     * @param bulletpic das Bild der Bullets
     * @param bulletspeed die Geschwindigkeit der Bullets
     * @param spread die Streuung der Bullets
     * @param explosionradius der Explosionsradius der Bullets
     */
    public FireBulletAbility(double damage, double attackspeed, double range, int bulletpic, double bulletspeed, double spread, double explosionradius) {
        // Grundwerte:
        addBaseProperty("damage", damage);
        addBaseProperty("attackspeed", attackspeed);
        addBaseProperty("range", range);
        addBaseProperty("bulletpic", bulletpic);
        addBaseProperty("bulletspeed", bulletspeed);
        addBaseProperty("spread", spread);
        addBaseProperty("explosionradius", explosionradius);

        // Wirkliche Werte (im moment die Grundwerte, da das Item noch nicht ausgerüstet ist.):
        setActualProperty("damage", damage);
        setActualProperty("attackspeed", attackspeed);
        setActualProperty("range", range);
        setActualProperty("bulletpic", bulletpic);
        setActualProperty("bulletspeed", bulletspeed);
        setActualProperty("spread", spread);
        setActualProperty("explosionradius", explosionradius);
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

        

        double damage = getProperty("damage");
        double attackspeed = getProperty("attackspeed");
        double range = getProperty("range");
        int bulletpic = (int) getProperty("bulletpic");
        double bulletspeed = getProperty("bulletspeed");
        double spread = getProperty("spread");
        double explosionradius = getProperty("explosionradius");

        if (owner.getAttackCooldownTick()
                <= Server.game.getTick()) {
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
        // Die basestats mal 1 + multiplikator ergeben die wirklichen werte:
        setActualProperty("damage", getBaseProperty("damage") * (1 + properties.getProperty("damagemultiplicator")));
        setActualProperty("range", getBaseProperty("range") * (1 + properties.getProperty("rangemultiplicator")));
        setActualProperty("bulletspeed", getBaseProperty("bulletspeed") * (1 + properties.getProperty("bulletspeedmultiplicator")));
        setActualProperty("attackspeed", getBaseProperty("attackspeed") * (1 + properties.getProperty("attackspeedmultiplicator")));
        setActualProperty("spread", getBaseProperty("spread") * (1 + properties.getProperty("spreadmultiplicator")));
        setActualProperty("explosionradius", getBaseProperty("explosionradius") * (1 + properties.getProperty("explosionradiusmultiplicator")));
    }
    
    
}
