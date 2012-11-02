package de._13ducks.spacebatz.util.mapgen.modules;

import com.sun.swing.internal.plaf.metal.resources.metal;
import com.vividsolutions.jts.geom.GeometryCollection;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
import java.util.HashMap;

/**
 * Formt den Landeplatz/Spawn und erzeugt Spawn-Koordinaten
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class SpawnFormer extends Module {

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public boolean requiresSeed() {
        return false;
    }

    @Override
    public String[] provides() {
        return new String[]{"SPAWN"};
    }

    @Override
    public boolean computesPolygons() {
        return true;
    }

    @Override
    public String[] requires() {
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        // Simple Implementierung, verbesserungswürdig - einfach erstbesten, freien Polygon nehmen
        for (int i = 0; i < map.polygons.getNumGeometries(); i++) {
            MPolygon poly = (MPolygon) map.polygons.getGeometryN(i);
            if (!poly.border && !poly.solid) {
                poly.spawn = true;
                break;
            }
        }
    }

}
