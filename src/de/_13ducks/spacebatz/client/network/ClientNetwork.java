package de._13ducks.spacebatz.client.network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

//        boolean connected = cn.tryConnect("127.0.0.1");
//        System.err.println(connected);
//        byte d[] = {3, 42, 42, 42};
//
//
//        cn.sendData(d);
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

    private void receiveData() {
        Thread receiveDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
            }
        });
    }
}
