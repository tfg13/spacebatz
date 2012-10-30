package de._13ducks.spacebatz.util.mapgen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
    private HashMap<String, HashMap<String, String>> settings = new HashMap<>();
    /**
     * Alle bekannten Module
     */
    private HashMap<String, Module> modules;
    /**
     * Aus diesem Seed werden die Seeds für die Module berechnet.
     */
    long masterSeed;

    /**
     * Erstellt neue MapParameter mit default-Einstellungen.
     * Diese können dann beliebig geändert werden.
     */
    public MapParameters() {
        modules = ModuleLoader.loadModules();
        loadDefaults();
    }

    /**
     * Erstellt neue MapParameter, importiert dabei die im String codierten Einstellungen.
     * Der String muss ein spezielles Format haben, das von der export()-Methode erzeugt wird.
     *
     * @param s die codierten Map-Parameter.
     */
    public MapParameters(String s) throws IOException {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Map-String must neither be null nor empty!");
        }
        modules = ModuleLoader.loadModules();
        // Grundlegendes Format prüfen:
        if (!s.matches("MAP\\d{2}(\\s|.)+MAP")) {
            throw new IllegalArgumentException("Wrong syntax!");
        }
        // Version lesen
        int version = Integer.valueOf(s.substring(3, 5));
        // Compatibility-Conversions
        switch (version) {
            case 01:
                // Default-Version
                break;
            default:
                System.out.println("Unknown format version, will try to import without conversions...");
        }
        // Inhalt raussägen, decodieren, entpacken
        BASE64Decoder dec = new BASE64Decoder();
        byte[] compressed = dec.decodeBuffer(s.substring(5, s.length() - 3));
        GZIPInputStream unzipper = new GZIPInputStream(new ByteArrayInputStream(compressed));
        byte[] buf = new byte[1024 * 8]; // 8K sollten genug sein, bei Bedarf einfach erhöhen.
        int size = unzipper.read(buf);
        byte[] realData = new byte[size];
        System.arraycopy(buf, 0, realData, 0, size);
        String params = new String(realData, "UTF8");

        // Einstellungen auslesen
        String[] lines = params.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // Anzahl Argumente (zusätzliche Zeilen) lesen:
            String[] words = line.split("\\s+");
            int additionalLines = Integer.parseInt(words[1]);
            if (words[0].equals("MAIN")) {
                masterSeed = Long.parseLong(lines[++i]);
            } else {
                Module m = modules.get(words[0]);
                HashMap<String, String> moduleParam = new HashMap<>();
                settings.put(m.getName(), moduleParam);
                for (int j = 1; j <= additionalLines; j++) {
                    String argLine = lines[i + j];
                    moduleParam.put(argLine.substring(0, argLine.indexOf(" ")), argLine.substring(argLine.indexOf(" ") + 1));
                }
                i += additionalLines;
            }
        }
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
     *
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
        moduleLoop:
        for (String moduleName : settings.keySet()) {
            Module module = modules.get(moduleName);
            // Hat das RASTERIZE?
            for (String var : module.provides()) {
                if (var.equals("RASTERIZE")) {
                    // Dann nicht einsortieren
                    continue moduleLoop;
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
            for (String requirement : module.requires()) {
                boolean requirementFulfilled = false;
                // Alle vorhergehenden Module prüfen, ob die das bereitstellen
                outer:
                for (int r = 0; r < i; r++) {
                    for (String provides : modules.get(allSorted.get(r)).provides()) {
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
     *
     * @param params das modul
     * @return die gelesenen prio
     */
    int getPriority(String module) {
        HashMap<String, String> params = settings.get(module);
        for (String var : params.keySet()) {
            if (var.equals("PRIORITY")) {
                return Integer.parseInt(params.get(var));
            }
        }
        throw new IllegalArgumentException("ERROR: MAPPARAMS: Check failed, required parameter PRIORITY missing! (module \"" + module + "\")");
    }

    /**
     * Prüft, ob Polygon-Module vorhanden sind.
     * Gibt Fehlermeldung aus, wenn ja.
     *
     * @return true, wenn alles in Ordnung
     */
    private boolean checkPolyModules() {
        for (String moduleName : settings.keySet()) {
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
     *
     * @return true, wenn alle bekannt.
     */
    private boolean checkKnownModuleNames() {
        for (String moduleName : settings.keySet()) {
            if (!modules.containsKey(moduleName)) {
                System.out.println("ERROR: MAPPARAMS: Check failed, unknown module \"" + moduleName + "\"");
                return false;
            }
        }
        return true;
    }

    /**
     * Überprüft, ob die derzeitigen Einstellungen die gegebene Funktion bereitstellen.
     *
     * @param provides die gesuchte Funktion
     * @return true, wenn enthalten, false sonst
     */
    private boolean selectionProvides(String provides) {
        for (String moduleName : settings.keySet()) {
            for (String s : modules.get(moduleName).provides()) {
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
        HashMap<String, String> polySettings = new HashMap<>();
        polySettings.put("PRIORITY", String.valueOf(0));
        polySettings.put("polynumber", String.valueOf(1000));
        polySettings.put("smooth", String.valueOf(2));
        settings.put("polymapgen", polySettings);
        HashMap<String, String> polyVisSettings = new HashMap<>();
        polyVisSettings.put("PRIORITY", String.valueOf(2));
        settings.put("polyVisualizer", polyVisSettings);
        HashMap<String, String> borderSettings = new HashMap<>();
        borderSettings.put("PRIORITY", String.valueOf(1));
        settings.put("bordergenerator", borderSettings);
        settings.put("rasterizer", new HashMap<String, String>());
        masterSeed = new Random().nextLong();
    }

    /**
     * Liefert eine speicher und übertragbare, textuelle Darstellung der aktuellen Konfiguration.
     * Mit diesem String können die MapParameters und damit die gesamte Map vollständig rekonstruiert werden.
     * Der einfließende Zufall ist enhalten, es ist also garantiert, dass mit der selben Spielversion exakt die selbe Map wieder gebaut wird.
     * Die Darstellung ist ein Base64-codiertes zip der Parameter.
     *
     * @return eine Textdarstellung der MapParameter
     */
    public String export() throws UnsupportedEncodingException, IOException {
        StringBuilder builder = new StringBuilder();
        // Header (version 01)
        builder.append("MAP01");
        // Alle Optionen codieren:
        StringBuilder params = new StringBuilder();
        // Allgemeine Sachen (z.B. Masterseed)
        params.append("MAIN 1\n");
        params.append(masterSeed).append("\n");
        // Alle Module
        for (String module : settings.keySet()) {
            HashMap<String, String> moduleSettings = settings.get(module);
            params.append(module).append(" ").append(moduleSettings.size()).append('\n');
            for (String moduleVariable : moduleSettings.keySet()) {
                params.append(moduleVariable).append(" ").append(moduleSettings.get(moduleVariable)).append('\n');
            }
        }
        // Settings-String komprimieren:
        byte[] paramsUncompressed = params.toString().getBytes("UTF8");
        ByteArrayOutputStream compRes = new ByteArrayOutputStream();
        GZIPOutputStream compress = new GZIPOutputStream(compRes, paramsUncompressed.length);
        compress.write(paramsUncompressed);
        compress.close();
        BASE64Encoder enc = new BASE64Encoder();
        String encodedComp = enc.encode(compRes.toByteArray());
        builder.append(encodedComp);
        builder.append("MAP");
        return builder.toString();
    }
}
