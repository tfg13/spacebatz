/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Johannes
 */
public class STC_CHAR_ATTACK extends FixedSizeSTCCommand{

    public STC_CHAR_ATTACK () {
        super (8);
    }
    
    @Override
    public void execute(byte[] data) {
        int charid = Bits.getInt(data, 0);
        
        // Overheat der Waffe erhöhen:
        if (charid == GameClient.player.netID) {
            int weaponnumber = GameClient.player.getSelectedattack();
            Item item = GameClient.getEquippedItems().getEquipslots()[1][weaponnumber];
            if (item != null) {
                GameClient.getEquippedItems().getEquipslots()[1][weaponnumber].increaseOverheat(1);
                GameClient.player.attackCooldownTick = GameClient.frozenGametick + (int) Math.ceil(1 / item.getWeaponAbility().getAttackspeed());
            }
        }
    }
    
    public static void sendCharAttack (int charid, float direction) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[8];
            Bits.putInt(b, 0, charid);
            Bits.putFloat(b, 4, direction);

            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_CHAR_ATTACK, b), c);
        }
    }
    
}
