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
package de._13ducks.spacebatz.shared.network;

import java.net.DatagramPacket;

/**
 * Stellt Methoden bereit, die Client und Server gleichermaßen brauchen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Utilities {
    
    /**
     * Extrahiert Daten aus einem gegebenen UDP-Paket.
     * Beachtet offset und Länge, das zurückgegebene Array hat genau packet.length() Einträge.
     * @param packet ein UDP-Packet
     * @return die Daten in seriös nutzbarer Form
     */
    public static byte[] extractData(DatagramPacket packet) {
	byte[] ret = new byte[packet.getLength()];
	System.arraycopy(packet.getData(), packet.getOffset(), ret, 0, packet.getLength());
	return ret;
    }
    
}
