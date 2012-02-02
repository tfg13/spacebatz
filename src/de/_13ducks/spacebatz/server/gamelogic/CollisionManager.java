package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Bullet;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Enemy;
import de._13ducks.spacebatz.server.data.Player;
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
                if (Math.abs(x - c.getX()) < 0.7 && Math.abs(y - c.getY()) < 0.7) {
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
                double futureX = mover.extrapolateX(1);
                double futureY = mover.extrapolateY(1);
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

                Iterator<Item> iterator = Server.game.getItemMap().values().iterator();

                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    double distance = Distance.getDistance(mover.getX(), mover.getY(), item.getPosX(), item.getPosY());
                    if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                        Player player = (Player) mover;
                        if (item.stats.itemStats.get("name").equals("Money")) {
                            player.getClient().getInventory().setMoney(player.getClient().getInventory().getMoney() + item.getAmount());
                            iterator.remove();
                            Server.msgSender.sendItemGrab(item.netID, player.getClient().clientID);
                        } else if (player.getClient().getInventory().getItems().size() < 96) {
                            player.getClient().getInventory().addItem(item);
                            iterator.remove();
                            Server.msgSender.sendItemGrab(item.netID, player.getClient().clientID);
                        }
                    }


                }
            }
        }
    }
}
