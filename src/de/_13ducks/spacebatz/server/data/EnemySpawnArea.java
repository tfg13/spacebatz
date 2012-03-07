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
public class EnemySpawnArea implements Serializable{

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
     */
    public EnemySpawnArea(double x1, double y1, double x2, double y2) {
        maxSpawns = 20;
        minSpawnDistance = 20;
        maxSpawnDistance = 40;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Erzeugt bei Bedarf Gegner
     */
    public void tick() {
        int enemys = 0;
        for (Entity e : Server.entityMap.getEntitiesInArea(0, 0, Server.game.getLevel().getSizeX(), Server.game.getLevel().getSizeY())) {
            if (e instanceof Enemy) {
                enemys++;
            }
        }
        if (maxSpawns > enemys && !Server.game.clients.isEmpty()) {
            Random r = new Random(System.currentTimeMillis());
            double x = 0, y = 0;
            boolean positionOk = false;

            double sizeX = x2 - x1;
            double sizeY = y2 - y1;
            // Position berechnen:
            while (!positionOk) {
                x = 1 + (r.nextDouble() * (sizeX - 2));
                y = 1 + (r.nextDouble() * (sizeY - 2));

                // liegt die Position zwischen MINDISTANE und MAXDISTANCE?
                for (Client client : Server.game.clients.values()) {
                    double distance = Distance.getDistance(x, y, client.getPlayer().getX(), client.getPlayer().getY());
                    if (distance > minSpawnDistance && distance < maxSpawnDistance) {
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

    /**
     * Setzt die maximale Gegnerzahl, die in diesem Gebiet gespawnt werden soll
     *
     * @param maxSpawns die maximale Gegnerzahl
     */
    public void setMaxSpawns(int maxSpawns) {
        this.maxSpawns = maxSpawns;
    }
}
