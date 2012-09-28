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
import java.util.LinkedList;

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
     * Wie weit in Client in X-Richtung sehen kann.
     * Denn so weit wird dann auch synchronisiert.
     */
    private static final int UPDATE_AREA_X_HALF = 20;
    /**
     * Wie weit in Client in Y-Richtung sehen kann.
     * Denn so weit wird dann auch synchronisiert.
     */
    private static final int UPDATE_AREA_Y_HALF = 15;
    /**
     * Speichert Entities, deren Bewegung sich geändert hat, zwischen.
     */
    private HashMap<Entity, Object> updatedEntities = new HashMap<>();
    /**
     * Speichert gelöschte Entities zwischen.
     */
    private HashMap<Entity, Object> removedEntities = new HashMap<>();

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
     * Muss immer aufgerufen werden, wenn eine Entity gelöscht wird.
     * Informiert Clients.
     * Cached deletes zwischen, verschickt die Information erst am Ende des Ticks.
     * Es ist in diesem Tick noch ok, wenn die Entity sich noch bewegt.
     * Diese Information wird dann aber bereits nichtmehr versendet.
     *
     * @param e
     */
    public void entityRemoved(Entity e) {
        removedEntities.put(e, null);
    }

    /**
     * Wird vom Netzwerksystem aufgerufen.
     * In dieser Methode wird berechnet, wer welche Daten (aus updatedEntities) gesendet bekommt.
     */
    void tick() {
        for (Client c : Server.game.clients.values()) {
            ClientContext2 context = c.getNetworkConnection().context;
            // Entities, die jetzt neu in Reichweite des Clients sind:
            LinkedList<Entity> entitiesInArea = Server.entityMap.getEntitiesInArea((int) c.getPlayer().getX() - UPDATE_AREA_X_HALF, (int) c.getPlayer().getY() - UPDATE_AREA_Y_HALF, (int) c.getPlayer().getX() + UPDATE_AREA_X_HALF, (int) c.getPlayer().getY() + UPDATE_AREA_Y_HALF);
            for (Entity e : entitiesInArea) {
                if (!context.tracks(e) && !removedEntities.containsKey(e)) {
                    context.track(e);
                    // Einheit bekannt machen:
                    Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_ENTITY_CREATE, craftCreateCommand(e)), c);
                }
            }
            // Updates
            ArrayList<Entity> updateForClient = new ArrayList<>();
            Iterator<Entity> iter = updatedEntities.keySet().iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (context.tracks(e) && !removedEntities.containsKey(e)) {
                    updateForClient.add(e);
                }
            }
            if (!updateForClient.isEmpty()) {
                // Senden
                Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_ENTITY_UPDATE, craftUpdateCommand(updateForClient)), c);
            }
            // Removals
            for (Entity e : removedEntities.keySet()) {
                if (context.tracks(e)) {
                    Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_ENTITY_REMOVE, craftRemoveCommand(e)), c);
                }
            }
        }
        updatedEntities.clear();
        removedEntities.clear();

    }

    /**
     * Bastelt ein Update-Command, der alle gegebenen Einheiten enthält.
     *
     * @param updateList die Liste der geupdateten Einheiten
     * @return der update-Befehl
     */
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

    /**
     * Bastelt ein Create-Command, das die neue Entity einfügt.
     *
     * @param e die neue Entity
     * @return der create-Befehl
     */
    private byte[] craftCreateCommand(Entity e) {
        byte[] data = new byte[e.byteArraySize() + 1];
        e.netPack(data, 1);
        data[0] = (byte) (data.length);
        return data;
    }

    /**
     * Bastelt einen Löschen-Befehl für die gegebene Entity
     *
     * @param e die zu löschende Entity
     * @return der löschen-Befehl
     */
    private byte[] craftRemoveCommand(Entity e) {
        byte[] data = new byte[4];
        Bits.putInt(data, 0, e.netID);
        return data;
    }
}
