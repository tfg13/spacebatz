package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Die Netzwerkkomponente des Clients
 * @author michael
 */
public class ClientNetwork {

    /**
     * Unser Socket
     */
    private Socket mySocket;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientNetwork cn = new ClientNetwork();
    }

    /**
     * Konstruktor
     */
    public ClientNetwork() {
        mySocket = new Socket();
    }

    /**
     * versucht, eine Verbindung zur angegebenen Addresse aufzubauen
     * @return true bei erfolg, false wenn der Verbindungsaufbau scheitert
     */
    public boolean tryConnect(String ipaddress) {
        InetSocketAddress serverAddress = new InetSocketAddress(ipaddress, 10000);
        boolean result = true;
        try {
            mySocket.connect(serverAddress, 10000);
        } catch (IOException ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Sendet Daten an den Server
     * @param message der Byte-Array der gessendet werden soll
     */
    public void sendData(byte message[]) {
        try {
            mySocket.getOutputStream().write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Empfängt in einer Endlosschleife Daten
     */
    private void receiveData() {
        Thread receiveDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int bytesToRead = 0; // Die bytes die noch gelesen werden müssen
                    byte cmdId = 0;     // Die cmdId
                    byte buffer[] = new byte[0];   // der puffer

                    while (true) {
                        if (bytesToRead == 0) {
                            if (mySocket.getInputStream().available() > 0) {
                                bytesToRead = mySocket.getInputStream().read();
                                buffer = new byte[bytesToRead];
                                cmdId = 0;
                            }
                        } else {
                            if (cmdId == 0) {
                                if (mySocket.getInputStream().available() >= bytesToRead) {
                                    cmdId = (byte) mySocket.getInputStream().read();
                                }
                            } else {
                                if (mySocket.getInputStream().available() >= bytesToRead) {
                                    for (int i = 0; i < bytesToRead; i++) {
                                        buffer[i] = (byte) mySocket.getInputStream().read();
                                    }
                                    Client.getMsgInterpreter().interpretTcpMessage(cmdId, buffer);
                                    bytesToRead = 0;
                                }
                            }

                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        });
    }
}
