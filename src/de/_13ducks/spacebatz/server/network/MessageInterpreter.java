package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.SpacebatzServer;
import de._13ducks.spacebatz.server.data.Client;


/**
 *
 * @author michael
 */
public class MessageInterpreter {

    /**
     * interpretiert eine Nachricht vion einem Client
     * @param message die Nachricht als byte-array
     */
    public void interpretMessage(byte message[], Client sender){
        System.out.print("Message received: ");
        for(int i=0; i<message.length; i++){
            System.out.print((int)message[i]);
        }
        System.out.print("\n");
        
        
        for(int i=0; i< SpacebatzServer.game.chars.size(); i++){
            if((byte)SpacebatzServer.game.chars.get(i).id == message[0])
            {
                SpacebatzServer.game.chars.get(i).posX = (int)message[1];
                SpacebatzServer.game.chars.get(i).posY = (int)message[2];
            }
        }
        
        
        
    }
}
