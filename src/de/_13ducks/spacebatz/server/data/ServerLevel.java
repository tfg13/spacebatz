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

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Level;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Das Level des Servers erweitert das ClientLevel um einige Infos die nur der Server braucht
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
     * Konstruktor
     *
     * @param xSize die Höhe des Levels
     * @param ySize die Breite des Levels
     */
    public ServerLevel(int xSize, int ySize) {
        super(xSize, ySize);
        areas = new ArrayList<>();
        destroyableBlockTypes = new HashMap<>();
        destroyableBlockTypes.put(2, new DestroyableBlockType(2, 3));
        destroyableBlockTypes.put(11, new DestroyableBlockType(11, 3));

    }

    /**
     * Zerstört einen Block
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     */
    public void destroyBlock(int x, int y) {
        Server.msgSender.broadcastCollisionChange(x, y, false);
        Server.msgSender.broadcastGroundChange(x, y, destroyableBlockTypes.get(getGround()[x][y]).backgroundTexture);
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
        getCollisionMap()[x][y] = true;
        Server.msgSender.broadcastCollisionChange(x, y, true);
        Server.msgSender.broadcastGroundChange(x, y, texture);
    }

    /**
     * Gibt true zurück wenn der Block an der angegebenen Stelle zerstörbar ist
     *
     * @param x X-Koordinate des Blocks
     * @param y Y-Koordinate des Blocks
     * @return true wenn der Block zerstörbar ist ,fasle wenn nicht
     */
    public boolean isBlockDestroyable(int x, int y) {
        return destroyableBlockTypes.containsKey(getGround()[x][y]);
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
     * Gibt das Gebiet mit dem angegebenen Namen zurück oder null wenns keins gibt
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
}
