/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.network.MessageFragmenter;

/**
 *
 * @author michael
 */
public class CTS_FRAGMENTED_MESSAGE extends CTSCommand {

    /**
     * Fügt fragmentierte Nachrichten für den Client wieder zusammen.
     */
    private static MessageFragmenter messageConnector = new MessageFragmenter();

    @Override
    public void execute(de._13ducks.spacebatz.server.data.Client client, byte[] data) {
        messageConnector.fragmentedMessageData(data);
        if (messageConnector.isComplete()) {
            // Packet ausführen:
            CTSCommand cmd = Server.serverNetwork2.getCmdForId(messageConnector.getMessageID());
            cmd.execute(client, messageConnector.getCompletedMessage());
        }

    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        if(sizeData < 0){
            return 128;
        }
        return sizeData;
    }
}
