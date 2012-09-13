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

    @Override
    public void execute(de._13ducks.spacebatz.server.data.Client client, byte[] data) {
        MessageFragmenter messageConnector = client.getNetworkConnection().getFragmenter();
        messageConnector.fragmentedMessageData(data);
        if (messageConnector.isComplete()) {
            // Packet ausführen:
            int id = messageConnector.getMessageID();
            if (id < 0) {
                id += 256;
            }
            CTSCommand cmd = Server.serverNetwork2.getCmdForId(id);
            cmd.execute(client, messageConnector.getCompletedMessage());
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
