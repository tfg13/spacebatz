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
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STCPacket implements Comparable<STCPacket> {
    
    /**
     * Die empfangenen Rohdaten.
     */
    private byte[] rawData;
    /**
     * Die fortlaufende (wrap-around) Indexnummer.
     */
    private short index;
    
    /**
     * Erzeugt ein neues Datenpaket aus den gegebenen Rohdaten
     *
     * @param rawData die Rohdaten
     */
    STCPacket(byte[] rawData) {
	if (rawData == null || rawData.length == 0) {
	    throw new IllegalArgumentException("rawData must neither be null nor empty");
	}
	this.rawData = rawData;
	index = Bits.getShort(rawData, 0);
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
    public int compareTo(STCPacket o) {
	// wrap-around:
	if (Math.abs(o.index - this.index) > Short.MAX_VALUE / 2) {
	    return o.index - this.index;
	} else {
	    return this.index - o.index;
	}
    }
    
    @Override
    public boolean equals(Object o) {
	if (o instanceof STCPacket) {
	    STCPacket i = (STCPacket) o;
	    return this.index == i.index;
	}
	return false;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 83 * hash + this.index;
	return hash;
    }
    
    /**
     * Verarbeitet das Datenpaket.
     */
    void compute() {
	int nextCmdIndex = 3;
	while (nextCmdIndex < rawData.length) {
	    int cmdID = rawData[nextCmdIndex++];
	    STCCommand cmd = ClientNetwork2.cmdMap[cmdID];
	    if (cmd == null) {
		System.out.println("WARNING: NET: ignoring unknown cmd! (id: " + cmdID);
		continue;
	    }
	    int dataSize = cmd.isVariableSize() ? cmd.getSize(rawData[nextCmdIndex]) : cmd.getSize((byte) 0);
	    byte[] data = new byte[dataSize];
	    // Daten kopieren
	    if (nextCmdIndex + dataSize < rawData.length) {
		for (int i = 0; i < dataSize; i++) {
		    data[i] = rawData[nextCmdIndex + i];
		}
	    } else {
		System.out.println("WARNING: NET: illegal cmd size, insufficient data bytes! (id: " + cmdID + " size: " + dataSize);
		// In diesem Fall ist wirklich gar nichts mehr zu retten abbrechen!
		break;
	    }
	    // Ausführen
	    try {
		cmd.execute(data);
	    } catch (Exception ex) {
		System.out.println("WARNING: NET: Execution of packet failed with Exception: " + ex);
		ex.printStackTrace();
	    }
	    nextCmdIndex += dataSize;
	}
    }

}
