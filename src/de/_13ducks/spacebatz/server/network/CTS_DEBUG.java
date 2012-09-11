package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;

/**
 * Debug-Signal. Gibt einfach die empfangenen Daten aus.
 *
 * @author michael
 */
public class CTS_DEBUG extends CTSCommand {

    @Override
    public void execute(Client client, byte[] data) {
        System.out.println("Hi, Debug here.  Size:" + data.length);
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i]);
        }
        System.out.println();
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return (int) sizeData * 100;
    }
}
