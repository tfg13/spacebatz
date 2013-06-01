package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Setzt die Größe eines neuen Levels.
 * Der Inhalt des Levels wird dynamisch nachgeladen
 *
 * @author michael
 */
public class STC_CHANGE_LEVEL extends STCCommand {

    @Override
    public void execute(byte[] data) {
        GameClient.currentLevel = new Level(Bits.getInt(data, 0), Bits.getInt(data, 4));
        if (GameClient.getEngine() != null) {
            GameClient.getEngine().getGraphics().levelChanged(Bits.getInt(data, 0), Bits.getInt(data, 4));
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 8;
    }

    /**
     * Sendet das Level an einen Client
     *
     * @param client der Client, an den gesendet wird
     */
    public static void sendLevel(Client client) {
        byte[] data = new byte[8];
        Bits.putInt(data, 0, Server.game.getLevel().getSizeX());
        Bits.putInt(data, 4, Server.game.getLevel().getSizeY());
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CHANGE_LEVEL, data), client);
    }
}
