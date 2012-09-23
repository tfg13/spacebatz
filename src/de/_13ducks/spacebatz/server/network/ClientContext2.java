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

    /**
     * Aufrufen, um herauszufinden, ob ein Client eine bestimmte Entity gerade trackt.
     *
     * @param e die Entity
     * @return true, wenn getrackt, false, wenn unbekannt
     */
    boolean tracks(Entity e) {
        //return trackedEntities.containsKey(e);
        return true;
    }
}
