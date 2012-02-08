package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 * Die Sendekpomponente des Netzwerkmoduls
 * @author michael
 */
public class ClientMessageSender {
    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     * @param client der Ziel-Client
     */
    public void sendEquipItem(Item item, int equipslot) {
        byte[] b = new byte[8];
        Bits.putInt(b, 0, item.netID);
        Bits.putInt(b, 4, equipslot);
        Client.getNetwork().sendTcpData((byte) Settings.NET_TCP_CMD_REQUEST_ITEM_EQUIP, b); 
    }
}
