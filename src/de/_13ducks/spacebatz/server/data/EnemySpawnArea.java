package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Distance;
import java.io.Serializable;
import java.util.Random;

/**
 * Verwaltet das Erzeugen von Gegnern
 *
 * @author michael
 */
public class EnemySpawnArea implements Serializable {

    /*
     * Gibt an wieviele Gegner maximal erzeugt werden
     */
    private int maxSpawns;
    /**
     * Der mindestAbstand zu Spielern
     */
    private double minSpawnDistance;
    /**
     * Der mindestAbstand zu Spielern
     */
    private double maxSpawnDistance;
    /**
     * Die Koordinaten der linken Oberen und der rechten unteren Ecke des Gebiets
     */
    double x1, x2, y1, y2;

    /**
     * Konstrukto, initialisiert einen neuen Spawner
     *
     * @param x1 X-Koordinate der linken oberen Ecke des Gebiets
     * @param y1 Y-Koordinate der linken oberen Ecke des Gebiets
     * @param x2 X-Koordinate der rechten unteren Ecke des Gebiets
     * @param y2 Y-Koordinate der rechten unteren Ecke des Gebiets
     */
    public EnemySpawnArea(double x1, double y1, double x2, double y2) {
        maxSpawns = 10;
        minSpawnDistance = 15;
        maxSpawnDistance = 15;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Erzeugt bei Bedarf Gegner
     */
    public void tick() {


        for (Client client : Server.game.clients.values()) {
            double playerX = client.getPlayer().getX();
            double playerY = client.getPlayer().getY();

            // Wenn der Spieler in der Zone ist:
            if (x1 < playerX && playerX < x2 && y1 < playerY && playerY < y2) {
                int enemys = 0;
                for (Entity e : Server.entityMap.getEntitiesInArea((int) x1, (int) y1, (int) x2, (int) y2)) {
                    if (e instanceof Enemy) {
                        enemys++;
                    }
                }
                if (maxSpawns > enemys) {
                    Random r = new Random(System.currentTimeMillis());
                    double x = 0, y = 0;
                    boolean positionOk = false;

                    int tries = 0;
                    // Position berechnen:
                    while (!positionOk) {
                        tries++;
                        // Abbrechen wenn wir schon 200 Versuche haben:
                        if (tries > 200) {
                            return;
                        }
                        x = (playerX - maxSpawnDistance) + (r.nextDouble() * 2 * maxSpawnDistance);
                        y = (playerY - maxSpawnDistance) + (r.nextDouble() * 2 * maxSpawnDistance);
                        double distance = Distance.getDistance(x, y, playerX, playerY);
                        if (distance > minSpawnDistance && distance < maxSpawnDistance && x1 < x && x < x2 && y1 < y && y < y2) {
                            boolean collision = Server.game.getLevel().getCollisionMap()[(int) x][(int) y];
                            if (collision == false) {
                                positionOk = true;
                            }
                        }
                    }
                    // zufälligen Gegner erzeugen:
                    int enemytype = r.nextInt(30);
                    if (enemytype > 2) {
                        enemytype = 1;
                    }
                    Enemy e = new Enemy(x, y, Server.game.newNetID(), enemytype);
                    Server.game.netIDMap.put(e.netID, e);
                }

            }
        }
    }

    /**
     * Setzt die maximale Gegnerzahl, die in diesem Gebiet gespawnt werden soll
     *
     * @param maxSpawns die maximale Gegnerzahl
     */
    public void setMaxSpawns(int maxSpawns) {
        this.maxSpawns = maxSpawns;
    }
}
