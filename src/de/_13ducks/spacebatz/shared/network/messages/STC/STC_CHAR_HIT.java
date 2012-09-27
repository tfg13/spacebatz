package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
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

        if (GameClient.netIDMap.get(netIDVictim) instanceof PlayerCharacter) {
            // HP abziehen, wenn eigener Spieler
            PlayerCharacter p = (PlayerCharacter) GameClient.netIDMap.get(netIDVictim);
            if (p == GameClient.getPlayer()) {

                if (victimdies == 1) {

                    // Weil es noch keinen richtigen Respawn gibt, werden die HP hier wieder hochgesetzt
                    p.setHealthpoints(p.getHealthpointsmax());
                } else {
                    p.setHealthpoints(p.getHealthpoints() - damage);
                }
            }
        } else if (GameClient.netIDMap.get(netIDVictim) instanceof Enemy) {
            Enemy e = (Enemy) GameClient.netIDMap.get(netIDVictim);
            // Schadenszahl rendern:
            Engine.createDamageNumber(damage, e.getX(), e.getY());
            // Test-Explosion:
            Animation anim = new Animation(0, 2, 2, 3, 4);
            Engine.addFx(new Fx(anim, e.getX(), e.getY(), 12));
        }
    }

    /**
     * Bullet/Mob-Angriff trifft Char
     *
     * @param netIDVictim netID des getroffenen Char
     * @param netIDAttacker netID des Bullets / Enemy
     * @param killed Ob Char getötet wird
     */
    public static void sendCharHit(int netIDVictim, int damage, boolean killed) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[9];
            Bits.putInt(b, 0, netIDVictim);
            Bits.putInt(b, 4, damage);
            if (killed) {
                b[8] = 1;
            } else {
                b[8] = 0;
            }
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHAR_HIT, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CHAR_HIT, b), c);
        }
    }
}
