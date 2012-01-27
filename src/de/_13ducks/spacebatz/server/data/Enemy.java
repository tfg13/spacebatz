package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.EnemyTypeStats;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps
     */
    private int enemytypeID = 0;
    /**
     * Der Char, den dieser Enemy gerade verfolgt
     */
    private Char myTarget;

    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param netid netID, nicht mehr änderbar.
     * @param typeid typeID gibt Gegnertyp an
     */
    public Enemy(double x, double y, int netid, int enemytypeID) {
        super(x, y, netid, (byte) 3);
        speed = .045;
        this.enemytypeID = enemytypeID;
        EnemyTypeStats estats = Server.game.enemytypes.getEnemytypelist().get(enemytypeID);
        this.healthpoints = estats.getHealthpoints();
        this.damage = estats.getDamage();
        this.speed = estats.getSpeed();
        this.sightrange = estats.getSightrange();
        this.pictureID = estats.getPicture();
        
    }

    /**
     * Gibt den Char, den dieser Enemy gerade verfolgt, zurück.
     * @return der Char der gerade verfolgt wird
     */
    public Char getMyTarget() {
        return myTarget;
    }

    /**
     * Setzt den Char, den dieser Enemy gerade verfolgt.
     * @param der Char den dieser Enemy verfolgen soll
     */
    public void setMyTarget(Char myTarget) {
        this.myTarget = myTarget;
    }

    /**
     * @return the enemytypeid
     */
    public int getEnemytypeid() {
        return enemytypeID;
    }

    /**
     * Zieht Schadenspunkte von HPab, returned true wenn Einheit stirbt
     * @param Schaden, der von Healthpoints abgezogen wird
     * @return true, wenn Enemy stirbt, sonst false
     */
    @Override
    public boolean decreaseHealthpoints(int netIDBullet) {
        for (int i = 0; i < Server.game.bullets.size(); i++) {
            if (Server.game.bullets.get(i).getNetID() == netIDBullet) {
                healthpoints -= Server.game.bullets.get(i).getDamage();
                break;
            }
        }

        if (healthpoints <= 0) {
            Server.msgSender.sendHitChar(netID, netIDBullet, true);
            Server.game.chars.remove(this);
            dropItem();
            return true;
        } else {
            Server.msgSender.sendHitChar(netID, netIDBullet, false);
            return false;
        }
    }
    
    public void dropItem() {
        Item item = new Item(getX(), getY(), (byte) 0, Server.game.newNetID());
        Server.game.items.add(item);
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 4;
    }

    @Override
    public void netPack(byte[] b, int offset) {
        super.netPack(b, offset);
        Bits.putInt(b, super.byteArraySize() + offset, enemytypeID);
    }
}
