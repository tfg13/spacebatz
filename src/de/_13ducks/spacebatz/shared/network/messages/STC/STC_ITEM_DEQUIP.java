package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_ITEM_DEQUIP extends FixedSizeSTCCommand {

    public STC_ITEM_DEQUIP() {
        super(13);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will ein bestimmtes Item ablegen
        int slottype = Bits.getInt(data, 0); // netID des  Items
        byte selslot2 = data[4];
        int clientID2 = Bits.getInt(data, 5); // clientID des Spielers
        float newspeed = Bits.getFloat(data, 9);
        if (clientID2 == GameClient.getClientID()) {
            Item item = GameClient.getEquippedItems().getEquipslots()[slottype][selslot2];
            GameClient.getEquippedItems().getEquipslots()[slottype][selslot2] = null;
            GameClient.addToInventory(item);

            GameClient.player.setHealthpointsmax(GameClient.player.getHealthpointsmax() - item.getBonusProperties().getMaxHitpoints());
            GameClient.player.setHitpointRegeneration(GameClient.player.getHitpointRegeneration() - item.getBonusProperties().getHitpointRegeneration());

            GameClient.players.get(clientID2).getPlayer().setPrediction_speed(newspeed);
            GameClient.players.get(clientID2).getPlayer().setMovement_speed(newspeed);
        }
    }

    /**
     * Item wird von Client abgelegt (zurück ins Inventar)
     */
    public static void sendItemDequip(int slottype, byte selslot, int clientID, float newspeed) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[13];
            Bits.putInt(b, 0, slottype);
            b[4] = selslot;
            Bits.putInt(b, 5, clientID);
            Bits.putFloat(b, 9, newspeed);
            //Server.serverNetwork.sendTcpData(MessageIDs.NET_TCP_CMD_DEQUIP_ITEM, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_DEQUIP_ITEM, b), c);
        }
    }
}
