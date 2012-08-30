package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.util.Bits;

/**
 * ACK-Befehl
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_ACK extends CTSCommand {

    @Override
    public void execute(Client client, byte[] data) {
	int ackID = Bits.getShort(data, 0);
	client.getNetworkConnection().getOutBuffer().ackPacket(ackID);
    }

    @Override
    public boolean isVariableSize() {
	return false;
    }

    @Override
    public int getSize(byte sizeData) {
	return 2;
    }

}
