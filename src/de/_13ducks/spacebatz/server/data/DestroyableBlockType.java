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

import java.io.Serializable;

/**
 * Eine Sorte zerstörbarer Blöcke
 *
 * @author michael
 */
public class DestroyableBlockType implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Die Textur des Felds auf dem der zerstörbare Block steht
     */
    public int backgroundTexture;
    /**
     * Die Textur des zerstörbaren Blocktyps
     */
    public int texture;
    /**
     * Name des Items das gedroppt wird, wenn der Block 
     */
    public int dropMaterial;

    /**
     * Erstellt einen neuen zerstörbaren Block
     *
     * @param texture die Textur des Blocks
     * @param backGroundTexture die HintergrundTextur wenn ein Block dieses Typs zerstört wird
     * @param dropMaterial Nummer des Materials, das man beim Zerstören erhält
     */
    public DestroyableBlockType(int texture, int backGroundTexture, int dropMaterial) {
        this.backgroundTexture = backGroundTexture;
        this.texture = texture;
        this.dropMaterial = dropMaterial;
    }
}
