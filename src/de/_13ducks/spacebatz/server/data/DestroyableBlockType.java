/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * Erstellt einen neuen zerstörbaren Block
     *
     * @param texture die Textur des Blocks
     * @param backGroundTexture die HintergrundTextur wenn ein Block dieses Typs zerstört wird
     */
    public DestroyableBlockType(int texture, int backGroundTexture) {
        this.backgroundTexture = backGroundTexture;
        this.texture = texture;
    }
}
