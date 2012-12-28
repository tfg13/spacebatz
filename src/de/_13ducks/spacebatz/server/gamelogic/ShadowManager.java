package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SHADOW_CHANGE;
import de._13ducks.spacebatz.util.mapgen.data.Vector;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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
            for (Client c : Server.game.clients.values()) {
                Player p = c.getPlayer();
                byte[][] changeMap = new byte[31][31];
                // Mittelpunkt. Hier startet die Suche.
                int px = (int) p.getX();
                int py = (int) p.getY();
                LinkedList<Vector> openQueue = new LinkedList<>();
                LinkedHashSet<Vector> containopen = new LinkedHashSet<>();  // Auch für entdeckte Knoten, hiermit kann viel schneller festgestellt werden, ob ein bestimmter Knoten schon enthalten ist.
                LinkedHashSet<Vector> closed = new LinkedHashSet<>();    // Liste für fertig bearbeitete Knoten
                // Startknoten einfügen
                openQueue.add(new Vector(px, py));
                containopen.add(new Vector(px, py));
                // A-Star-mäßig beim Bearbeiten Nachbarn entdecken
                while (!openQueue.isEmpty()) {
                    // Position aus Liste nehmen
                    Vector position = openQueue.remove();
                    containopen.remove(position);
                    closed.add(position);
                    // Nachbarfelder entdecken, fals noch nicht bekannt:
                    Vector top = new Vector(position.x, position.y + 1);
                    Vector right = new Vector(position.x + 1, position.y);
                    Vector bottom = new Vector(position.x, position.y - 1);
                    Vector left = new Vector(position.x - 1, position.y);
                    if (!containopen.contains(top) && !closed.contains(top) && checkPosition(px, py, (int) top.x, (int) top.y)) {
                        openQueue.add(top);
                        containopen.add(top);
                    }
                    if (!containopen.contains(right) && !closed.contains(right) && checkPosition(px, py, (int) right.x, (int) right.y)) {
                        openQueue.add(right);
                        containopen.add(right);
                    }
                    if (!containopen.contains(bottom) && !closed.contains(bottom) && checkPosition(px, py, (int) bottom.x, (int) bottom.y)) {
                        openQueue.add(bottom);
                        containopen.add(bottom);
                    }
                    if (!containopen.contains(left) && !closed.contains(left) && checkPosition(px, py, (int) left.x, (int) left.y)) {
                        openQueue.add(left);
                        containopen.add(left);
                    }
                    // Das erste Feld nicht bearbeiten, das ist immer hell:
                    if (position.x == px && position.y == py) {
                        changeMap[(int) position.x - px + 15][(int) position.y - py + 15] = 0;
                        continue;
                    }
                    // Jetzt dieses Feld bearbeiten:
                    // Niedrigsten Schattenwert der Nachbarn finden:
                    int light = Math.min(Math.min(shadowAt(top), shadowAt(right)), Math.min(shadowAt(bottom), shadowAt(left)));
                    // Erhöht die Textur dieses Feldes den Schattenwert?
                    if (Server.game.getLevel().getGround()[(int) position.x][(int) position.y] != 3) {
                        light += 32;
                    }
                    if (light > 127) {
                        light = 127;
                    }
                    changeMap[(int) position.x - px + 15][(int) position.y - py + 15] = (byte) light;
                }
                // Schatten berechnet - Übernehmen:
                boolean updateClients = false;
                for (int x = -15; x <= 15; x++) {
                    for (int y = -15; y <= 15; y++) {
                        if (px + x < 0 || py + y < 0 || px + x >= Server.game.getLevel().shadow.length || py + y >= Server.game.getLevel().shadow[0].length) {
                            continue;
                        }
                        // Nicht abdunkeln, deshalb >
                        if (Server.game.getLevel().shadow[px + x][py + y] > changeMap[15 + x][15 + y]) {
                            updateClients = true;
                            Server.game.getLevel().shadow[px + x][py + y] = changeMap[15 + x][15 + y];
                        }
                    }
                }
                // Den Clients senden?
                if (updateClients) {
                    STC_SHADOW_CHANGE.sendShadowChange(px, py, changeMap);
                }
            }
        }
    }

    /**
     * Prüft, ob die Koordinate erlaubt ist.
     * Dazu muss sie innerhalb der Map liegen und innerhalb der changeMap.
     *
     * @param px Startpunkt der Beleuchtungsberechnung X
     * @param py Startpunkt der Beleuchtungsberechnung Y
     * @param x Zu testende Koordinate X
     * @param y Zu testende Koordinate Y
     * @return true, wenn Feld ok
     */
    private boolean checkPosition(int px, int py, int x, int y) {
        return x > 0
                && y > 0
                && x < Server.game.getLevel().shadow.length
                && y < Server.game.getLevel().shadow[0].length
                && Math.abs(x - px) < 16
                && Math.abs(y - py) < 16;
    }

    private int shadowAt(Vector position) {
        if (position.x > 0 && position.y > 0 && position.x < Server.game.getLevel().shadow.length && position.y < Server.game.getLevel().shadow[0].length) {
            return Server.game.getLevel().shadow[(int) position.x][(int) position.y];
        }
        return 127;
    }
}
