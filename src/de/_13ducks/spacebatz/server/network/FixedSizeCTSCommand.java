package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;

/**
 * Wrapper für ein STC-Kommando, das immer die gleiche Größe hat.
 *
 * @author michael
 */
public abstract class FixedSizeCTSCommand extends CTSCommand {

    /**
     * Die Größe.
     */
    private int size;

    public FixedSizeCTSCommand(int size) {
        this.size = size;
    }

    @Override
    public abstract void execute(Client client, byte[] data);

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return size;
    }
}
