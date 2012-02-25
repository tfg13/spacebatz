package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Item;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends Char {

    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;
    /*
     * Die möglichen Angriffe des Spielers (werden durch Waffen bestimmt)
     */
    private PlayerAttack[] attack = new PlayerAttack[3]; // # Waffenslots
    /*
     * Nummer des Angriffs, der zurzeit ausgewählt ist
     */
    private int selectedattack = 0;

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
        if (thistick > attackcooldowntick + attack[selectedattack].getAttackcooldown()) {
            attackcooldowntick = thistick;

            Bullet bullet = new Bullet(thistick, getX(), getY(), angle, 0, Server.game.newNetID(), this);
            Server.game.bullets.add(bullet);

            Server.game.netIDMap.put(bullet.netID, bullet);
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
        double newmovespeed = Settings.CHARSPEED;

        for (int w = 0; w <= 2; w++) {

            ArrayList<Item> checkitems = new ArrayList<>();

            // Eine der Waffen hinzufügen
            checkitems.add(this.getClient().getEquippedItems().getEquipslots()[1][w]);

            // Alle Equip-Items hinzufügen
            for (int i = 2; i < this.getClient().getEquippedItems().getEquipslots().length; i++) {
                checkitems.addAll(Arrays.asList(this.getClient().getEquippedItems().getEquipslots()[i]));
            }

            for (int i = 0; i < checkitems.size(); i++) {
                Item item = checkitems.get(i);
                if (item == null) {
                    continue;
                }
                newdamage += (int) item.getStats().itemStats.get("damage");
                if ((double) item.getStats().itemStats.get("attackcooldown") != 0) {
                    newattackcooldown = (double) item.getStats().itemStats.get("attackcooldown");
                }
                if ((double) item.getStats().itemStats.get("range") != 0) {
                    newrange = (double) item.getStats().itemStats.get("range");
                }
                newarmor += (int) item.getStats().itemStats.get("armor");
                for (int k = 0; k < item.getItemattributes().size(); k++) {
                    for (String astatname : item.getItemattributes().get(k).getStats().keySet()) {
                        double astatval = item.getItemattributes().get(k).getStats().get(astatname);
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
                                newmovespeed *= (1 + astatval);
                                break;
                        }
                    }
                }
            }

            speed = newmovespeed;
            armor = newarmor;

            newdamage *= newdamagemulti;
            newdamage = Math.max(newdamage, 2);
            newattackcooldown /= newattackspeedmulti;

            PlayerAttack playerattack = new PlayerAttack(newdamage, (int) newattackcooldown, newrange);
            attack[w] = playerattack;
        }
    }

    /**
     * @return den gerade ausgewählten Angriff
     */
    public PlayerAttack getSelectedAttack() {
        return attack[selectedattack];
    }

    public void selectAttack(byte select) {
        this.selectedattack = select;
    }

    /**
     * Zieht Schadenspunkte von HP ab, returned true wenn Einheit stirbt
     *
     * @param Entity, das den Schaden anrichtet
     * @return true, wenn Enemy stirbt, sonst false
     */
    @Override
    public boolean decreaseHealthpoints(Entity e) {
        if (e instanceof Enemy) {
            Enemy enemy = (Enemy) e;
            healthpoints -= enemy.getDamage();

            if (healthpoints <= 0) {
                Server.msgSender.sendCharHit(netID, e.netID, true);
                setStillX(Server.game.getLevel().respawnX);
                setStillY(Server.game.getLevel().respawnY);
                healthpoints = healthpointsmax;
                return true;
            } else {
                Server.msgSender.sendCharHit(netID, e.netID, false);
                return false;
            }
        } else {
            return false;
        }
    }
}
