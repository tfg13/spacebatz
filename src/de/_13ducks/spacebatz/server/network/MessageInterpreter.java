package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;


/**
 *
 * @author michael
 */
public class MessageInterpreter {

    /**
     * Interpretiert eine TCP Nachricht von einem Client.
     * @param message die Nachricht als byte-array
     */
    public void interpretMessage(byte message[], Client sender) {
        System.out.print("TCP received: ");
        for(int i=0; i<message.length; i++){
            System.out.print((int)message[i]);
        }
        System.out.print("\n");
        
        //TODO: Nachricht interpretieren...
    }
}
