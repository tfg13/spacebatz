package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Distance;
import java.util.Random;

/**
 * Verwaltet das Erzeugen von Gegnern
 *
 * @author michael
 */
public class EnemySpawner {

    /**
     * Der Name des Spawners
     */
    private String name;
    /*
     * Gibt an wieviele Gegner maximal erzeugt werden
     */
    private int maxSpawns;
    /**
     * Der mindestAbstand zu Spielern
     */
    private static final double MINSPAWNDISTANCE = 20.0;
    /**
     * Der mindestAbstand zu Spielern
     */
    private static final double MAXSPAWNDISTANCE = 40.0;

    /**
     * Konstrukto, initialisiert einen neuen Spawner
     */
    public EnemySpawner(String name) {
        this.name = name;
        maxSpawns = 20;
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

            // Position berechnen:
            while (!positionOk) {
                x = 1+ (r.nextDouble() * (Server.game.getLevel().getSizeX() - 2));
                y = 1+ (r.nextDouble() * (Server.game.getLevel().getSizeY() - 2));

                // liegt die Position zwischen MINDISTANE und MAXDISTANCE?
                for (Client client : Server.game.clients.values()) {
                    if (Distance.getDistance(x, y, client.getPlayer().getX(), client.getPlayer().getY()) > MINSPAWNDISTANCE && Distance.getDistance(x, y, client.getPlayer().getX(), client.getPlayer().getY()) < MAXSPAWNDISTANCE) {
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
