package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SHADOW_CHANGE;
import de._13ducks.spacebatz.util.mapgen.data.Vector;
import java.util.Collections;
import java.util.Comparator;
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
            final byte[][] serverShadow = Server.game.getLevel().shadow;
            for (Client c : Server.game.clients.values()) {
                Player p = c.getPlayer();
                boolean updateClients = false;
                // Mittelpunkt. Hier startet die Suche.
                int px = (int) p.getX();
                int py = (int) p.getY();
                // Das erste Feld nicht bearbeiten, das ist immer hell:
                if (serverShadow[px][py] != 0) {
                    serverShadow[px][py] = 0;
                    updateClients = true;
                }
                // Liste mit allen Feldern im Umkreis bauen
                LinkedList<Vector> queue = new LinkedList<>();
                // Alle Elemente einfüllen:
                for (int x = -14; x <= 14; x++) {
                    for (int y = -14; y <= 14; y++) {
                        // Das Startfeld nicht:
                        if (x == 0 && y == 0) {
                            continue;
                        }
                        if (checkPosition(px, py, px + x, py + y)) {
                            queue.add(new Vector(px + x, py + y));
                        }
                    }
                }
                // Sortierung: Licht zuerst
                Comparator<Vector> sorter = new Comparator<Vector>() {
                    @Override
                    public int compare(Vector o1, Vector o2) {
                        return Integer.compare(serverShadow[(int) o1.x][(int) o1.y], serverShadow[(int) o2.x][(int) o2.y]);
                    }
                };
                // Alle Elemente durchgehen
                while (!queue.isEmpty()) {
                    // Erst sortieren
                    Collections.sort(queue, sorter);
                    // Position aus Liste nehmen
                    Vector position = queue.remove();
                    // Jetzt dieses Feld bearbeiten:
                    // Maximale Helligkeit der Nachbarfelder hängt von der Lichtstufe dieses Felds ab:
                    int light = shadowAt(position.x , position.y);
                    // Erhöht die Textur dieses Feldes den Schattenwert?
                    if (Server.game.getLevel().getGround()[(int) position.x][(int) position.y] != 3) {
                        light += 32;
                    }
                    if (light > 127) {
                       light = 127;
                    }
                    // Schatten der Nachbarn senken, falls nötig:
                    if (checkPosition(px, py,(int) position.x + 1, (int) position.y) && serverShadow[(int) position.x + 1][(int) position.y] > light) {
                        serverShadow[(int) position.x + 1][(int) position.y] = (byte) light;
                        updateClients = true;
                    }
                    if (checkPosition(px, py,(int) position.x - 1, (int) position.y) && serverShadow[(int) position.x - 1][(int) position.y] > light) {
                        serverShadow[(int) position.x - 1][(int) position.y] = (byte) light;
                        updateClients = true;
                    }
                    if (checkPosition(px, py,(int) position.x, (int) position.y + 1) && serverShadow[(int) position.x][(int) position.y + 1] > light) {
                        serverShadow[(int) position.x][(int) position.y + 1] = (byte) light;
                        updateClients = true;
                    }
                    if (checkPosition(px, py,(int) position.x, (int) position.y - 1) && serverShadow[(int) position.x][(int) position.y - 1] > light) {
                        serverShadow[(int) position.x][(int) position.y - 1] = (byte) light;
                        updateClients = true;
                    }
                }
                // Den Clients senden?
                if (updateClients) {
                    STC_SHADOW_CHANGE.sendShadowChange(px, py);
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
        return x >= 0
                && y >= 0
                && x < Server.game.getLevel().shadow.length
                && y < Server.game.getLevel().shadow[0].length
                && Math.abs(x - px) < 16
                && Math.abs(y - py) < 16;
    }

    private int shadowAt(double x, double y) {
        int tx = (int) x;
        int ty = (int) y;
        if (tx >= 0 && ty >= 0 && tx < Server.game.getLevel().shadow.length && ty < Server.game.getLevel().shadow[0].length) {
            return Server.game.getLevel().shadow[tx][ty];
        }
        return 127;
    }
}
