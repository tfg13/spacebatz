/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author Johannes
 */
public class CTS_TOGGLE_BUILDMODE extends FixedSizeCTSCommand {

    public CTS_TOGGLE_BUILDMODE() {
        super(1);
    }

    @Override
    public void execute(Client client, byte[] data) {
        client.getPlayer().setBuildmode(data[0] == 0 ? false : true);
    }

    public static void sendToggleBuildmode(boolean mode) {
        byte[] b = new byte[1];
        b[0] = mode ? (byte) 1 : (byte) 0;
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_TOGGLE_BUILDMODE, b));
    }
}
