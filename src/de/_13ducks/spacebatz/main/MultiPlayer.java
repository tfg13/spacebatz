package de._13ducks.spacebatz.main;

import de._13ducks.spacebatz.client.Client;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Starten, um zu sich zu einem laufenden Server woanderst zu verbinden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MultiPlayer {

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.print("IP>");
            StringBuilder b = new StringBuilder();
            char c;
            while ((c = (char) System.in.read()) != '\n') {
                b.append(c);
            }
            String result = b.toString();
            if (result.equals("exit") || result.equals("quit")) {
                break;
            } else {
                // Versuche als IP zu interpretieren:
                try {
                    InetAddress adr = InetAddress.getByName(result);
                    System.out.println("IP seems valid, starting client...");
                    // Es scheint eine gültige IP zu sein, da hin verbinden!
                    Client.main(new String[]{"ip=" + result});
                } catch (UnknownHostException ex) {
                }
            }
            System.out.println("Enter valid IP-Adress or type \"quit\"!");
        }
    }
}
