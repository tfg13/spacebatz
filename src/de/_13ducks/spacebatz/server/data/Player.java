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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.shared.Item;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Der Spielercharakter
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

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

        addAbility(Ability.SHOOT, new FireBulletAbility());
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
     * Lässt den Player seine "Shoot"-Fähigkeit einsetzen
     *
     * @param angle der Winkel in dem der Player schießen soll
     */
    public void playerShoot(float angle) {
        useAbilityInAngle(Ability.SHOOT, angle);
    }

    /**
     * Item in leeren Slot anlegen
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
}
