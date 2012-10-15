package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.server.levelgenerator.LevelGenerator;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Bindet den alten MapGenerator als alles erledigendes Super-Modul ein.
 * Der ist nicht deterministisch und muss daher bald verschwinden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OldMapGeneratorWrapper extends Module {

    @Override
    public String getName() {
        return "oldmapgen";
    }

    @Override
    public boolean requiresSeed() {
        return false;
    }

    @Override
    public String[] provides() {
        return new String[]{"CREATE_RASTER"};
    }

    @Override
    public boolean computesPolygons() {
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        ServerLevel level = LevelGenerator.generateLevel();
        map.groundTex = level.getGround();
        map.collision = level.getCollisionMap();
        map.metadata = new HashMap<>();
    }
}
