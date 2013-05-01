package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Schreibt in die vorhandenen Polygone ein paar Spawn-Gegner rein.
 * Nur recht simpel, sollte von richtigen, ganzen Quests ersetzt werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DefaultEnemyPlacer extends Module {

    @Override
    public String getName() {
        return "defaultenemyplacer";
    }

    @Override
    public boolean requiresSeed() {
        return true;
    }

    @Override
    public String[] provides() {
        return new String[]{"ENEMYDEF"};
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
        Random random = new Random(Long.parseLong(parameters.get("SEED")));
        EnemyTypes types = new EnemyTypes();
        // Alle Polygone, die nicht anderweitig verwendet werden mit einer zufälligen Sorte Gegner füllen (nicht den Boss)
        Iterator<MPolygon> polyIter = map.polygons.polyIter();
        while (polyIter.hasNext()) {
            MPolygon poly = polyIter.next();
            if (!poly.border && !poly.solid && !poly.spawn && poly.spawnInfo == null) {
                // Zufälligen Gegner wählen:
                int type;
                while ((type = random.nextInt(types.getEnemytypelist().size())) != 6) {
                    // Anzahl an der Größe des Polys festmachen:
                    int numberOfEnemys = (int) (poly.getArea() / 16) + 1;
                    poly.spawnInfo = new HashMap<>();
                    poly.spawnInfo.put(type, numberOfEnemys);
                }
            }
        }
    }

}
