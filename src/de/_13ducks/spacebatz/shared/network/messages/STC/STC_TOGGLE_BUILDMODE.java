/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author Johannes
 */
public class STC_TOGGLE_BUILDMODE extends FixedSizeSTCCommand {

    public STC_TOGGLE_BUILDMODE() {
        super(2);
    }

    @Override
    public void execute(byte[] data) {
        boolean mode = data[0] == 1 ? true : false;
        GameClient.players.get((int) data[1]).getPlayer().setBuildmode(mode);
        GameClient.players.get((int) data[1]).getPlayer().setTurretRenderObject(new RenderObject(new Animation(mode ? 8 : 4, 4, 4, 1, 1)));
    }

    public static void sendToggleBuildmode(boolean mode, byte clientid) {
        byte[] b = new byte[2];
        b[0] = mode ? (byte) 1 : (byte) 0;
        b[1] = clientid;
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_TOGGLE_BUILDMODE, b), c);
        }
    }
}
