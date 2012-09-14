package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.shared.Level;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author michael
 */
public class STC_TRANSFER_LEVEL extends STCCommand {

    @Override
    public void execute(byte[] data) {
        try {
            ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(data));
            Level myLevel = (Level) is.readObject();
            Client.currentLevel = myLevel;
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        throw new IllegalStateException("STC_TRANSFER_LEVEL wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    @Override
    public int getSize(byte sizeData) {
        throw new IllegalStateException("STC_TRANSFER_LEVEL wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }
}
