package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
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
}
