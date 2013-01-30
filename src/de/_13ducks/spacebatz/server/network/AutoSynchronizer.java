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
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.shared.network.MessageIDs;
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
 * Weiter wird auch die Map (das Level) auf diese Weise übertragen: Der Client bekommt die Texturdaten und Beleuchtungsdaten erst, wenn er in der Nähe ist.
 * Das beschleunigt den Spielstart massiv und entlastet das Netzwerksystem zu Beginn.
 * Änderungen an der Map werden derzeit aber noch manuell übertragen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class AutoSynchronizer {

    /**
     * Wie weit in Client in X-Richtung sehen kann.
     * Denn so weit wird dann auch synchronisiert.
     */
    private static final int UPDATE_AREA_X_DIST = 35;
    /**
     * Wie weit in Client in Y-Richtung sehen kann.
     * Denn so weit wird dann auch synchronisiert.
     */
    private static final int UPDATE_AREA_Y_DIST = 20;
    /**
     * Wieviele Chunks des Levels in jede Richtung geupdated werden.
     */
    private static final int UPDATE_LEVEL_DIST_X = 4;
    /**
     * Wieviele Chunks des Levels in jede Richtung geupdated werden.
     */
    private static final int UPDATE_LEVEL_DIST_Y = 3;
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
        // Berechnung der Einheitenbewegungen für alle Clients
        for (Client c : Server.game.clients.values()) {
            ClientContext2 context = c.getNetworkConnection().context;
            ArrayList<Entity> updateForClient = new ArrayList<>();
            // Entities, die jetzt neu in Reichweite des Clients sind:
            LinkedList<Entity> entitiesInArea = Server.entityMap.getEntitiesInArea((int) c.getPlayer().getX() - UPDATE_AREA_X_DIST, (int) c.getPlayer().getY() - UPDATE_AREA_Y_DIST, (int) c.getPlayer().getX() + UPDATE_AREA_X_DIST, (int) c.getPlayer().getY() + UPDATE_AREA_Y_DIST);
            for (Entity e : entitiesInArea) {
                if (!context.tracks(e) && !removedEntities.containsKey(e)) {
                    context.track(e);
                    // Einheit bekannt machen:
                    Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_ENTITY_CREATE, craftCreateCommand(e)), c);
                    updateForClient.add(e);
                }
            }
            // Updates
            Iterator<Entity> iter = updatedEntities.keySet().iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (context.tracks(e) && !removedEntities.containsKey(e)) {
                    updateForClient.add(e);
                }
            }
            if (!updateForClient.isEmpty()) {
                // Senden
                Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_ENTITY_UPDATE, craftUpdateCommand(updateForClient)), c);
            }
            // Removals
            for (Entity e : removedEntities.keySet()) {
                if (context.tracks(e)) {
                    Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_ENTITY_REMOVE, craftRemoveCommand(e)), c);
                }
            }
        }
        updatedEntities.clear();
        removedEntities.clear();

        // Neue Mapbereiche?
        for (Client c : Server.game.clients.values()) {
            ClientContext2 context = c.getNetworkConnection().context;

            int playerX = ((int) c.getPlayer().getX()) / 8;
            int playerY = ((int) c.getPlayer().getY()) / 8;
            playerX -= UPDATE_LEVEL_DIST_X;
            playerY -= UPDATE_LEVEL_DIST_Y;
            if (playerX < 0) {
                playerX = 0;
            }
            if (playerY < 0) {
                playerY = 0;
            }

            for (int x = playerX; x < playerX + UPDATE_LEVEL_DIST_X * 2 + 1; x++) {
                for (int y = playerY; y < playerY + UPDATE_LEVEL_DIST_Y * 2 + 1; y++) {
                    if (!context.chunkLoaded(x, y)) {
                        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TRANSFER_CHUNK, craftTransferChunkCommand(x, y)), c);
                        context.setChunkLoaded(x, y);
                    }
                }
            }

        }
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

    /**
     * Bastelt ein Chunk-Transfer für den gegebenen Chunk
     *
     * @param x x-Koordinate des Chunks
     * @param y y-Koordinate des Chunks
     * @return die transfer-Daten
     */
    private byte[] craftTransferChunkCommand(int x, int y) {
        int[][] ground = Server.game.getLevel().ground;
        int[][] top = Server.game.getLevel().top;
        int[][] dye_ground = Server.game.getLevel().dye_ground;
        int[][] dye_top = Server.game.getLevel().dye_top;
        boolean[][] col = Server.game.getLevel().getCollisionMap();
        byte[][] shadow = Server.game.getLevel().shadow;
        byte[] data = new byte[8 * 8 * 4 * 4 + 8 + 8 + 8 * 8];
        Bits.putInt(data, 0, x);
        Bits.putInt(data, 4, y);
        int dataIndex = 8;
        // Ground
        for (int gx = 0; gx < 8; gx++) {
            for (int gy = 0; gy < 8; gy++) {
                Bits.putInt(data, dataIndex, ground[x * 8 + gx][y * 8 + gy]);
                dataIndex += 4;
            }
        }
        // Top
        for (int gx = 0; gx < 8; gx++) {
            for (int gy = 0; gy < 8; gy++) {
                Bits.putInt(data, dataIndex, top[x * 8 + gx][y * 8 + gy]);
                dataIndex += 4;
            }
        }
        // dye_ground
        for (int gx = 0; gx < 8; gx++) {
            for (int gy = 0; gy < 8; gy++) {
                Bits.putInt(data, dataIndex, dye_ground[x * 8 + gx][y * 8 + gy]);
                dataIndex += 4;
            }
        }
        // dye_top
        for (int gx = 0; gx < 8; gx++) {
            for (int gy = 0; gy < 8; gy++) {
                Bits.putInt(data, dataIndex, dye_top[x * 8 + gx][y * 8 + gy]);
                dataIndex += 4;
            }
        }
        // Col
        for (int gx = 0; gx < 8; gx++) {
            byte row = 0;
            for (int gy = 0; gy < 8; gy++) {
                if (col[x][y]) {
                    row |= 1 << gy;
                }
            }
            data[dataIndex++] = row;
        }
        // Shadow
        for (int gx = 0; gx < 8; gx++) {
            for (int gy = 0; gy < 8; gy++) {
                data[dataIndex++] = shadow[x * 8 + gx][y * 8 + gy];
            }
        }
        return data;
    }
}
