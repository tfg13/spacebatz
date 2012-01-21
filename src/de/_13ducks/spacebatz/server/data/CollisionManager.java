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
    public static void computeCollision(ArrayList<Char> chars, ArrayList<Bullet> bullets) {
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
}
