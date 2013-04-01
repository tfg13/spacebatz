/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.main;

import de._13ducks.spacebatz.client.GameClient;
import java.io.IOException;

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
            	if (c != '\r') { // geht sonst nicht in Eclipse
            		b.append(c);
            	}
            }
            String result = b.toString();
            if (result.equals("exit") || result.equals("quit")) {
                break;
            } else {
                // Versuche als IP zu interpretieren:
                System.out.println("IP seems valid, starting client...");
                // Es scheint eine gültige IP zu sein, da hin verbinden!
                GameClient.startClient(result);
            }
            System.out.println("Enter valid IP-Adress or type \"quit\"!");
        }
    }
}
