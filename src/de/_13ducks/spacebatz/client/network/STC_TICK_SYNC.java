package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.util.Bits;

/**
 * Server-Antwort auf ein Client-Sync Paket.
 * Anders als die Ping-Messungen werden diese Pakete
 * zwar beim senden priorisiert, müssen beim Empfangen aber die normalen Queues durchlaufen.
 * Damit kann bei bekannter Laufzeit, Ping und Lerp festgestellt werden, wie stark die Tickzählungen von Server und Client abweichen.
 * Diese Abweichung kann dann korrigiert werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_TICK_SYNC extends STCCommand {

    @Override
    public void execute(byte[] data) {
        GameClient.getNetwork2().tickSyncReceived(Bits.getInt(data, 0));
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 4;
    }
}
