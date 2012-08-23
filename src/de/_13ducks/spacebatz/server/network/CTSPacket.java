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
package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein von einem Client empfangenes, reguläres Datenpaket.
 * NETMODE ist also 0b00
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
class CTSPacket implements Comparable<CTSPacket> {

    /**
     * Die empfangenen Rohdaten.
     */
    private byte[] rawData;
    /**
     * Die fortlaufende (wrap-around) Indexnummer.
     */
    private short index;
    /**
     * Der Absender dieses Pakets.
     */
    private Client sender;

    /**
     * Erzeugt ein neues Datenpaket aus den gegebenen Rohdaten
     *
     * @param rawData die Rohdaten
     */
    CTSPacket(byte[] rawData, Client sender) {
	this.rawData = rawData;
	index = Bits.getShort(rawData, 1);
    }

    /**
     * Liefert den Index dieses Pakets.
     *
     * @return den Index
     */
    public short getIndex() {
	return index;
    }

    @Override
    public int compareTo(CTSPacket o) {
	// wrap-around:
	if (Math.abs(this.index - o.index) > Short.MAX_VALUE / 2) {
	    return this.index - o.index;
	} else {
	    return o.index - this.index;
	}
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof CTSPacket) {
	    CTSPacket i = (CTSPacket) o;
	    return this.index == i.index;
	}
	return false;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 97 * hash + this.index;
	return hash;
    }

    /**
     * Verarbeitet das Datenpaket.
     */
    void compute() {
	int nextCmdIndex = 5;
	while (nextCmdIndex < rawData.length) {
	    int cmdID = rawData[nextCmdIndex];
	    CTSCommand cmd = ServerNetwork2.cmdMap[cmdID];
	    if (cmd == null) {
		System.out.println("WARNING: NET: ignoring unknown cmd! (id: " + cmdID);
		continue;
	    }
	    int dataSize = cmd.isVariableSize() ? cmd.getSize(rawData[nextCmdIndex + 1]) : cmd.getSize((byte) 0);
	    byte[] data = new byte[dataSize];
	    // Daten kopieren
	    if (nextCmdIndex + dataSize < rawData.length) {
		for (int i = 1; i <= dataSize; i++) {
		    data[i] = rawData[nextCmdIndex + i];
		}
	    } else {
		System.out.println("WARNING: NET: illegal cmd size, insufficient data bytes! (id: " + cmdID + " size: " + dataSize);
		// In diesem Fall ist wirklich gar nichts mehr zu retten abbrechen!
		break;
	    }
	    // Ausführen
	    try {
		cmd.execute(sender, data);
	    } catch (Exception ex) {
		System.out.println("WARNING: NET: Execution of packet failed with Exception: " + ex);
		ex.printStackTrace();
	    }
	}
    }
}
