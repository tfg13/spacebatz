package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.*;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Distance;
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
     * Berechnet Kollisionen zwischen Bullets und Chars
     */
    private static void computeBulletCollision() {

        Iterator<Entity> listIterator = Server.game.netIDMap.values().iterator();

        while (listIterator.hasNext()) {
            Bullet bullet = null;
            Entity entity = listIterator.next();
            if (entity instanceof Bullet) {
                bullet = (Bullet) entity;
            } else {
                continue;
            }


            // Bullet muss nach bestimmter Zeit gelöscht werden
            if (Server.game.getTick() > bullet.getDeletetick()) {
                listIterator.remove();

                continue;
            }

            double x = bullet.getX();
            double y = bullet.getY();

            Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (e instanceof Char) {
                    Char c = (Char) e;
                    if (Math.abs(x - c.getX()) < 0.7 && Math.abs(y - c.getY()) < 0.7) {
                        if (!c.equals(bullet.getOwner())) {
                            if (c instanceof Enemy) {
                                Enemy en = (Enemy) c;
                                // Schaden von HP abziehen
                                if (en.decreaseHealthpoints(bullet)) {
                                } else {
                                    if (en.getMyTarget() == null) {
                                        en.setMyTarget(bullet.getOwner());
                                    }
                                }
                                // Flächenschaden
                                if (bullet.getExplosionradius() > 0) {
                                    computeBulletExplosionCollision(bullet, c);
                                }
                            }
                            listIterator.remove();


                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Berechnet Flächenschaden von Bullet-Explosionen
     * Bekommt den Char übergeben, den es direkt getroffen hat, damit dieser nicht nochmal Schaden kriegt
     */
    private static void computeBulletExplosionCollision(Bullet bullet, Char charhit) {
        double x = bullet.getX();
        double y = bullet.getY();

        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char c = (Char) e;
                if (c == charhit) {
                    continue;
                }
                double distance = Math.sqrt((x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()));
                if (distance < bullet.getExplosionradius()) {
                    if (!c.equals(bullet.getOwner())) {
                        if (c instanceof Enemy) {
                            Enemy en = (Enemy) c;
                            // Schaden von HP abziehen, wird von Distanz verringert
                            if (en.decreaseHealthpoints(bullet, 1.0 - distance / bullet.getExplosionradius() * 2 / 3)) {
                            } else {
                                if (en.getMyTarget() == null) {
                                    en.setMyTarget(bullet.getOwner());
                                }
                            }
                        }
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
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char mover = (Char) e;
                if (mover.isMoving()) {
                    double futureX = mover.extrapolateX(1);
                    double futureY = mover.extrapolateY(1);

                    int leftX = (int) (futureX - mover.getSize());
                    int leftY = (int) (futureY);

                    int topX = (int) (futureX);
                    int topY = (int) (futureY + mover.getSize());

                    int rightX = (int) (futureX + mover.getSize());
                    int rightY = (int) (futureY);

                    int botX = (int) (futureX);
                    int botY = (int) (futureY - mover.getSize());



                    if (Server.game.getLevel().getCollisionMap()[leftX][leftY] == true) {
                        mover.stopMovement();
                    }
                    if (Server.game.getLevel().getCollisionMap()[topX][topY] == true) {
                        mover.stopMovement();
                    }
                    if (Server.game.getLevel().getCollisionMap()[rightX][rightY] == true) {
                        mover.stopMovement();
                    }
                    if (Server.game.getLevel().getCollisionMap()[botX][botY] == true) {
                        mover.stopMovement();
                    }
                }
            }
        }
    }

    /**
     * Berechnet Kollision mit Mobs
     */
    private static void computeMobCollission() {
        // Alle Chars, die sich bewegen auf Kollision prüfen:
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char mover = (Char) e;
                if (mover instanceof Player) {
                    Iterator<Entity> iter2 = Server.game.netIDMap.values().iterator();
                    while (iter2.hasNext()) {
                        Entity e2 = iter2.next();
                        if (e2 instanceof Enemy) {
                            Enemy mob = (Enemy) e2;
                            double distance = Distance.getDistance(mover.getX(), mover.getY(), mob.getX(), mob.getY());
                            if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                                mob.attack(mover);
                            }
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
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Player) {
                Player mover = (Player) e;
                Iterator<Item> iterator = Server.game.getItemMap().values().iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    double distance = Distance.getDistance(mover.getX(), mover.getY(), item.getPosX(), item.getPosY());
                    if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                        if (item.getStats().itemStats.get("name").equals("Money")) {
                            mover.getClient().getInventory().setMoney(mover.getClient().getInventory().getMoney() + item.getAmount());
                            iterator.remove();
                            Server.msgSender.sendItemGrab(item.getNetID(), mover.getClient().clientID);
                        } else if (mover.getClient().getInventory().getItems().size() < Settings.INVENTORY_SIZE) {
                            mover.getClient().getInventory().putItem(item.getNetID(), item);
                            iterator.remove();
                            Server.msgSender.sendItemGrab(item.getNetID(), mover.getClient().clientID);
                        }
                    }
                }
            }
        }
    }
}
