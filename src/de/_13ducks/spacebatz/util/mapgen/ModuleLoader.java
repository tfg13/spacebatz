package de._13ducks.spacebatz.util.mapgen;

import de._13ducks.spacebatz.util.mapgen.modules.OldMapGeneratorWrapper;
import de._13ducks.spacebatz.util.mapgen.modules.PolygonMapGenerator;
import de._13ducks.spacebatz.util.mapgen.modules.Rasterizer;
import java.util.HashMap;

/**
 * Läd die Module.
 * In eigene Klasse ausgelagert, damit man neue Module nur an einer Stelle eintragen muss.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class ModuleLoader {

    private static HashMap<String, Module> map = new HashMap<>();

    public static HashMap<String, Module> loadModules() {
        if (map.isEmpty()) {
            // Hier neue Module eintragen:
            addModule(new PolygonMapGenerator());
            addModule(new Rasterizer());
            addModule(new OldMapGeneratorWrapper());
        }
        return map;
    }

    private static void addModule(Module module) {
        if (map.containsKey(module.getName())) {
            throw new IllegalArgumentException("Cannot load MapGen-module \"" + module.getName() + "\", a module with that name already exists.");
        }
        map.put(module.getName(), module);
    }
    
    private ModuleLoader() {
        // privater Konstruktor, da Utility-Class
    }
}