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
import de._13ducks.spacebatz.server.data.Enemy;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.server.data.Player;
import de._13ducks.spacebatz.server.data.Zone;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	if (Server.game.getTick() % (1000 / Settings.SERVER_TICKRATE / Settings.SERVER_SPAWNER_EXECSPERSEC) == 0) {
	    // Alle Spieler durchgehen
	    for (Entity e : Server.game.netIDMap.values()) {
		if (e instanceof Player) {
		    Player player = (Player) e;
		    // Das Gebiet dieses Spielers finden
		    Zone zone = Zone.getMostSpecializedZone(player.getX(), player.getY());
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
		    if (h.timeSinceLastSpawn() / getSpawnRate(zone) * Math.random() > .5) {
			// Spawnen!
			h.spawn();
			double[] pos = calcPosition(player);
			if (pos != null) {
			    Enemy enem = new Enemy(pos[0], pos[1], Server.game.newNetID(), 1);
			    Server.game.netIDMap.put(enem.netID, enem);
			    enem.setMyTarget(player);
			}
		    }
		}
	    }
	}
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
	int rate = 5000; // default
	Object rrate = zone.getValue("SPAWNRATE");
	if (rrate != null) {
	    rate = (Integer) rrate;
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
	 * Liefert die Zeit, die seit dem letzten Spawnen vergangen ist.
	 *
	 * @return die Zeit, die seit dem letzten Spawnen vergangen ist.
	 */
	private double timeSinceLastSpawn() {
	    return System.currentTimeMillis() - lastSpawnTime;
	}

	/**
	 * Aufrufen, wenn ein Gegner gespawnt wurde.
	 */
	private void spawn() {
	    lastSpawnTime = System.currentTimeMillis();
	}
    }
}
