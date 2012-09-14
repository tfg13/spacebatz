package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;

/**
 *
 * @author michael
 */
public class STC_START_ENGINE extends FixedSizeSTCCommand {

    public STC_START_ENGINE() {
        super(0);
    }

    @Override
    public void execute(byte[] data) {
        // Engine starten:
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Client.setEngine(new Engine());
                Client.getEngine().start();
            }
        });
        t.setName("CLIENT_ENGINE");
        t.setDaemon(false);
        t.start();
        Client.getMsgInterpreter().initTcpReceiverThreadRun = false;
    }
}