package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;

/**
 *
 * @author michael
 */
public class STC_SET_CLIENT extends FixedSizeSTCCommand {

    public STC_SET_CLIENT() {
        super(1);
    }

    @Override
    public void execute(byte[] data) {
        // ClientID setzen
        Client.setClientID(data[0]);
    }
}
