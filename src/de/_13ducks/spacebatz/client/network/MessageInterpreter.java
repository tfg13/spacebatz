package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Die Empfangskomponente des Netzwerkmoduls
 *
 * @author michael
 */
public class MessageInterpreter {

    /**
     * Interpretiert eine TCP-Nachricht
     *
     * @param message die bytes der NAchricht
     */
    public void interpretTcpMessage(byte cmdId, byte message[]) {

        System.out.println("RECEIVED: " + cmdId + " and " + message.length + " bytes of data: ");

        switch (cmdId) {
            case 20:
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    Level myLevel = (Level) is.readObject();
                    Client.currentLevel = myLevel;
                    System.out.println("Level received and loaded!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case 21:
                // Player setzen
                Client.player = new Player(Bits.getInt(message, 0), Bits.getFloat(message, 4), Bits.getFloat(message, 8));
                Client.netIDMap.put(Client.player.netID, Client.player);
                break;
            case 22:
                // Engine starten:
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new Engine().start();
                    }
                });
                t.start();
                break;
            case 23:
                // ClientID setzen
                Client.setClientID(message[0]);
                break;
            case 24:
                // Neuer, fremder Player
                Player np = new Player(Bits.getInt(message, 0), 10, 10);
                Client.netIDMap.put(np.netID, np);
                System.out.println("ADDED " + np.netID);
                break;
            case 25:
                // Fremder Player mit Position
                Player np2 = new Player(Bits.getInt(message, 0), Bits.getFloat(message, 4), Bits.getFloat(message, 8));
                Client.netIDMap.put(np2.netID, np2);
                break;
            case 26:
                // Neuer Gegner
                Enemy enemy = new Enemy(Bits.getInt(message, 0), Bits.getFloat(message, 4), Bits.getFloat(message, 8));
                Client.netIDMap.put(enemy.netID, enemy);
            default:
                System.out.println("WARNING: Client received unknown TCP-Command");
        }

    }

    /**
     * Interpretiert eine Udp-Nachricht Die Nachricht sollte schnell interpretiert werden
     *
     * @param message die bytes der Nachricht
     */
    public void interpretUdpMessage(byte message[]) {
    }
}
