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
package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.HashMap;

/**
 * Der neue ClientContext
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
class ClientContext2 {

    /**
     * Alle Entities, die derzeit getrackt werden.
     */
    private HashMap<Entity, Object> trackedEntities = new HashMap<>();
    private boolean[][] loadedChunks = new boolean[Server.game.getLevel().getSizeX() / 8][Server.game.getLevel().getSizeY() / 8];

    /**
     * Aufrufen, um herauszufinden, ob ein Client eine bestimmte Entity gerade trackt.
     *
     * @param e die Entity
     * @return true, wenn getrackt, false, wenn unbekannt
     */
    boolean tracks(Entity e) {
        return trackedEntities.containsKey(e);
    }

    /**
     * Aufrufen, um eine Entity einzufügen.
     * Wenn die Entity bereits getrackt wird passiert nichts.
     *
     * @param e die Entity
     */
    void track(Entity e) {
        trackedEntities.put(e, null);
    }

    /**
     * Löscht die Entity aus den getrackten.
     * Wenn die Entity nicht getrackt wird, passiert nichts.
     *
     * @param e die Entity
     */
    void stopTrack(Entity e) {
        trackedEntities.remove(e);
    }

    /**
     * Findet heraus, ob der gegebene Chunk dem Client bereits gesendet wurde.
     * Liefert für ungültige Chunk-Koordinaten immer true, damit nichts übertragen wird, das nicht existiert.
     *
     * @param x x-Koordinate des Chunks
     * @param y y-Koordinate des Chunks
     * @return true, wenn geladen (bekannt)
     */
    boolean chunkLoaded(int x, int y) {
        if (x < 0 || y < 0 || x >= loadedChunks.length || y >= loadedChunks[0].length) {
            return true;
        }
        return loadedChunks[x][y];
    }

    /**
     * Aufrufen, wenn dem Client ein Chunk geschickt wurde, der fortan auf Änderungen gecheckt werden muss.
     *
     * @param x x-Koordinate des Chunks
     * @param y y-Koordinate des Chunks
     */
    void setChunkLoaded(int x, int y) {
        loadedChunks[x][y] = true;
    }
}
