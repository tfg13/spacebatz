package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Level;
import java.util.HashMap;

/**
 * Das Level des Servers erweitert das ClientLevel um einige Infos die nur der Server braucht
 *
 * @author michael
 */
public class ServerLevel extends Level {

    /**
     * Liste der Typen von zerstörbaren Blocken
     */
    HashMap<Integer, DestroyableBlockType> destroyableBlockTypes;

    /**
     * Konstruktor
     *
     * @param xSize die Höhe des Levels
     * @param ySize die Breite des Levels
     */
    public ServerLevel(int xSize, int ySize) {
        super(xSize, ySize);
        destroyableBlockTypes = new HashMap<>();
        destroyableBlockTypes.put(6, new DestroyableBlockType(6, 3));

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
}
