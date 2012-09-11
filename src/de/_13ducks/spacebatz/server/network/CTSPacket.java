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
import de._13ducks.spacebatz.shared.network.NetCommand;
import de._13ducks.spacebatz.shared.network.NetPacket;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein von einem Client empfangenes, reguläres Datenpaket.
 * NETMODE ist also 0b00
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
class CTSPacket extends NetPacket implements Comparable<CTSPacket> {

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
	super(rawData);
	if (sender == null) {
	    throw new IllegalArgumentException("sender must not be null");
	}
	this.sender = sender;
    }

    @Override
    protected short readIndex() {
	return Bits.getShort(rawData, 1);
    }
    
    @Override
    protected int getInitialCmdIndex() {
	return 4;
    }
    
    @Override
    protected NetCommand getCommand(int id) {
	return ServerNetwork2.cmdMap[id];
    }
    
    @Override
    protected void runCommand(NetCommand cmd, byte[] data) {
	((CTSCommand) cmd).execute(sender, data);
    }

    @Override
    public int compareTo(CTSPacket o) {
	// wrap-around:
	if (Math.abs(o.index - this.index) > Short.MAX_VALUE / 2) {
	    return o.index - this.index;
	} else {
	    return this.index - o.index;
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

    @Override
    protected void preRunCommand(NetCommand cmd, byte[] data) {
        ((CTSCommand) cmd).preExecute(sender, data);
    }
}
