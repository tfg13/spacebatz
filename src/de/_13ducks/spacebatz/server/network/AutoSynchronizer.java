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

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Verwaltet zusammen mit dem neuen ClientContext die Synchronisierung von Entitys mit allen Clients.
 * Der Client wird nur über die Existenz von Entitys informiert, die in seiner Nähe sind, die er also vielleicht bald zu sehen bekommt.
 * Ebenso werden Änderungen der Bewegung nur für "getrackte" Einheiten verwaltet.
 * Dabei wird grundsätzlich das ältere System mit der "Movement"-Klasse weiter verwendet, allerdings werden Entitys nicht mehr gepollt, sondern benachrichtigen
 * diesen Synchronisierer von sich aus. Hier wird dann entschieden, wem die Änderungen mitgeteilt werden müssen und wem nicht.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class AutoSynchronizer {

    /**
     * Speichert Entities, deren Bewegung sich geändert hat, zwischen.
     */
    private HashMap<Entity, Object> updatedEntities = new HashMap<>();

    /**
     * Muss von einer Entity immer dann aufgerufen werden, wenn sich ihre Bewegung geändert hat.
     * Cached Movements zwischen und sendet am Ende des Ticks nur die aktuellesten Informationen, häufiges Aufrufen ist also kein Problem.
     *
     * @param e die Entity, deren Movement sich geändert hat.
     */
    public void updateMovement(Entity e) {
        updatedEntities.put(e, null);
    }

    /**
     * Wird vom Netzwerksystem aufgerufen.
     * In dieser Methode wird berechnet, wer welche Daten (aus updatedEntities) gesendet bekommt.
     */
    void tick() {
        for (Client c : Server.game.clients.values()) {
            ArrayList<Entity> updateForClient = new ArrayList<>();
            Iterator<Entity> iter = updatedEntities.keySet().iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                iter.remove();
                if (c.getNetworkConnection().context.tracks(e)) {
                    updateForClient.add(e);
                }
            }
            if (!updateForClient.isEmpty()) {
                // Senden
                Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_ENTITY_UPDATE, craftUpdateCommand(updateForClient)), c);
            }
        }
    }

    private byte[] craftUpdateCommand(ArrayList<Entity> updateList) {
        // Größe in erstes byte schreiben. Egal wenn zu viel für dieses byte - denn dann ist das Paket sowieso Fragmentiert und die Größe wird ignoriert...
        byte[] data = new byte[updateList.size() * 28 + 1];
        data[0] = (byte) updateList.size();
        for (int i = 0; i < updateList.size(); i++) {
            Entity e = updateList.get(i);
            // netID:
            Bits.putInt(data, i * 28 + 1, e.netID);
            // movement
            Movement m = e.getMovement();
            Bits.putInt(data, i * 28 + 5, m.startTick);
            Bits.putFloat(data, i * 28 + 9, m.speed);
            Bits.putFloat(data, i * 28 + 13, m.startX);
            Bits.putFloat(data, i * 28 + 17, m.startY);
            Bits.putFloat(data, i * 28 + 21, m.vecX);
            Bits.putFloat(data, i * 28 + 25, m.vecY);
        }
        return data;
    }
}
