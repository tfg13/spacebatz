package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_CHAR_HIT extends FixedSizeSTCCommand {

    public STC_CHAR_HIT() {
        super(9);
    }

    @Override
    public void execute(byte[] data) {
        // Char wird von Bullet / angriff getroffen
        int netIDVictim = Bits.getInt(data, 0); // netID von dem, der getroffen wird
        int damage = Bits.getInt(data, 4);

        byte victimdies = data[8];

        if (Client.netIDMap.get(netIDVictim) instanceof Player) {
            // HP abziehen, wenn eigener Spieler
            Player p = (Player) Client.netIDMap.get(netIDVictim);
            if (p == Client.getPlayer()) {

                if (victimdies == 1) {

                    // Weil es noch keinen richtigen Respawn gibt, werden die HP hier wieder hochgesetzt
                    p.setHealthpoints(p.getHealthpointsmax());
                } else {
                    p.setHealthpoints(p.getHealthpoints() - damage);
                }
            }
        } else if (Client.netIDMap.get(netIDVictim) instanceof Enemy) {
            Enemy e = (Enemy) Client.netIDMap.get(netIDVictim);
            // Schadenszahl rendern:
            Engine.createDamageNumber(damage, e.getX(), e.getY());
            // Test-Explosion:
            Animation anim = new Animation(0, 2, 2, 3, 4);
            Engine.addFx(new Fx(anim, e.getX(), e.getY(), 12));
        }
    }
}
