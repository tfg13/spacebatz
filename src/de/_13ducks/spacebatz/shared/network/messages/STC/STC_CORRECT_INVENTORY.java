package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Sendet die korrekte Belegung des Inventars and einen Client, wenn er einne illegalen Inventarbefehl gesendet hat.
 *
 * @author mekhar
 */
public class STC_CORRECT_INVENTORY extends STCCommand {

    @Override
    public void execute(byte[] data) {
        GameClient.getEngine().getGraphics().getInventory().forceMapping(data);
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return CompileTimeParameters.INVENTORY_SIZE * 4; // Ein Integer(4 bytes) pro slot
    }

    public static void sendCorrectInventory(byte[] mapping, Player player) {
        Client client = player.getClient();
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_CORRECT_INVENTORY, mapping), client);
    }
}
