package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author michael
 */
public class STC_TRANSFER_ENEMYTYPES extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // EnemyTypes empfangen (nur einmal)       
        try {
            ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(new java.io.ByteArrayInputStream(data)));
            EnemyTypes et = (EnemyTypes) is.readObject();
            GameClient.enemytypes = et;
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        throw new IllegalStateException("STC_TRANSFER_ENEMYTYPES wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    @Override
    public int getSize(byte sizeData) {
        throw new IllegalStateException("STC_TRANSFER_ENEMYTYPES wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    /**
     * Sendet enemytypes an einen Client
     *
     * @param client der Client, an den die enemytypes gesendet werden sollen
     */
    public static void sendEnemyTypes(Client client) {
        //Server.serverNetwork.sendTcpData(MessageIDs.NET_TCP_CMD_TRANSFER_ENEMYTYPES, Server.game.getSerializedEnemyTypes(), client);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_TRANSFER_ENEMYTYPES, Server.game.getSerializedEnemyTypes()), client);
    }
}
