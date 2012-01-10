package de._13ducks.spacebatz.client.network;

import com.sun.corba.se.impl.orbutil.ObjectWriter;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.shared.Level;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import sun.misc.JavaxSecurityAuthKerberosAccess;

/**
 * Die Empfangskomponente des Netzwerkmoduls
 * @author michael 
 */
public class MessageInterpreter {

    /**
     * Interpretiert eine TCP-Nachricht
     * @param message die bytes der NAchricht
     */
    public void interpretTcpMessage(byte cmdId, byte message[]) {

        System.out.println("RECEIVED: " + cmdId);
        if (cmdId == (byte) 20) {
            try {
                ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                Level myLevel = (Level) is.readObject();
                Client.currentLevel = myLevel;
                new Engine();
                System.out.println("Level received and loaded!");
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Interpretiert eine Udp-Nachricht
     * Die Nachricht sollte schnell interpretiert werden
     * @param message die bytes der Nachricht
     */
    public void interpretUdpMessage(byte message[]) {
    }
}
