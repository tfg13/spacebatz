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

import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_BROADCAST_GROUND_CHANGE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_COLLISION;
import java.util.ArrayList;
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
     * Die Liste mit Gebieten
     */
    private ArrayList<Zone> areas;
    /**
     * Enthält alle vom MapGenerator für diese Map erstellten Quests. Die
     * meisten Quests funktionieren nur auf bestimmten Map-Strukturen und müssen
     * daher schon während der Map-Berechung berücksichtigt werden. Diese Quests
     * sind dann hier zu finden.
     */
    public final List<Quest> quests;
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
    public ServerLevel(int xSize, int ySize, String hash, List<Quest> quests) {
        super(xSize, ySize);
        this.hash = hash;
        areas = new ArrayList<>();
        destroyableBlockTypes = new HashMap<>();
        destroyableBlockTypes.put(2, new DestroyableBlockType(2, 3, -1)); // -1: droppt nichts
        destroyableBlockTypes.put(11, new DestroyableBlockType(11, 3, 1));
        destroyableBlockTypes.put(12, new DestroyableBlockType(12, 3, 2));
        this.quests = Collections.unmodifiableList(quests);
    }

    /**
     * Zerstört einen Block
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     */
    public void destroyBlock(int x, int y) {
        // Material droppen
        int material = destroyableBlockTypes.get(getGround()[x][y]).dropMaterial;
        if (material >= 0) {
            // Material mit angepasster Position (int -> double) droppen
            DropManager.dropMaterial(destroyableBlockTypes.get(getGround()[x][y]).dropMaterial, 1);
        }

        STC_CHANGE_COLLISION.broadcastCollisionChange(x, y, false);
        STC_BROADCAST_GROUND_CHANGE.broadcastGroundChange(x, y, destroyableBlockTypes.get(getGround()[x][y]).backgroundTexture);
        getGround()[x][y] = destroyableBlockTypes.get(getGround()[x][y]).backgroundTexture;
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
        getGround()[x][y] = texture;
        //getCollisionMap()[x][y] = true;
        STC_CHANGE_COLLISION.broadcastCollisionChange(x, y, true);
        STC_BROADCAST_GROUND_CHANGE.broadcastGroundChange(x, y, texture);
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
            return destroyableBlockTypes.containsKey(getGround()[x][y]);
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

    /**
     * Fügt der Map ein neues Gebiet hinzu
     *
     * @param area das neue Gebiet
     */
    public void addArea(Zone area) {
        areas.add(area);
    }

    /**
     * Gibt das Gebiet mit dem angegebenen Namen zurück oder null wenns keins
     * gibt
     *
     * @return das Gebiet mit dem Namen oder null wenns keins gibt
     */
    public Zone getArea(String name) {
        for (Zone area : areas) {
            if (area.getName().equals(name)) {
                return area;
            }
        }
        return null;
    }

    public String getHash() {
        return hash;
    }
}
