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

import de._13ducks.spacebatz.shared.network.NetCommand;
import de._13ducks.spacebatz.shared.network.NetPacket;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STCPacket extends NetPacket implements Comparable<STCPacket> {
    
    /**
     * Erzeugt ein neues Datenpaket aus den gegebenen Rohdaten
     *
     * @param rawData die Rohdaten
     */
    STCPacket(byte[] rawData) {
	super(rawData);
    }
    
    @Override
    protected short readIndex() {
	return Bits.getShort(rawData, 0);
    }

    @Override
    protected int getInitialCmdIndex() {
	return 3;
    }
    
    @Override
    protected NetCommand getCommand(int id) {
	return ClientNetwork2.cmdMap[id];
    }
    
    @Override
    protected void runCommand(NetCommand cmd, byte[] data) {
	((STCCommand) cmd).execute(data);
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
}
