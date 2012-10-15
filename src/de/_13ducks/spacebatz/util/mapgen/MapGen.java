package de._13ducks.spacebatz.util.mapgen;

import de._13ducks.spacebatz.server.data.ServerLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * Der next-Generation Level-Generator
 * Erstellt eine Map durch Hintereinander-Auführen verschiedener Module.
 * Diese Module haben jeweils eine spezifische Aufgabe, z.B. "Loot verstecken".
 * Unterschiedliche Map-Typen entstehen durch verschiedene Kombinationen, Parameter und Reihenfolge dieser Module.
 *
 * @see de._13ducks.spacebatz.util.mapgen.Module
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MapGen {

    /**
     * Alle bekannten Module
     */
    private static HashMap<String, Module> modules = ModuleLoader.loadModules();
    /**
     * Ein Pseudozufallgenerator.
     * Wird verwendet, um die Seeds für die Module zu bauen.
     * Wird während genMap mit dem in den MapParametern festgelegten masterSeed initialisiert.
     */
    private static Random seedGenerator;
    /**
     * Die interne Map, an der gerade gearbeitet wird.
     * Wenn die Methode genMap gerade nicht läuft, ist der Inhalt dieser Variable nicht definiert.
     */
    private static InternalMap currentMap;

    /**
     * Erzeuge eine Map aus den gegebenen Parametern.
     *
     * @param params die Map-Parameter
     * @return die Map als ServerLevel
     */
    public static ServerLevel genMap(final MapParameters params) {
        return generateInternal(params).toServerLevel();
    }

    /**
     * Interne Methode zur Maperstellung. Kann direkt aufgerufen werden, z.B. vom Mapviewer, um an die Metadaten zu kommen.
     *
     * @param params
     */
    public static InternalMap generateInternal(final MapParameters params) {
        if (!params.check()) {
            throw new IllegalArgumentException("Invalid params!");
        }
        // Module sortieren und ausführen:
        ArrayList<String> polyModules = new ArrayList<>();
        ArrayList<String> rasterModules = new ArrayList<>();
        String rasterize = null;
        for (String moduleName : params.getModules()) {
            Module module = modules.get(moduleName);
            // Hat das RASTERIZE?
            for (String var : module.provides()) {
                if (var.equals("RASTERIZE")) {
                    rasterize = moduleName;
                    // Dann nicht einsortieren
                    continue;
                }
            }
            // Sonst einsortieren
            if (module.computesPolygons()) {
                polyModules.add(moduleName);
            } else {
                rasterModules.add(moduleName);
            }
        }
        // Ordnen:
        Comparator<String> comp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return params.getPriority(o1) - params.getPriority(o2);
            }
        };
        Collections.sort(polyModules, comp);
        Collections.sort(rasterModules, comp);

        // Ausführen:
        // Init
        currentMap = new InternalMap();
        seedGenerator = new Random(params.masterSeed);
        // Poly
        for (String polyModuleName : polyModules) {
            runModule(polyModuleName, params);
        }
        // Umwandeln in raster
        if (rasterize != null) {
            runModule(rasterize, params);
        }
        // Raster
        for (String rasterModuleName : rasterModules) {
            runModule(rasterModuleName, params);
        }

        return currentMap;
    }

    private static void runModule(String moduleName, MapParameters params) {
        Module polyModule = modules.get(moduleName);
        HashMap<String, String> modParams = params.getModuleParameters(moduleName);
        if (polyModule.requiresSeed()) {
            modParams.put("SEED", String.valueOf(seedGenerator.nextLong()));
        }
        polyModule.computeMap(currentMap, modParams);
    }
}
