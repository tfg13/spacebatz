package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.shared.network.StatisticRingBuffer;

/**
 * Sammelt Statusinformationen über die Auslastung der Netzwerkverbindung zu einem Client.
 * Dazu zählen:
 * - Pakete pro Zeiteinheit, verwendete Größe der Pakete, Anzahl Befehle pro Paket
 * - Re-Request-Rate von Paketen
 * - Auslastung der Retransmit-Puffer
 * - Auslastung des Puffers für ausgehende Befehle
 * - Absolute Lag Factor (ALF) (berechnet vor allem aus den beiden drüber)
 *
 * Mit diesen Informationen kann das Netzwerksystem zukünftig versuchen, die Last besser automatisch zu regeln.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ServerNetStats {

    /**
     * Größe der Puffer für Rolling-Checksums in Ticks.
     * Bestimmt im Wesentlichen die Reaktionsgeschwindigkeit des Netzwerksystems auf Änderungen an der Last oder der Übertragungsqualität.
     * Kleinere Werte lassen das System schneller reagieren, machen es aber auch empfindlicher gegenüber normalen Schwankungen.
     * Größere Werte haben dieses Problem weniger, können aber bei Lastspitzen oder plötzlichen Verschlechterungen der Verbindung Lags verschlimmern.
     */
    private static final int SURVEILLANCE_WINDOW_SIZE = 66; // Eine Sekunde
    /**
     * Die absolute Anzahl erfasster Netzwerkpakete.
     * Wird zur Online-Durchschnittsberechnung benötigt.
     */
    private long trackedPackets;
    /**
     * Die absolute Anzahl erfasster Ticks.
     * Wird zur Online-Durchschnittsberechnung benötigt.
     */
    private long trackedTicks;
    /**
     * Die durchschnittliche Anzahl von normalen Befehlen pro Netzwerkpaket.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgNumberOfCmdsPerPacket;
    /**
     * Die durchschnittliche Anzahl von Prio-Befehlen pro Netzwerkpaket (ohne Ultra-Prio).
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgNumberOfPrioCmdsPerPacket;
    /**
     * Die durchschnittliche Größe eines normalen Befehls in Byte.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgCmdSize;
    /**
     * Die durchschnittliche Größe eines Prio-Befehls in Byte (ohne Ultra-Prio).
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgPrioCmdSize;
    /**
     * Die durchschnittliche Auslastung einzelner Pakete.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgLoadPerPacket;
    /**
     * Die durchschnittliche Anzahl versendeter Pakete pro Tick.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgNumberOfPacketsPerTick;
    /**
     * Die durchschnittliche Auslastung des Puffers für ausgehende Pakete.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgOutBufferLoad;
    /**
     * Die durchschnittliche Anzahl an Paketen, die neu übertragen wurden pro Tick.
     * Online-Durchschnitt, wird nie zurückgesetzt.
     */
    private double avgNumberOfRetransmitsPerTick;
    /**
     * Verwaltet den aktuellen Durchschnittswert von Paketen pro Tick.
     * Ringpuffer, enthält nur aktuelle Werte.
     */
    private StatisticRingBuffer recentNumberOfPacketsPerTick = new StatisticRingBuffer(SURVEILLANCE_WINDOW_SIZE);
    /**
     * Verwaltet den aktuellen Durchschnittswert der Auslastung des Puffers für ausgehende Pakete.
     * Ringpuffer, enthält nur aktuelle Werte.
     */
    private StatisticRingBuffer recentOutBufferLoad = new StatisticRingBuffer(SURVEILLANCE_WINDOW_SIZE);
    /**
     * Verwaltet den aktuellen Durchschnittswert der Anzahl Befehle im Befehlspuffer.
     * Ringpuffer, enthält nur aktuelle Werte.
     */
    private StatisticRingBuffer recentOutQueueSize = new StatisticRingBuffer(SURVEILLANCE_WINDOW_SIZE);
    /**
     * Verwaltet den aktuellen Durchschnittswert der Anzahl Prio-Befehle im Befehlspuffer. (Ohne Ultra-Prio)
     * Ringpuffer, enthält nur aktuelle Werte.
     */
    private StatisticRingBuffer recentPrioOutQueueSize = new StatisticRingBuffer(SURVEILLANCE_WINDOW_SIZE);
    /**
     * Verwaltet den aktuellen Durchschnittswert der Anzahl Pakete, die pro Tick erneut übertragen werden mussten.
     * Ringpuffer, enthält nur aktuelle Werte.
     */
    private StatisticRingBuffer recentRetransmitNumber = new StatisticRingBuffer(SURVEILLANCE_WINDOW_SIZE);

    /**
     * Aufrufen, um ein neu gebautes Paket zu erfassen.
     *
     * @param numberOfNormalCmds Anzahl Befehle in diesem Paket
     * @param numberOfPrioCmds Anzahl Prio-Befehle in diesem Paket (ohne Ultra-Prio)
     * @param avgNormalCmdSize Durchschnittliche Größe eines Befehls in diesem Paket
     * @param avgPrioCmdSize Durchschnittliche Größe eines Prio-Befehls in diesem Paket (ohne Ultra-Prio)
     */
    void craftedPacket(int numberOfNormalCmds, int numberOfPrioCmds, double avgNormalCmdSize, double avgPrioCmdSize) {
        trackedPackets++;
        // Durchschnitte updaten:
        avgNumberOfCmdsPerPacket = avgNumberOfCmdsPerPacket + ((numberOfNormalCmds - avgNumberOfCmdsPerPacket) / trackedPackets);
        avgNumberOfPrioCmdsPerPacket = avgNumberOfPrioCmdsPerPacket + ((numberOfPrioCmds - avgNumberOfPrioCmdsPerPacket) / trackedPackets);
        avgCmdSize = avgCmdSize + ((avgNormalCmdSize - avgCmdSize) / trackedPackets);
        this.avgPrioCmdSize = this.avgPrioCmdSize + ((avgPrioCmdSize - this.avgPrioCmdSize) / trackedPackets);
        // Auslastung dieses Pakets berechnen:
        double packetLoad = ((numberOfNormalCmds * avgNormalCmdSize) + (numberOfPrioCmds * avgPrioCmdSize)) / 1460;
        // Auch hier Durchschnitt updaten:
        avgLoadPerPacket = avgLoadPerPacket + ((packetLoad - avgLoadPerPacket) / trackedPackets);
    }

    /**
     * Aufrufen, um die Anzahl und den Zustand des Systems nach dem Sender aller Pakete nach einem Tick zu erfassen.
     *
     * @param numberOfPackets Anzahl der in diesem Tick versendeten Pakete
     * @param bufferRatio Ganzzahlige Prozentzahl, wie stark der Paketpuffer ausgelastet ist
     * @param outQueueSize Anzahl Befehle in der Befehlswarteschlange
     * @param prioOutQueueSize Anzahl Befehle in der Prio-Befehlswarteschlange (ohne Ultra-Prio)
     */
    void sentPackets(int numberOfPackets, int bufferRatio, int outQueueSize, int prioOutQueueSize) {
        trackedTicks++;
        // Durchschnitte updaten:
        avgNumberOfPacketsPerTick = avgNumberOfPacketsPerTick + ((numberOfPackets - avgNumberOfPacketsPerTick) / trackedTicks);
        avgOutBufferLoad = avgOutBufferLoad + ((bufferRatio - avgOutBufferLoad) / trackedTicks);
        /**
         * Die mit dieser Methode übermittelten Werte sind sehr wichtig für die Einschätzung des Gesamtzustandes des Netzwerksystems.
         * Daher wird ein Durchschnitt über einen emprisch bestimmten Zeitabschnitt berechnet, der den "Momentanzustand" des Netzwerks beschreibt.
         * Mit diesem können dann Entscheidungen getroffen werden.
         */
        recentNumberOfPacketsPerTick.push(numberOfPackets);
        recentOutBufferLoad.push(bufferRatio);
        recentOutQueueSize.push(outQueueSize);
        recentPrioOutQueueSize.push(prioOutQueueSize);
    }

    /**
     * Aufrufen, um die Anzahl von erneut gesendeten Paketen zu erfassen.
     * Pakete müssen neu gesendet werden, wenn nach einer bestimmten Zeit noch kein ACK eingegangen ist
     *
     * @param retransmitCounter
     */
    void resentPackets(int retransmitCounter) {
        avgNumberOfRetransmitsPerTick = avgNumberOfRetransmitsPerTick + ((retransmitCounter - avgNumberOfRetransmitsPerTick) / trackedTicks);
        recentRetransmitNumber.push(retransmitCounter);
    }

    /**
     * Gibt alle Werte aus.
     */
    public void printAll() {
        System.out.println(String.format("Tracked %d Packets, %d Ticks", trackedPackets, trackedTicks));
        System.out.println(String.format("Per Packet: Cmds: %.3f Prio: %.3f Load: %.3f", avgNumberOfCmdsPerPacket, avgNumberOfPrioCmdsPerPacket, avgLoadPerPacket));
        System.out.println(String.format("Per Tick: #Packets: %.3f OutBuffer Load: %.3f retrans: %.3f", avgNumberOfPacketsPerTick, avgOutBufferLoad, avgNumberOfRetransmitsPerTick));
        System.out.println("Recent: Packets:" + recentNumberOfPacketsPerTick.getNiceAvg() + " OutBuffer Load: " + recentOutBufferLoad.getNiceAvg() + " OutQueueSize: " + recentOutQueueSize.getNiceAvg() + " PrioOutQueueSize: " + recentPrioOutQueueSize.getNiceAvg() + " retrans: " + recentRetransmitNumber.getNiceAvg());
    }
}
