package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Distance;
import java.util.ArrayList;

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

    }

    /**
     * Berechnet Kollisionen zwischen Bullets und Cahrs
     */
    private static void computeBulletCollision() {
        ArrayList<Bullet> bullets = Server.game.bullets;
        ArrayList<Char> chars = Server.game.chars;
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            float radius = (float) bullet.getSpeed() * (Server.game.getTick() - bullet.getSpawntick());
            float x = (float) bullet.getSpawnposX() + radius * (float) Math.cos(bullet.getDirection());
            float y = (float) bullet.getSpawnposY() + radius * (float) Math.sin(bullet.getDirection());

            for (int j = 0; j < chars.size(); j++) {
                if (Math.abs(x - chars.get(j).posX) < 0.7 && Math.abs(y - chars.get(j).posY) < 0.7) {
                    if (chars.get(j) instanceof Enemy) {
                        System.out.println("KOLLISION " + Server.game.getTick());
                        Server.msgSender.killEnemy(chars.get(j).netID);
                        chars.remove(j);
                    }
                }
            }
        }
    }

    /**
     * Berechnet Kollisionen zwischen Wänden und Chars
     */
    private static void computeWallCollision() {
        ArrayList<Char> chars = Server.game.chars;

        // Alle Chars, die sich bewegen auf Kollision prüfen:
        for (int i = 0; i < chars.size(); i++) {
            Char mover = Server.game.getChar(i);
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
        ArrayList<Char> chars = Server.game.chars;

        // Alle Chars, die sich bewegen auf Kollision prüfen:
        for (int i = 0; i < chars.size(); i++) {
            Char mover = Server.game.getChar(i);
            if (mover instanceof Player) {
                for (int j = 0; j < chars.size(); j++) {
                    Char mob = Server.game.getChar(j);
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
}
