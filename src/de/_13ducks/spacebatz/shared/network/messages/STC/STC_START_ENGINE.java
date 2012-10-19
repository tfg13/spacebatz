package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.Engine;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class STC_START_ENGINE extends FixedSizeSTCCommand {

    public STC_START_ENGINE() {
        super(0);
    }

    @Override
    public void execute(byte[] data) {
        // Engine starten:
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GameClient.setEngine(new Engine());
                GameClient.getEngine().start();
            }
        });
        t.setName("CLIENT_ENGINE");
        t.setDaemon(false);
        t.start();
        GameClient.getInitialMainloop().stop();
    }

    /**
     * Lässt den Client das Spiel starten.
     *
     * @param client der Ziel-Client
     */
    public static void sendStartGame(Client client) {
        //Server.serverNetwork.sendTcpData((byte) MessageIDs.NET_STC_START_ENGINE, new byte[1], client);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_START_ENGINE, new byte[0]), client);
    }
}
