package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Bits;
import java.util.Random;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Die normale Geschwindigkeit dieses Players.
     */
    private double speed = .2;

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann nur einen Player pro Client geben.
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     * @param client der Client, dem dieser Player gehören soll.
     */
    public Player(double x, double y, int id, Client client) {
        super(x, y, id);
        client.setPlayer(this);
    }

    /**
     * Ein move-Request vom Client ist eingegangen. Teil der Netz-Infrastruktur, muss schnell verarbeitet werden
     *
     * @param w W-Button gedrückt.
     * @param a A-Button gedrückt.
     * @param s S-Button gedrückt.
     * @param d D-Button gedrückt.
     */
    public void clientMove(boolean w, boolean a, boolean s, boolean d, boolean space) {
        if (w) {
            setStillY(posY + speed);
        }
        if (a) {
            setStillX(posX - speed);
        }
        if (s) {
            setStillY(posY - speed);
        }
        if (d) {
            setStillX(posX + speed);
        }
        if (space) {
            int thistick = Server.game.getTick();
            System.out.println("this " + thistick + " attack " + AttackCooldownTick);
            if (thistick > AttackCooldownTick + 20) {
                Random random = new Random(System.nanoTime());

                Bullet bullet = new Bullet(thistick, posX, posY, random.nextGaussian() * Math.PI / 16, 0.15f, Server.game.newNetID());
                Server.game.bullets.add(bullet);
                byte[] bytearray = new byte[25];

                bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
                Bits.putInt(bytearray, 1, bullet.getSpawntick());
                Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
                Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
                Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
                Bits.putFloat(bytearray, 17, bullet.getSpeed());
                Bits.putInt(bytearray, 21, bullet.getNetID());

                for (int i = 0; i < Server.game.clients.size(); i++) {
                    Server.serverNetwork.udp.sendPack(bytearray, Server.game.clients.get(i));
                }
                AttackCooldownTick = thistick;
            }
        }
    }
}
