package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;

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
        this.client = client;
        calcEquipStats();
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
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    public void playerShoot(float angle) {
        int thistick = Server.game.getTick();
        if (thistick > attackcooldowntick + attackcooldown) {
            attackcooldowntick = thistick;


            Bullet bullet = new Bullet(thistick, getX(), getY(), angle, 0, Server.game.newNetID(), this);
            Server.game.bullets.add(bullet);
            byte[] bytearray = new byte[25];

            bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
            Bits.putInt(bytearray, 1, bullet.getSpawntick());
            Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
            Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
            Bits.putFloat(bytearray, 13, (float) Math.atan2(bullet.getDirectionY(), bullet.getDirectionX()));
            Bits.putInt(bytearray, 17, bullet.getTypeID());
            Bits.putInt(bytearray, 21, bullet.netID);

            for (int i = 0; i < Server.game.clients.size(); i++) {
                Server.serverNetwork.udp.sendPack(bytearray, Server.game.clients.get(i));
            }
        }
    }

    /**
     * Berechnet Playerwerte (Schaden, ...) für die angelegten Items
     */
    public void calcEquipStats() {
        int newdamage = 0;
        double newdamagemulti = 1.0;
        double newattackcooldown = 10;
        double newattackspeedmulti = 1.0;
        double newrange = 10.0;
        int newarmor = 0;
        double newmovespeedmulti = Settings.CHARSPEED;

        for (int i = 1; i < this.getClient().getEquippedItems().length; i++) {
            Item item = this.getClient().getEquippedItems()[i];
            if (item != null) {
                newdamage += (int) item.getStats().itemStats.get("damage");
                if ((double) item.getStats().itemStats.get("attackcooldown") != 0) {
                    newattackcooldown = (double) item.getStats().itemStats.get("attackcooldown");
                }
                if ((double) item.getStats().itemStats.get("range") != 0) {
                    newrange = (double) item.getStats().itemStats.get("range");
                }
                newarmor += (int) item.getStats().itemStats.get("armor");
                for (int j = 0; j < item.getItemattributes().size(); j++) {
                    for (String astatname : item.getItemattributes().get(j).getStats().keySet()) {
                        double astatval = item.getItemattributes().get(j).getStats().get(astatname);
                        switch (astatname) {
                            case "damage":
                                newdamagemulti += astatval;
                                break;
                            case "attackspeed":
                                newattackspeedmulti += astatval;
                                break;
                            case "range":
                                newrange *= (1 + astatval);
                                break;
                            case "armor":
                                newarmor += astatval;
                                break;
                            case "movespeed":
                                newmovespeedmulti *= (1 + astatval);
                                break;
                        }
                    }
                }
            }
        }
        newdamage *= newdamagemulti;
        damage = Math.max(newdamage, 2);
        speed = newmovespeedmulti;
        attackcooldown = (int) (newattackcooldown / newattackspeedmulti);
        range = newrange;
        System.out.println("damage: " + damage + " acooldown: " + attackcooldown + " mspeed: " + speed + " range: " + getRange());

    }
}
