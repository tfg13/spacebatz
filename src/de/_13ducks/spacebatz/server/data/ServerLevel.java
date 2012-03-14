package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Level;

/**
 * Das Level des Servers erweitert das ClientLevel um einige Infos die nur der Server braucht
 *
 * @author michael
 */
public class ServerLevel extends Level {

    /**
     * Konstruktor
     *
     * @param xSize die Höhe des Levels
     * @param ySize die Breite des Levels
     */
    public ServerLevel(int xSize, int ySize) {
        super(xSize, ySize);

    }
}
