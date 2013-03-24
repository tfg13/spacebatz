package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.data.LogicPlayer;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Wird verschickt, wenn ein neuer Client dem Spiel beitritt.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_NEW_CLIENT extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int clientID = Bits.getInt(data, 1);
        int playerNetID = Bits.getInt(data, 5);
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < data[0]; i++) {
            char c = Bits.getChar(data, 9 + (i * 2));
            name.append(c);
        }
        LogicPlayer newPlayer = new LogicPlayer(clientID, name.toString());
        newPlayer.setPlayerNetID(playerNetID);
        GameClient.players.put(clientID, newPlayer);
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return 9 + sizeData * 2;
    }

    /**
     * Sendet eine Nachricht an alle Spieler, die über den neuen Client informiert.
     *
     * @param newClient der neue Client
     */
    public static void broadcast(Client newClient) {
        for (Client c : Server.game.clients.values()) {
            send(newClient, c);
        }
    }

    /**
     * Sendet eine Nachricht an alle Spieler, die über den neuen Client informiert.
     *
     * @param newClient der neue Client
     */
    public static void send(Client newClient, Client target) {
        String nickName = newClient.getNickName();
        byte[] data = new byte[9 + (nickName.length() * 2)];
        data[0] = (byte) nickName.length();
        Bits.putInt(data, 1, newClient.clientID);
        Bits.putInt(data, 5, newClient.getPlayer().netID);
        for (int i = 0; i < nickName.length(); i++) {
            Bits.putChar(data, 9 + (i * 2), nickName.charAt(i));
        }
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_NEW_CLIENT, data), target);
    }
}
