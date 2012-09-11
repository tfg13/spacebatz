package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.util.Bits;

/**
 * ACK-Befehl
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_ACK extends STCCommand {

    @Override
    public void preExecute(byte[] data) {
	Client.getNetwork2().outBuffer.ackPacket(Bits.getShort(data, 0));
    }

    @Override
    public boolean isVariableSize() {
	return false;
    }

    @Override
    public int getSize(byte sizeData) {
	return 2;
    }

    @Override
    public void execute(byte[] data) {
        // nix
    }

}
