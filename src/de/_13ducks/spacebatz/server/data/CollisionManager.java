package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Distance;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Berechnet Kollisionen zwischen Chars, Enemys und Bullets.
 *
 * @author michael
 */
public class CollisionManager {

    /**
     * Berechnet Kollision für Bullets
     *
     * @param chars die Liste der Chars, für die Kollision berechnet werden soll
     * @param bullets die Liste der Bullets, deren Kollisionen berechnet werden sollen
     */
    public static void computeCollision() {
        computeBulletCollision();
        computeWallCollision();
        computeMobCollission();
        computeItemCollission();

    }

    /**
     * Berechnet Kollisionen zwischen Bullets und Cahrs
     */
    private static void computeBulletCollision() {
        ArrayList<Bullet> bullets = Server.game.bullets;
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            // Bullet muss nach bestimmter Zeit gelöscht werden
            if (Server.game.getTick() > bullet.getDeletetick()) {
                bullets.remove(i);
                i--;
                continue;
            }

            float radius = bullet.getSpeed() * (Server.game.getTick() - bullet.getSpawntick());
            float x = (float) bullet.getSpawnposX() + radius * (float) Math.cos(bullet.getDirection());
            float y = (float) bullet.getSpawnposY() + radius * (float) Math.sin(bullet.getDirection());

            Iterator<Char> iter = Server.game.netIDMap.values().iterator();
            while (iter.hasNext()) {
                Char c = iter.next();
                if (Math.abs(x - c.posX) < 0.7 && Math.abs(y - c.posY) < 0.7) {
                    if (!c.equals(bullet.getOwner())) {
                        if (c instanceof Enemy) {
                            Enemy e = (Enemy) c;
                            // Schaden von HP abziehen
                            if (e.decreaseHealthpoints(bullets.get(i))) {
                            } else {
                                if (e.getMyTarget() == null) {
                                    e.setMyTarget(bullet.getOwner());
                                }
                            }
                        }
                        // Testcode: Bullet kann nur einen Gegner treffen
                        bullets.remove(i);
                        i--;
                        break;
                    }

                }
            }
        }
    }

    /**
     * Berechnet Kollisionen zwischen Wänden und Chars
     */
    private static void computeWallCollision() {
        // Alle Chars, die sich bewegen auf Kollision prüfen:
        Iterator<Char> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Char mover = iter.next();
            if (mover.isMoving()) {
                double futureX = mover.posX + mover.vecX * mover.getSpeed() * (Server.game.getTick() + 1 - mover.moveStartTick);
                double futureY = mover.posY + mover.vecY * mover.getSpeed() * (Server.game.getTick() + 1 - mover.moveStartTick);


                if (Server.game.getLevel().getCollisionMap()[(int) futureX][(int) futureY] == true) {
                    mover.stopMovement();
                }

            }
        }
    }

    /**
     * Berechnet Kollision mit Mobs
     */
    private static void computeMobCollission() {
        // Alle Chars, die sich bewegen auf Kollision prüfen:
        Iterator<Char> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Char mover = iter.next();
            if (mover instanceof Player) {
                Iterator<Char> iter2 = Server.game.netIDMap.values().iterator();
                while (iter2.hasNext()) {
                    Char mob = iter2.next();
                    if (mob instanceof Enemy) {
                        double distance = Distance.getDistance(mover.getX(), mover.getY(), mob.getX(), mob.getY());
                        if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                            mover.setStillX(Server.game.getLevel().respawnX);
                            mover.setStillY(Server.game.getLevel().respawnY);
                        }
                    }
                }
            }

        }
    }

    /**
     * Berechnet Kollision mit Items
     */
    private static void computeItemCollission() {
        Iterator<Char> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Char mover = iter.next();
            if (mover instanceof Player) {
                for (int j = 0; j < Server.game.items.size(); j++) {
                    Item item = Server.game.items.get(j);

                    double distance = Distance.getDistance(mover.getX(), mover.getY(), item.getPosX(), item.getPosY());
                    if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                        Player player = (Player) mover;
                        player.getClient().getInventory().addItem(item);
                        Server.game.items.remove(j);
                        j--;
                        Server.msgSender.sendItemGrab(item.netID, player.getClient().clientID);
                    }
                }
            }
        }
    }
}
