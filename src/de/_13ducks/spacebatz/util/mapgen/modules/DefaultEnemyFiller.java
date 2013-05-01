package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Schreibt in die vorhandenen Polygone ein paar Spawn-Gegner rein.
 * Nur recht simpel, sollte von richtigen, ganzen Quests ersetzt werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DefaultEnemyFiller extends Module {

    @Override
    public String getName() {
        
    }

    @Override
    public boolean requiresSeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] provides() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computesPolygons() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] requires() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
