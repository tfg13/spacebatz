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
     */
    public void sendEquipItem(Item item, byte selectedslot) {
        byte[] b = new byte[9];
        Bits.putInt(b, 0, item.getNetID());
        b[4] = selectedslot;
        Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_ITEM_EQUIP, b);
    }

    /**
     * Client will Item ablegen, muss dafür aber erst Server fragen
     */
    public void sendDequipItem(int slottype, byte selslot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, slottype);
        b[4] = selslot;
        Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, b);
    }
    
    /**
     * Client will andere Waffe auswählen
     */
    public void sendSwitchWeapon(byte slot) {
        byte[] b = new byte[1];
        b[0] = slot;
        Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH, b);
    }

    public void sendDisconnect() {
        Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[1]);
    }

    void sendRequestResync() {
	Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RESYNC, new byte[1]);
    }

}
