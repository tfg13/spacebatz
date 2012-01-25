package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann nur einen Player pro Client geben.
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     * @param client der Client, dem dieser Player gehören soll.
     */
    public Player(double x, double y, int id, Client client) {
        super(x, y, id, (byte) 2);
        client.setPlayer(this);
        client.getContext().makeCharKnown(netID);
    }

    /**
     * Ein move-Request vom Client ist eingegangen. Teil der Netz-Infrastruktur, muss schnell verarbeitet werden
     *
     * @param w W-Button gedrückt.
     * @param a A-Button gedrückt.
     * @param s S-Button gedrückt.
     * @param d D-Button gedrückt.
     */
    public void clientMove(boolean w, boolean a, boolean s, boolean d) {
        double x = 0, y = 0;

        if (w) {
            y += 1;
        }
        if (a) {
            x += -1;
        }
        if (s) {
            y += -1;
        }
        if (d) {
            x += 1;
        }
        // Sonderfall stoppen
        if (x == 0 && y == 0) {
            if (isMoving()) {
                stopMovement();
            }
        } else {
            // Bewegen wir uns zur Zeit schon (in diese Richtung)
            if (isMoving()) {
                double length = Math.sqrt((x * x) + (y * y));
                x /= length;
                y /= length;
                if (Math.abs(vecX - x) > .001 || Math.abs(vecY - y) > .001) {
                    this.setVector(x, y);
                }
            } else {
                setVector(x, y);
            }
        }
//        if (space) {
//            int thistick = Server.game.getTick();
//            if (thistick > AttackCooldownTick + 20) {
//                Random random = new Random(System.nanoTime());
//
//                Bullet bullet = new Bullet(thistick, posX, posY, random.nextGaussian() * Math.PI / 16, 0.15f, Server.game.newNetID());
//                Server.game.bullets.add(bullet);
//                byte[] bytearray = new byte[25];
//
//                bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
//                Bits.putInt(bytearray, 1, bullet.getSpawntick());
//                Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
//                Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
//                Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
//                Bits.putFloat(bytearray, 17, bullet.getSpeed());
//                Bits.putInt(bytearray, 21, bullet.getNetID());
//
//                for (int i = 0; i < Server.game.clients.size(); i++) {
//                    Server.serverNetwork.udp.sendPack(bytearray, Server.game.clients.get(i));
//                }
//                AttackCooldownTick = thistick;
//            }
//        }
    }

    public void clientShoot(float angle) {
        int thistick = Server.game.getTick();
        if (thistick > AttackCooldownTick + 5) {
            Server.game.fireBullet(this.getX(), this.getY(), angle);
            AttackCooldownTick = thistick;
        }
    }
}
