package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;

/**
 * Sendet Daten übers Netzwerk.
 * Kümmert sich darum, gescheite Pakete zu backen.
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MessageSender {

    /**
     * Sendet das Level an einen Client
     */
    public void sendLevel(Client client) {
        System.out.println("Sending Level to Client.");
        Server.serverNetwork.sendTcpData((byte) 20, Server.game.getSerializedLevel(), client);
        System.out.println("Level sent.");
    }

    /**
     * Sendet eine große Nachricht, die in mehrere Teilnachrichten aufgeteilt werden muss
     * 
     * @param byteBlock der byte array der gesendet werden soll 
     */
    public void sendLargeByteBlock(byte byteBlock[], Client client) {

//        System.out.println("Sending " + byteBlock.length + "bytes AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
//
//        int blocks = byteBlock.length / 100;
//        int rest = byteBlock.length % 100;
//
//        // Größe des Packets senden:
//        byte sizePacket[] = {(byte) (byteBlock.length - 1)};
//        Server.serverNetwork.sendTcpData(sizePacket, client);
//
//        byte msg[] = new byte[100];
//        for (int b = 0; b < blocks; b++) {
//            for (int i = 0; i < 100; i++) {
//                msg[i] = byteBlock[b + i];
//                Server.serverNetwork.sendTcpData(msg, client);
//            }
//        }
//        msg = new byte[rest];
//        for (int i = 0; i < rest; i++) {
//            msg[i] = byteBlock[blocks + i];
//        }

    }
}
