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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_BROADCAST_TOP_CHANGE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_COLLISION;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Das Level des Servers erweitert das ClientLevel um einige Infos die nur der
 * Server braucht
 *
 * @author michael
 */
public class ServerLevel extends Level {

    private static final long serialVersionUID = 1L;
    /**
     * Liste der Typen von zerstörbaren Blocken
     */
    private HashMap<Integer, DestroyableBlockType> destroyableBlockTypes;
    /**
     * Enthält alle vom MapGenerator für diese Map erstellten Quests. Die
     * meisten Quests funktionieren nur auf bestimmten Map-Strukturen und müssen
     * daher schon während der Map-Berechung berücksichtigt werden. Diese Quests
     * sind dann hier zu finden.
     */
    public final List<Quest> quests;
    /**
     * Die initiale netMap.
     * Enthält Einheiten, die schon bei der Maperstellung angelegt wurden.
     * Wird beim Spielstart in die echte netMap kopiert (es wird *nicht* diese Map übernommen)
     */
    public final HashMap<Integer, Entity> initNetMap;
    /**
     * Parameter, um diese Map wieder zu erstellen.
     */
    private final String hash;

    /**
     * Konstruktor
     *
     * @param xSize die Höhe des Levels
     * @param ySize die Breite des Levels
     */
    public ServerLevel(int xSize, int ySize, String hash, List<Quest> quests, HashMap<Integer, Entity> initIDMap) {
        super(xSize, ySize);
        this.hash = hash;
        destroyableBlockTypes = new HashMap<>();
        destroyableBlockTypes.put(2, new DestroyableBlockType(2, 3, -1)); // -1: droppt nichts
        destroyableBlockTypes.put(4, new DestroyableBlockType(11, 3, 1));
        // destroyableBlockTypes.put(12, new DestroyableBlockType(12, 3, 2));
        this.quests = Collections.unmodifiableList(quests);
        this.initNetMap = initIDMap;
    }

    /**
     * Zerstört einen Block
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     */
    public void destroyBlock(int x, int y) {
        // Material droppen
        int material = destroyableBlockTypes.get(top[x][y]).dropMaterial;
        if (material >= 0) {
            // Material mit angepasster Position (int -> double) droppen
            DropManager.dropMaterial(destroyableBlockTypes.get(top[x][y]).dropMaterial, 1);
        }

        STC_CHANGE_COLLISION.broadcastCollisionChange(x, y, false);
        STC_BROADCAST_TOP_CHANGE.broadcastTopChange(x, y, 0);
        top[x][y] = 0; // Wand löschen
        getCollisionMap()[x][y] = false;
    }

    /**
     * Erzeugt einen zerstörbaren Block
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     * @param texture die Textur des Blocks
     */
    public void createDestroyableBlock(int x, int y, int texture) {
        top[x][y] = texture;
        //getCollisionMap()[x][y] = true;
        STC_CHANGE_COLLISION.broadcastCollisionChange(x, y, true);
        STC_BROADCAST_TOP_CHANGE.broadcastTopChange(x, y, texture);
    }

    /**
     * Gibt true zurück wenn der Block an der angegebenen Stelle zerstörbar ist
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     * @return true wenn der Block zerstörbar ist ,fasle wenn nicht
     */
    public boolean isBlockDestroyable(int x, int y) {
        if (0 < x && x < getSizeX() && 0 < y && y < getSizeY()) {
            return destroyableBlockTypes.containsKey(top[x][y]);
        } else {
            return false;
        }
    }

    /**
     * Fügt der Liste einen neuen Typ zerstörbarer Blöcke hinzu
     *
     * @param newBlock der neue Blocktyp
     */
    public void addNewDestroyableBlockType(DestroyableBlockType newBlock) {
        destroyableBlockTypes.put(newBlock.texture, newBlock);
    }

    public String getHash() {
        return hash;
    }
}
