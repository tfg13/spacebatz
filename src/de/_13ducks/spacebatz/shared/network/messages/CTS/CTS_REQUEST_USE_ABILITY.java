/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Fähigkeitsbenutzungsanfordernung.
 *
 * @author michael
 */
public class CTS_REQUEST_USE_ABILITY extends FixedSizeCTSCommand {

    public CTS_REQUEST_USE_ABILITY() {
        super(9);
    }

    @Override
    public void execute(Client client, byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        byte ability = decoder.readByte();
        float dir = decoder.readFloat();
        float distance = decoder.readFloat();
        double x = client.getPlayer().getX() + distance * Math.cos(dir);
        double y = client.getPlayer().getY() + distance * Math.sin(dir);
        client.getPlayer().useAbility(ability, x, y);
    }

    public static void sendAbilityUseRequest(byte ability, float angle, float distance) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeByte(ability);
        encoder.writeFloat((float) angle);
        encoder.writeFloat(distance);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_USE_ABILITY, encoder.getBytes()));

    }
}
