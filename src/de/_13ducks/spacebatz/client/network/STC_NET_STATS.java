package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.util.Bits;

/**
 * Überträgt Netzwerk-Statusinformationen an den Client.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_NET_STATS extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Werte einfach schreiben:
        NetStats.avgNumberOfCmdsPerPacket = Bits.getFloat(data, 0);
        NetStats.avgNumberOfPrioCmdsPerPacket = Bits.getFloat(data, 4);
        NetStats.avgLoadPerPacket = Bits.getFloat(data, 8);
        NetStats.recentNumberOfPacketsPerTick = Bits.getFloat(data, 12);
        NetStats.recentOutBufferLoad = Bits.getFloat(data, 16);
        NetStats.recentOutQueueSize = Bits.getFloat(data, 20);
        NetStats.recentPrioOutQueueSize = Bits.getFloat(data, 24);
        NetStats.recentRetransmitNumber = Bits.getFloat(data, 28);
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 8 * 4;
    }
}
