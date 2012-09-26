package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.shared.Level;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author michael
 */
public class STC_TRANSFER_LEVEL extends STCCommand {

    @Override
    public void execute(byte[] data) {
        try {
            ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(new java.io.ByteArrayInputStream(data)));
            Level myLevel = (Level) is.readObject();
            GameClient.currentLevel = myLevel;
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
