package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SHADOW_CHANGE;
import de._13ducks.spacebatz.util.mapgen.data.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Berechnet das Beleuchtungssystem
 * Läuft aus Performancegründen nur 1 mal pro Sekunde.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ShadowManager {

    /**
     * Zufälliger Wert zwischen 0 und Servertickrate.
     * Zur automatischen Lastverteilung, immer bei diesem (x-ten) Tick einer Sekunde werden Beleuchtungsberechnungen durchgeführt
     */
    private final int randomTick = (int) (Math.random() * (1 / Settings.SERVER_TICKRATE));

    /**
     * Muss jeden Tick aufgerufen werden, arbeitet aber nur selten wirklich.
     * Verteilt die Last selbstständig.
     */
    public void tick() {
        if (Server.game.getTick() % Settings.SERVER_TICKRATE == randomTick) {
            // Jetzt tatsächlich arbeiten:
            ArrayList<Vector> updatedChunks = new ArrayList<>();
            for (Client c : Server.game.clients.values()) {
                Player p = c.getPlayer();
                updatedChunks.addAll(lightShadows((int) p.getX(), (int) p.getY(), 20, 20, (byte) 0, (byte) 8, (byte) 32, 4, Server.game.getLevel().shadow, Server.game.getLevel().getGround()));
            }
            for (Vector v : updatedChunks) {
                STC_SHADOW_CHANGE.sendShadowChange(v);
            }
        }
    }

    /**
     * Führt auf der gegebenen Schattenmap eine Lichtberechnung durch.
     * Liefert eine Liste von geänderten Chunks zurück. Diese kann z.B. verwendet werden, um die Clients zu syncen.
     *
     * @param lightX Position der Lichtquelle, X
     * @param lightY Position der Lichtquelle, Y
     * @param maxLightX Berechnungsreichweite X (einfach, rechnet dann soviel in Richtung + und -)
     * @param maxLightY Berechnungsreichweite Y (einfach, rechnet dann soviel in Richtung + und -)
     * @param initialShadow Helligkeit der Lichtquelle am Startpunkt der Beleuchtung
     * @param freeDarkening Um wieviel Freiflächen die Sicht einschränken
     * @param wallDarkening Um wieviel Wände die Sicht einschränken
     * @param fullVisFreeSightDistance Wieviele Blöcke weit man auf Freiflächen uneingeschränkt sehen kann (also ohne das freeDarkening) (Abstand ist euklid)
     * @param shadowMap die Shadowmap, die verändert werden soll
     * @param texMap die Texturmap, wird konsultiert, um herauszufinden, wo Freiflächen/Hindernisse sind
     * @return eine Liste mit modifizierten Schatten-Chunks
     */
    public static Set<Vector> lightShadows(int lightX, int lightY, int maxLightX, int maxLightY, byte initialShadow, byte freeDarkening, byte wallDarkening, double fullVisFreeSightDistance, final byte[][] shadowMap, final int[][] texMap) {
        // Hier die geupdateten Chunks speichern
        HashSet<Vector> updatedChunks = new HashSet<>();
        // Das erste Feld nicht bearbeiten, das bekommt initalShadow
        if (shadowMap[lightX][lightY] > initialShadow) {
            shadowMap[lightX][lightY] = initialShadow;
            updatedChunks.add(chunkFor(lightX, lightY));
        }
        // Liste mit allen Feldern im Umkreis bauen
        LinkedList<Vector> queue = new LinkedList<>();
        // Alle Elemente einfüllen:
        for (int x = -maxLightX; x <= maxLightX; x++) {
            for (int y = -maxLightY; y <= maxLightY; y++) {
                // Keine Felder außerhalb der Map:
                if (lightX + x < 0 || lightY + y < 0 || lightX + x >= shadowMap.length || lightY + y >= shadowMap[0].length) {
                    continue;
                }
                queue.add(new Vector(lightX + x, lightY + y));
            }
        }
        // Sortierung: Licht zuerst
        Comparator<Vector> sorter = new Comparator<Vector>() {
            @Override
            public int compare(Vector o1, Vector o2) {
                return Integer.compare(shadowMap[(int) o1.x][(int) o1.y], shadowMap[(int) o2.x][(int) o2.y]);
            }
        };
        int lastLight = -1;
        // Alle Elemente durchgehen
        while (!queue.isEmpty()) {
            // Performance-Optimierung: Nur sortieren, wenn Helligkeit des nächsten abweicht:
            if (lastLight != shadowMap[(int) queue.getFirst().x][(int) queue.getFirst().y]) {
                // Erst sortieren
                Collections.sort(queue, sorter);
            }
            // Position aus Liste nehmen
            Vector position = queue.remove();
            // Jetzt dieses Feld bearbeiten:
            // Maximale Helligkeit der Nachbarfelder hängt von der Lichtstufe dieses Felds ab:
            int light = shadowMap[(int) position.x][(int) position.y];
            lastLight = light;
            // Erhöht die Textur dieses Feldes den Schattenwert?
            if (texMap[(int) position.x][(int) position.y] != 3 && texMap[(int) position.x][(int) position.y] != 5) {
                light += wallDarkening;
            } else {
                // Weit genug weg, dass es auch auf Freiflächen abdunkelt?
                if (Math.sqrt((position.x - lightX) * (position.x - lightX) + (position.y - lightY) * (position.y - lightY)) > fullVisFreeSightDistance) {
                    light += freeDarkening;
                }
            }
            if (light > 127) {
                light = 127;
            }
            // Schatten der Nachbarn senken, falls nötig:
            if (position.x + 1 >= 0 && position.y >= 0 && position.x + 1 < shadowMap.length && position.y < shadowMap[0].length && shadowMap[(int) position.x + 1][(int) position.y] > light) {
                shadowMap[(int) position.x + 1][(int) position.y] = (byte) light;
                updatedChunks.add(chunkFor((int) position.x + 1, (int) position.y));
            }
            if (position.x - 1 >= 0 && position.y >= 0 && position.x - 1 < shadowMap.length && position.y < shadowMap[0].length && shadowMap[(int) position.x - 1][(int) position.y] > light) {
                shadowMap[(int) position.x - 1][(int) position.y] = (byte) light;
                updatedChunks.add(chunkFor((int) position.x - 1, (int) position.y));
            }
            if (position.x >= 0 && position.y + 1 >= 0 && position.x < shadowMap.length && position.y + 1 < shadowMap[0].length && shadowMap[(int) position.x][(int) position.y + 1] > light) {
                shadowMap[(int) position.x][(int) position.y + 1] = (byte) light;
                updatedChunks.add(chunkFor((int) position.x, (int) position.y + 1));
            }
            if (position.x >= 0 && position.y - 1 >= 0 && position.x < shadowMap.length && position.y - 1 < shadowMap[0].length && shadowMap[(int) position.x][(int) position.y - 1] > light) {
                shadowMap[(int) position.x][(int) position.y - 1] = (byte) light;
                updatedChunks.add(chunkFor((int) position.x, (int) position.y - 1));
            }
        }
        return updatedChunks;
    }

    /**
     * Liefert den Chunk für eine gegebene Koordinate
     *
     * @param x X
     * @param y Y
     * @return den Chunk als Vector
     */
    private static Vector chunkFor(int x, int y) {
        return new Vector(x / 8, y / 8);
    }
}
