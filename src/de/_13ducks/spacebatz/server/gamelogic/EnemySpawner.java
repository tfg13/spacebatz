/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Zone;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import java.util.*;

/**
 * Verwaltet das Spawnen von Gegnern in der Nähe der Spieler
 *
 * @author tobi
 */
public class EnemySpawner {

    /**
     * Ordnet den Spieler ihre SpawnHistory zu.
     */
    private static HashMap<Player, PlayerSpawnHistory> history = new HashMap<>();

    /**
     * Muss jeden Gametick aufgerufen werden.
     * Entscheidet für alle Spieler, ob neue Gegner benötigt werden
     * Rechner nicht bei jedem Tick.
     */
    public static void tick() {
        Random random = new Random();
        if (Server.game.getTick() % (1000 / Settings.SERVER_TICKRATE / Settings.SERVER_SPAWNER_EXECSPERSEC) == 0) {
            // Alle Spieler durchgehen
            for (Entity e : Server.game.netIDMap.values()) {
                if (e instanceof Player) {
                    Player player = (Player) e;
                    // Das Gebiet dieses Spielers finden
                    Zone zone = Zone.getMostSpecializedZone(player.getX(), player.getY());
		    // In dieser Zone überhaupt spawnen?
		    if (isSpawnEnabled(zone)) {
			// Die Spawngeschichte dieses Spielers finden
			PlayerSpawnHistory h = getHistory(player);
			// Entscheiden, ob ein Gegner gespawnt werden soll.
			/*
			* Die Spawnformel ist:
			*
			* s = (tsl/rate)*Math.random()
			* Wenn s > .5 ist wird gespawnt
			*
			* tsl ist die Zeit in Millisekunden seit dem letzten Spawnen
			* rate ist die Spawnrate des Sektors. Die Durchschnittliche Wartezeit zwischen 2 spawnenden Einheiten.
			*/
			if (h.inWaveSpawn()) {
			    // es wird gerade eine Gegnerwelle gespawnt -> schnelles Spawnen
			    if (h.timeSinceLastSpawn() / 300 * random.nextDouble() > .5) {
				// Spawnen!
				h.spawn(player);
			    }
			} else {
			    // es wird gerade keine Gegnerwelle gespawnt -> lange bis zum nächsten Spawnen
			    if (h.timeSinceLastSpawn() / getSpawnRate(zone) * random.nextDouble() > .5) {
				// Anzahl der Gegner in dieser Welle festlegen
				int enemywave = Math.min((int) Math.abs(random.nextGaussian() * 2.5) + 1, 6);
				h.startWaveSpawn(enemywave);
				// Spawnen!
				h.spawn(player);
			    }
			}
		    }
		}
            }
        }
    }

    /**
     * Prüft, ob in diesem Gebiet überhaupt Gegner gespawnt werden sollen
     * @param zone die Zone
     * @return true, wenn Spawnen erlaubt
     */
    private static boolean isSpawnEnabled(Zone zone) {
	int val = 1; // Notfall-Default ist an
	Object spawnOn = zone.getValue("spawn_enabled");
	if (spawnOn != null) {
	    val = (Integer) spawnOn;
	} else {
	    System.out.println("WARNING: global zone does not define \"spawn_enabled\" (i)");
	}
	return val != 0;
    }

    /**
     * Liefert die History zum gegebenen Player.
     * Wenn es noch keine History gibt, wird eine angelegt.
     *
     * @param player der Player, dessen History interessiert
     * @return die gespeicherte oder neu erstellte History
     */
    private static PlayerSpawnHistory getHistory(Player player) {
        if (!history.containsKey(player)) {
            PlayerSpawnHistory newHistory = new PlayerSpawnHistory();
            history.put(player, newHistory);
        }
        return history.get(player);
    }

    /**
     * Liefert die Spawnrate für diese Zone.
     *
     * @param zone die Zone, deren Rate interessiert
     * @return die Rate, oder ein default-Wert
     */
    private static double getSpawnRate(Zone zone) {
        int rate = 9000; // Notfall-Wert. Default-Wert in Zone einstellen!
        Object rrate = zone.getValue("spawnrate");
        if (rrate != null) {
            rate = (Integer) rrate;
        } else {
	    System.out.println("WARNING: global zone does not define \"spawnrate\" (i)");
	}
        return rate;
    }

    /**
     * Berechnet eine Spawnposition für die angegebene Einheit.
     *
     * @param p die Einheit
     * @return eine Spawnposition oder null
     */
    private static double[] calcPosition(Player p) {
        LinkedList<double[]> positions = positionsOnCircleSegment(p.getX(), p.getY(), 15, p.getDirection(), p.isMoving() ? Math.PI / 2 : Math.PI * 2);
        // Nur die freien Positionen nehmen:
        removeNonFree(positions);
        // Noch Position übrig?
        if (!positions.isEmpty()) {
            // Eine Auslosen
            int index = (int) (Math.random() * positions.size());
            return positions.get(index);
        }
        return null;
    }

    /**
     * Findet alle Positionen, die auf einem Kreissegment liegen.
     *
     * @param x Mitte des Kreises X
     * @param y Mitte des Kreises Y
     * @param radius Radius
     * @param dir Richtung (0-2PI)
     * @param area Größe des Segments (0-2PI)
     * @return eine Liste mit Positionen auf dem Kreissegment
     */
    private static LinkedList<double[]> positionsOnCircleSegment(double x, double y, double radius, double dir, double area) {
        LinkedList<double[]> positions = new LinkedList<>();
        // Auf der Kreisbahn entlang laufen und auf Felder runden
        for (double d = dir - (area / 2); d <= dir + (area / 2); d += .5) {
            // Position berechnen
            positions.add(new double[]{x + Math.cos(d) * radius, y + Math.sin(d) * radius});
        }
        return positions;
    }

    /**
     * Löscht alle Position aus der Liste, die derzeit nicht frei sind.
     *
     * @param positions die Liste
     */
    private static void removeNonFree(List<double[]> positions) {
        Iterator<double[]> iter = positions.iterator();
        while (iter.hasNext()) {
            double[] pos = iter.next();
            if (Server.game.getLevel().getCollisionMap()[(int) Math.round(pos[0])][(int) Math.round(pos[1])]) {
                iter.remove();
            }
        }
    }

    /**
     * Speichert spawnrelevante Infos wie den letzten Spawnzeitpunkt.
     */
    private static class PlayerSpawnHistory {

        /**
         * Der Zeitpunkt, zu dem zuletzt ein Gegner gespawnt wurde
         */
        private long lastSpawnTime = System.currentTimeMillis();
        /**
         * Wie oft noch in kurzen Zeitabständen gespawnt wird (-> Gegnerwelle)
         */
        private int waveSpawnRemaining;

        /**
         * Liefert die Zeit, die seit dem letzten Spawnen vergangen ist.
         *
         * @return die Zeit, die seit dem letzten Spawnen vergangen ist.
         */
        private double timeSinceLastSpawn() {
            return System.currentTimeMillis() - lastSpawnTime;
        }

        /**
         * Gibt zurück, ob der nächste Gegner in kurzer Zeit gespawnt werden soll (gehört zur Gegnerwelle) oder erst nach längerer Zeit
         */
        private boolean inWaveSpawn() {
            if (waveSpawnRemaining > 0) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Legt die Gegneranzahl für aktuelle Gegnerwelle fest
         */
        private void startWaveSpawn(int amount) {
            waveSpawnRemaining = amount;
        }

        /**
         * Spawnt einen Gegner zu einem Spieler
         */
        private void spawn(Player player) {
            Random random = new Random();

            lastSpawnTime = System.currentTimeMillis();
            waveSpawnRemaining--;
            double[] pos = calcPosition(player);
            if (pos != null) {
                int enemytype = 1;
                int randomenemy = random.nextInt(20);
                if (randomenemy == 0) {
                    enemytype = 0;
                } else if (randomenemy == 2) {
                    enemytype = 2;
                }

                Enemy enem = new Enemy(pos[0], pos[1], Server.game.newNetID(), enemytype);
                Server.game.netIDMap.put(enem.netID, enem);
                enem.setMyTarget(player);
            }
        }
    }
}
