/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Der Spielercharakter.
 * Verwaltet die Interaktion des Clients mit der Spielwelt.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

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

        setProperty("canShoot", 1);
        setProperty("shootDamage", 10);
        setProperty("shootRange", 10);
        setProperty("shootSpread", 0.25);
        setProperty("shootBulletSpeed", 0.1);
        setProperty("shootBulletPic", 1);
        setProperty("shootExplosionRadius", 0.0);
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

    /**
     * Lässt den Player seine "Shoot"-Fähigkeit auf ein Ziel einsetzen
     *
     * @param angle der Winkel in dem die Fähigkeit benutzt wird
     *
     */
    public void playerShoot(double angle) {
        useAbilityInAngle(0, angle);
    }

    /**
     * Item in leeren Slot anlegen
     *
     * @param itemnetID NetID des Items
     * @param selectedslot ausgewählter Slot
     */
    public void clientEquipItem(int itemnetID, byte selectedslot) {
        Item item = getItems().get(itemnetID);

        // Item anlegen
        if (equipItem(itemnetID, selectedslot)) {
            // Wenn erfolgreich, an Client senden
            Server.msgSender.sendItemEquip(item.getNetID(), selectedslot, getClient().clientID);
        }
    }

    /**
     * Item ablegen
     *
     * @param slottype Slotart (Waffe, Hut, ...)
     * @param selectedslot Nr. des Slots dieser Art
     */
    public void clientDequipItem(int slottype, byte selectedslot) {
        if (freeInventorySlot()) {
            // Item ins Inventar tun:
            if (dequipItemToInventar(slottype, selectedslot)) {
                Server.msgSender.sendItemDequip(slottype, selectedslot, (byte) 0, getClient().clientID);
            }
        } else {
            // Item zu Poden werfen:
            Item item = dequipItemToGround(slottype, selectedslot);

            if (item != null) {
                Server.msgSender.sendItemDequip(slottype, selectedslot, (byte) 1, getClient().clientID);
                item.setPosX(getX());
                item.setPosY(getY());
                Server.game.getItemMap().put(item.getNetID(), item);
                byte[] serializedItem = null;
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ObjectOutputStream os;
                try {
                    os = new ObjectOutputStream(bs);
                    os.writeObject(item);
                    os.flush();
                    bs.flush();
                    bs.close();
                    os.close();
                    serializedItem = bs.toByteArray();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Server.msgSender.sendItemDrop(serializedItem);
            }

        }
    }

    /**
     * Wählt gerade aktive Waffe aus
     *
     * @param selectedslot aktiver Waffenslot (0 bis 2)
     */
    public void clientSelectWeapon(byte selectedslot) {
        if (setSelectedweapon(selectedslot)) {
            Server.msgSender.sendWeaponswitch(this.getClient(), selectedslot);
        }
    }
}
