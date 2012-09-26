package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_CHANGE_MATERIAL_AMOUNT extends FixedSizeSTCCommand {

    public STC_CHANGE_MATERIAL_AMOUNT() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Spieler bekommt Material  

        int clientID = Bits.getInt(data, 0); // netID des Spielers, für den das Command ist
        int material = Bits.getInt(data, 4);
        int amount = Bits.getInt(data, 8);
        // Item ins Client-Inventar verschieben, wenn eigene clientID
        if (clientID == GameClient.getClientID()) {
            GameClient.setMaterial(material, amount);
        }
    }

    /**
     * Menge eines Materials wird für einen Spieler geändert
     *
     * @param clientID ?
     * @param material Nummer des Materialstyps
     * @param amount Wieviel hinzugefügt werden soll
     */
    public static void sendMaterialAmountChange(int clientID, int material, int amount) {
        for (Client c : Server.game.clients.values()) {

            byte[] b = new byte[12];
            Bits.putInt(b, 0, clientID);
            Bits.putInt(b, 4, material);
            Bits.putInt(b, 8, amount);

            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CHANGE_MATERIAL_AMOUNT, b), c);
        }
    }
}
