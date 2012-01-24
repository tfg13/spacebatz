package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps
     */
    private int enemytypeid = 1;
    /**
     * Der Char, den dieser Enemy gerade verfolgt
     */
    private Char myTarget;

    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     */
    public Enemy(double x, double y, int id) {
        super(x, y, id);
        speed = .045;
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
        return enemytypeid;
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
            return true;
        } else {
            Server.msgSender.sendHitChar(netID, netIDBullet, false);
            return false;
        }
    }
}
