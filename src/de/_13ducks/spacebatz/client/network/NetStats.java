package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;

/**
 * Diese Klasse enthält stets aktuelle Statistikwerte, die für Netzwerk-Debugging verwendet werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class NetStats {

    /**
     * Die berechneten Tickdelays für die letzte Sekunde. Ein Ringbuffer.
     */
    private static int[] tickDelay = new int[Settings.SERVER_TICKRATE];
    /**
     * Summe aller Delays aus dem Tickdelay-Ringbuffer. Zum schnellen Berechnen des Durchschnitts.
     */
    private static int sumTickDelay = 0;
    /**
     * Der Zeiger auf die nächste Positon des Tickdelay-Ringbuffers.
     */
    private static int tickDelayClock = 0;

    /**
     * Liefert den letzten gemessenen Tickdelay.
     *
     * @return den letzten gemessenen Tickdelay
     */
    public static int getLastTickDelay() {
        return tickDelay[tickDelayClock != 0 ? tickDelayClock - 1 : Settings.SERVER_TICKRATE - 1];
    }

    /**
     * Liefert den durchschnittlichen Tickdelay der letzten Sekunde.
     *
     * @return den durchschnittlichen Tickdelay der letzten Sekunde
     */
    public static int getAvgTickDelay() {
        return sumTickDelay / Settings.SERVER_TICKRATE;
    }

    /**
     * Aufrufen, um ein neu gemessens TickDelay einzufügen.
     *
     * @param delay das neue Tickdelay.
     */
    public static void pushTickDelay(int delay) {
        int old = tickDelay[tickDelayClock];
        tickDelay[tickDelayClock] = delay;
        sumTickDelay -= old;
        sumTickDelay += delay;
        if (++tickDelayClock == Settings.SERVER_TICKRATE) {
            tickDelayClock = 0;
        }
    }
    /**
     * Ob der Net-Graph derzeit aktiv ist.
     */
    public static boolean netGraph = Settings.CLIENT_NETGRAPH_ON;
    /**
     * Wieviele Pakete ankommen.
     */
    private static int inCounter = 0;
    /**
     * Wieviele Bytes ankommen.
     */
    private static int inBytes = 0;

    /**
     * Signalisieren, dass ein neues Paket angekommen ist.
     *
     * @param bytes wieviele Bytes angekommen sind.
     */
    public static void inPack(int bytes) {
        inCounter++;
        inBytes += bytes;
    }

    /**
     * Liefert den InCounter und setzt ihn zurück.
     *
     * @return den InCounter
     */
    public static int getAndResetInCounter() {
        int temp = inCounter;
        inCounter = 0;
        return temp;
    }

    /**
     * Liefert InBytes und setzt ihn zurück.
     *
     * @return InBytes
     */
    public static int getAndResetInBytes() {
        int temp = inBytes;
        inBytes = 0;
        return temp;
    }
    /**
     * Der Ping-Wert, wird vom Netzwerksystem ermittelt.
     */
    public static int ping;

    /**
     * Privater Konstruktor, da Utility-Class.
     */
    private NetStats() {
    }
}
