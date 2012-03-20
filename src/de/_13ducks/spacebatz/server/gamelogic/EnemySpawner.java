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
			Enemy enem = new Enemy(player.getX() + 2, player.getY() + 2, Server.game.newNetID(), 1);
			Server.game.netIDMap.put(enem.netID, enem);
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
     * @param zone die Zone, deren Rate interessiert
     * @return die Rate, oder ein default-Wert
     */
    private static int getSpawnRate(Zone zone) {
	int rate = 2000; // default
	Object rrate = zone.getValue("SPAWNRATE");
	if (rrate != null) {
	    rate = (Integer) rrate;
	}
	return rate;
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
	private long timeSinceLastSpawn() {
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
