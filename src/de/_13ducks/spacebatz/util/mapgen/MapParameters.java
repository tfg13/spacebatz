package de._13ducks.spacebatz.util.mapgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Beschreibt die Parameter, also Einstellungen für den MapGenerator.
 * Dieses Objekt definiert die Map vollständig, inklusive des einfließenden Zufalls.
 * Man kann sich von dieser Klasse eine kurze, textuelle Beschreibung der Map-Parameter geben lassen, mit der eine Map vollständig wieder hergestellt werden kann.
 * Das ist wichtig, um dem MapGenerator und dessen Modulen Bugs auszutreiben und das Questsystem vernünftig zu debuggen.
 * Ich bin bemüht, diese Beschreibungen so zu gestalten, das neue Versionen alle alten lesen und verarbeiten können.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MapParameters {
    
    /**
     * Die verwendeten Module und deren Parameter
     */
    private HashMap<String, HashMap<String, String>> settings;
    /**
     * Alle bekannten Module
     */
    private HashMap<String, Module> modules;
    
    /**
     * Erstellt neue MapParameter mit default-Einstellungen.
     * Diese können dann beliebig geändert werden.
     */
    public MapParameters() {
        modules = ModuleLoader.loadModules();
        loadDefaults();
    }
    
    public Set<String> getModules() {
        return settings.keySet();
    }
    
    public HashMap<String, String> getModuleParameters(String module) {
        if (module == null || module.isEmpty() || !settings.containsKey(module)) {
            throw new IllegalArgumentException("Illegal module identifier: " + module);
        }
        return settings.get(module);
    }
    
    /**
     * Überprüft, ob die Parameter sinnvoll sind und alle notwendigen Module vorhanden sind.
     * Garantiert nicht, dass der MapGenerator nicht noch Fehler findet.
     * @return 
     */
    public final boolean check() {
        // Alle Modulenamen bekannt?
        checkKnownModuleNames();
        // Wir brauchen entweder CREATE_RASTER oder CREATE_POLY _und_ RASTERIZE:
        if (!(selectionProvides("CREATE_RASTER") || (selectionProvides("CREATE_POLY") && selectionProvides("RASTERIZE")))) {
            System.out.println("ERROR: MAPPARAMS: Check failed, either CREATE_RASTER or CREATE_POLY and RASTERIZE required!");
            return false;
        }
        // Nicht mehrere CREATE:
        if (selectionProvides("CREATE_RASTER") && selectionProvides("CREATE_POLY")) {
            System.out.println("ERROR: MAPPARAMS: Check failed, found both CREATE_POLY and CREATE_RASTER");
            return false;
        }
        // Nicht CREATE_RASTER und RASTERIZE
        if (selectionProvides("CREATE_RASTER") && selectionProvides("RASTERIZE")) {
            System.out.println("ERROR: MAPPARAMS: Check failed, found both RASTERIZE and CREATE_RASTER");
            return false;
        }
        // Wenn CREATE_RASTER, dann dürfen keine Poly-Module da sein:
        if (selectionProvides("CREATE_RASTER") && !checkPolyModules()) {
            return false;
        }
        // In Reihenfolge bringen:
        ArrayList<String> polyModules = new ArrayList<>();
        ArrayList<String> rasterModules = new ArrayList<>();
        for (String moduleName: settings.keySet()) {
            Module module = modules.get(moduleName);
            // Hat das RASTERIZE?
            for (String var: module.provides()) {
                if (var.equals("RASTERIZE")) {
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
                return getPriority(o1) - getPriority(o2);
            }
        };
        Collections.sort(polyModules, comp);
        Collections.sort(rasterModules, comp);
        // Jetzt wieder hintereinander hängen:
        ArrayList<String> allSorted = new ArrayList<>(polyModules);
        allSorted.addAll(rasterModules);
        // Durchgehen, und schauen, ob Abhängigkeiten erfüllt sind.
        for (int i = 0; i < allSorted.size(); i++) {
            Module module = modules.get(allSorted.get(i));
            for (String requirement: module.requires()) {
                boolean requirementFulfilled = false;
                // Alle vorhergehenden Module prüfen, ob die das bereitstellen
                outer:
                for (int r = 0; r < i; r++) {
                    for (String provides: modules.get(allSorted.get(r)).provides()) {
                        if (provides.equals(requirement)) {
                            requirementFulfilled = true;
                            break outer;
                        }
                    }
                }
                if (!requirementFulfilled) {
                    System.out.println("ERROR: MAPPARAM: Module \"" + module.getName() + "\" requires " + requirement + ", but none of the previous modules provides it");
                    return false;
                }
            }
        }
        // Alles ok
        return true;
    }
    
    /**
     * Liest die PRIORITY-Angabe aus. Muss eine Zahl >= 0 sein.
     * Spukt Fehlermeldung aus, wenn nicht vorhanden.
     * @param params das modul
     * @return die gelesenen prio
     */
    private int getPriority(String module) {
        HashMap<String, String> params = settings.get(module);
        for (String var: params.keySet()) {
            if (var.equals("PRIORITY")) {
                return Integer.parseInt(params.get(var));
            }
        }
        throw new IllegalArgumentException("ERROR: MAPPARAMS: Check failed, required parameter PRIORITY missing! (module \"" + module + "\")");
    }
    
    /**
     * Prüft, ob Polygon-Module vorhanden sind.
     * Gibt Fehlermeldung aus, wenn ja.
     * @return true, wenn alles in Ordnung
     */
    private boolean checkPolyModules() {
        for (String moduleName: settings.keySet()) {
            if (modules.get(moduleName).computesPolygons()) {
                System.out.println("ERROR: MAPPARAMS: Check failed, found CREATE_RASTER and polygon-module \"" + moduleName + "\"");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Prüft, ob alle benannten Module überhaupt vorhanden sind.
     * Gibt Fehlermeldung aus, wenn es auf ein unbekanntes stößt.
     * @return true, wenn alle bekannt.
     */
    private boolean checkKnownModuleNames() {
        for (String moduleName: settings.keySet()) {
            if (!modules.containsKey(moduleName)) {
                System.out.println("ERROR: MAPPARAMS: Check failed, unknown module \"" + moduleName + "\"");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Überprüft, ob die derzeitigen Einstellungen die gegebene Funktion bereitstellen.
     * @param provides die gesuchte Funktion
     * @return true, wenn enthalten, false sonst
     */
    private boolean selectionProvides(String provides) {
        for (String moduleName: settings.keySet()) {
            for (String s: modules.get(moduleName).provides()) {
                if (s.equals(provides)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Die defaultEinstellungen
     */
    private void loadDefaults() {
        HashMap<String, String> modSettings = new HashMap<>();
        modSettings.put("SIZEX", String.valueOf(1024));
        modSettings.put("SIZEY", String.valueOf(1024));
        settings.put("oldmapgen", modSettings);
    }

}
