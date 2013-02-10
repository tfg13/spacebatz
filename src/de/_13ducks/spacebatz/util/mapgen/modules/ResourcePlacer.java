package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.PolyMesh;
import de._13ducks.spacebatz.util.mapgen.util.MPolygonSubdivider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * Dieses Modul füllt größere Berge mit Ressourcen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ResourcePlacer extends Module {

    @Override
    public String getName() {
        return "resourceplacer";
    }

    @Override
    public boolean requiresSeed() {
        return true;
    }

    @Override
    public String[] provides() {
        return new String[]{"RESOURCES"};
    }

    @Override
    public boolean computesPolygons() {
        return true;
    }

    @Override
    public String[] requires() {
        return new String[]{"BORDER"};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        // Die 50 % größten Polygone, die Wand, aber nicht Border sind mit Ressourcen füllen (via SubMesh).
        // Liste mit nicht-Border Poly erstellen
        ArrayList<MPolygon> polys = new ArrayList<>();
        for (MPolygon poly : map.polygons.polys) {
            if (!poly.border && poly.solid) {
                polys.add(poly);
            }
        }
        // Nach Größe sortieren
        Collections.sort(polys, new Comparator<MPolygon>() {
            @Override
            public int compare(MPolygon o1, MPolygon o2) {
                return Double.compare(o2.getArea(), o1.getArea());
            }
        });
        // Teil davon auffüllen:
        Random random = new Random(Long.parseLong(parameters.get("SEED")));
        for (int i = 0; i < polys.size() / 3; i++) {
            MPolygon poly = polys.get(i);
            // Mesh erst noch erzeugen
            if (poly.getMesh() == null) {
                poly.setMesh(MPolygonSubdivider.createMesh(poly, 20, 0, random.nextLong()));
            }
            // SubMesh-Blöcke auffüllen:
            ArrayList<MPolygon> subPolys = new ArrayList<>(poly.getMesh().polys);
            Collections.shuffle(subPolys, random);
            for (int j = 0; j < subPolys.size() / 8; j++) {
                subPolys.get(j).resource = 1;
            }
        }
    }
}
