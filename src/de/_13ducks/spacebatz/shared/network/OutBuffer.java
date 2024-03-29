package de._13ducks.spacebatz.shared.network;

import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * Speichert Pakete so lange, bis die Gegenseite den Empfang bestätigt hat.
 * Kann z.B. eine Liste aller noch nicht bestätigten Pakete ausspucken.
 * Hat eine Maximalgröße. Wird diese überschritten bedeutet das, dass die Verbindung so schlecht ist, dass ein reconnect notwendig wird.
 * Verwaltet eine Zeit, nach der die Pakete bestätigt sein müssen. Sind sie bis dahin nicht bestätigt, so müssen sie neu gesendet werden.
 * Bestätigte Pakete werden automatisch gelöscht.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OutBuffer {

    /**
     * Wie viele Pakete maximal zwischengespeichert werden.
     * Pakete werden erst dann gelöscht, wenn alle vorherigen garantiert empfangen wurden.
     * Gibt also auch die Zeitspanne für disconnects an, die das System maximal überbrücken kann.
     */
    private static final int MAXIMUM_SIZE = 600; // etwa 10 Sekunden bei normaler Paketrate. In der Praxis etwas weniger.
    /**
     * Wie lange auf ein ACK gewartet werden soll, bevor das Paket noch einmal gesendet wird.
     * Bestimmt im wesentlichen die maximale Ping, die noch lagfrei funktioniert.
     * Wenn der Wert zu hoch eingestellt wird, muss die Prediction mehr vorhersagen und wird schlechter.
     */
    private static final int MAX_ACKTIME_MS = 100; // erlaubt pings von 100 ms lagfrei. Lerp muss dann mindestens 150 sein.
    /**
     * Der eigentliche Puffer.
     * Organisiert als Ringbuffer.
     * Ein Element muss immer null bleiben, um den Anfang zu signalisieren
     */
    private Buffer[] buffer = new Buffer[MAXIMUM_SIZE];
    /**
     * Zeigt auf den Anfang des Ringbuffers.
     */
    private int ringBufferFirst = 0;
    /**
     * Zeigt auf das letzte Element des Ringbuffers.
     * Ist nicht definiert, falls der Ringbuffer leer ist.
     */
    private int ringBufferLast = 0;
    /**
     * Anzahl Elemente im Ringbuffer. Muss immer kleiner MAXIMUM_SIZE sein.
     */
    private int bufferSize = 0;
    /**
     * Zählt, wie oft Pakete neu gesendet werden, weil ihre ACK-Zeit abgelaufen ist.
     */
    private int numberOfRetransmits;

    /**
     * Fügt ein Paket dem Puffer hinzu.
     * Wird bei der nächsten Abfrage, was gesendet werden muss mitgeliefert.
     * ID darf noch nicht vorhanden sein und muss genau eins größer sein als die vorherige (falls existent. Wrap-around!).
     * Returned true, wenn einfügen funktioniert hat (also Platz genug war)
     *
     * @param packet das fertig gecraftete Paket
     * @param packID die ID dieses Pakets
     */
    public synchronized boolean registerPacket(DatagramPacket packet, int packID) {
        if (packet == null) {
            throw new IllegalArgumentException("Packet must not be null");
        }
        if (buffer[ringBufferFirst] == null) {
            // leer, Sonderbehandlung
            buffer[ringBufferFirst] = new Buffer(packet, packID);
            ringBufferLast = ringBufferFirst;
            bufferSize++;
            return true;
        } else {
            // Normal. Testen ob Platz:
            if (bufferSize == MAXIMUM_SIZE - 1) {
                return false;
            }
            // Id muss eins größer sein als Vorgänger:
            int expectedID = buffer[ringBufferLast].packID + 1;
            // Wrap-around:
            if (expectedID == Constants.OVERFLOW_STC_PACK_ID) {
                expectedID = 0;
            }
            if (packID != expectedID) {
                throw new IllegalArgumentException("Irregular Paket id. Should be " + (expectedID) + " but is " + packID);
            }
            // Alles ok. Aufnehmen:
            if (++ringBufferLast == MAXIMUM_SIZE) {
                ringBufferLast = 0;
            }
            buffer[ringBufferLast] = new Buffer(packet, packID);
            bufferSize++;
            return true;
        }
    }

    /**
     * Liefert eine Liste aller Pakete, die gesendet werden sollen.
     * Es wird angenommen, dass diese Pakete dann auch sofort gesendet werden.
     *
     * @return eine Liste aller Pakete, die gesendet werden sollen.
     */
    public synchronized ArrayList<DatagramPacket> packetsToSend() {
        final long time = System.currentTimeMillis();
        ArrayList<DatagramPacket> sendList = new ArrayList<>();
        int ringBufferIter = ringBufferFirst;
        while (buffer[ringBufferIter] != null) {
            Buffer buf = buffer[ringBufferIter];

            if (buf.sendTime == 0) { // Überhaupt schon einmal gesendet?
                sendList.add(buf.pack);
                buf.sendTime = time;
            } else {
                // Noch nicht ACK und Zeit abgelaufen?
                if (!buf.acked && (time - buf.sendTime) >= MAX_ACKTIME_MS) {
                    // Neu senden
                    sendList.add(buf.pack);
                    buf.sendTime = time;
                    numberOfRetransmits++;
                }
            }

            // Increment
            if (++ringBufferIter == MAXIMUM_SIZE) {
                ringBufferIter = 0;
            }
        }
        return sendList;
    }

    /**
     * Muss aufgerufen werden, wenn eine Bestätigung für ein Paket empfangen wurde.
     * Löscht dieses Paket, falls es das erste im Puffer ist. In diesem Fall werden auch alle folgenden Pakete gelöscht, die schon bestätigt wurden.
     * Ignoriert Pakete, deren ID gar nicht bekannt ist.
     *
     * @param packID die packID des Pakets, das bestätigt wurde
     */
    public synchronized void ackPacket(int packID) {
        // Paket suchen.
        int ringBufferIter = ringBufferFirst;
        while (buffer[ringBufferIter] != null) {
            if (buffer[ringBufferIter].packID == packID) {
                break; // Gefunden
            }

            // Increment
            if (++ringBufferIter == MAXIMUM_SIZE) {
                ringBufferIter = 0;
            }
        }
        // Gefunden?
        if (buffer[ringBufferIter] != null) {
            buffer[ringBufferIter].acked = true;
            // Aufräumen?
            while (buffer[ringBufferFirst] != null && buffer[ringBufferFirst].acked) {
                buffer[ringBufferFirst] = null;
                bufferSize--;
                // Increment
                if (++ringBufferFirst == MAXIMUM_SIZE) {
                    ringBufferFirst = 0;
                }
            }
        }
    }

    /**
     * Gibt die Auslastung des Puffers in ganzen Prozent zurück.
     *
     * @return die Auslastung des Puffers in ganzen Prozent
     */
    public int getBufferRatio() {
        return (int) (100.0 * bufferSize / MAXIMUM_SIZE);
    }

    /**
     * Liefert die Anzahl von Paketen zurück, die erneut versendet wurden.
     * Setzt diese Anzahl anschließend zurück.
     *
     * @return Anzahl von Resends
     */
    public int getAndResetRetransmitCounter() {
        int result = numberOfRetransmits;
        numberOfRetransmits = 0;
        return result;
    }

    private class Buffer {

        private final DatagramPacket pack;
        private final int packID;
        private long sendTime;
        private boolean acked = false;

        private Buffer(DatagramPacket pack, int id) {
            this.pack = pack;
            this.packID = id;
        }
    }
}
