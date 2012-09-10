package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;

/**
 * Debug-Signal. Gibt einfach die empfangenen Daten aus.
 *
 * @author michael
 */
public class CTS_DEBUG extends CTSCommand {

    private int size;

    @Override
    public void execute(Client client, byte[] data) {
        System.out.println("Hi, Debug here.  Size:" + size);
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
        size = (int) sizeData * 100;
        System.out.println(size);
        return size;
    }
}
