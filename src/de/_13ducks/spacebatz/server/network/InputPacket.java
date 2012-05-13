package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.util.Bits;

/**
 * Ein von einem Client empfangenes, reguläres Datenpaket.
 * NETMODE ist also 0b00
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
class InputPacket implements Comparable<InputPacket> {
    
    /**
     * Die empfangenen Rohdaten.
     */
    private byte[] rawData;
    /**
     * Die fortlaufende (wrap-around) Indexnummer.
     */
    private short index;

    InputPacket(byte[] rawData) {
	this.rawData = rawData;
	index = Bits.getShort(rawData, 1);
    }
    
    /**
     * Liefert den Index dieses Pakets.
     * @return den Index
     */
    public short getIndex() {
	return index;
    }

    @Override
    public int compareTo(InputPacket o) {
	// wrap-around:
	if (Math.abs(this.index - o.index) > Short.MAX_VALUE / 2) {
	    return this.index - o.index;
	} else {
	    return o.index - this.index;
	}
	
    }
    
    @Override
    public boolean equals(Object o) {
	if (o instanceof InputPacket) {
	    InputPacket i = (InputPacket) o;
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
	throw new UnsupportedOperationException("not yet implemented");
    }
}
