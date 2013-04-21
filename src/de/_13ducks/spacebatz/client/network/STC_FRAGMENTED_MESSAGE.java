/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.shared.network.MessageFragmenter;
import de._13ducks.spacebatz.shared.network.MessageRegister;

/**
 *
 * @author michael
 */
public class STC_FRAGMENTED_MESSAGE extends STCCommand {

    /**
     * Fügt fragmentierte Nachrichten für den Client wieder zusammen.
     */
    private static MessageFragmenter messageConnector = new MessageFragmenter();

    @Override
    public void execute(byte[] data) {
        messageConnector.fragmentedMessageData(data);
        if (messageConnector.isComplete()) {
            // Packet ausführen:
            STCCommand cmd = MessageRegister.getSTC(messageConnector.getMessageID());
            cmd.execute(messageConnector.getCompletedMessage());
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        if (sizeData < 0) {
            return 128;
        }
        return sizeData;
    }
}
