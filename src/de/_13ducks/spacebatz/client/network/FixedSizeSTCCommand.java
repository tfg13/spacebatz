package de._13ducks.spacebatz.client.network;

/**
 * Wrapper für ein STC-Kommando, das immer die gleiche Größe hat.
 *
 * @author michael
 */
public abstract class FixedSizeSTCCommand extends STCCommand {

    /**
     * Die Größe.
     */
    private int size;

    public FixedSizeSTCCommand(int size) {
        this.size = size;
    }

    @Override
    public abstract void execute(byte[] data);

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return size;
    }
}
