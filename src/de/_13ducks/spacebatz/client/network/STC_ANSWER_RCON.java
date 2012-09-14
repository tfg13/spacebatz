package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_ANSWER_RCON extends FixedSizeSTCCommand {

    public STC_ANSWER_RCON() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        if (data[0] == 1) {
            // Server erlaubt rcon
            Client.terminal.rcon(Bits.getInt(data, 1));
        }
    }
}
