package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
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
                    System.out.println("KOLLISION " + Server.game.getTick());
                }
            }
        }
    }

    /**
     * Berechnet Kollisionen zwischen Wänden und Chars
     */
    private static void computeWallCollision() {
        ArrayList<Wall> walls = Server.game.getServerLevel().getWalls();
        ArrayList<Char> chars = Server.game.chars;
    }
}