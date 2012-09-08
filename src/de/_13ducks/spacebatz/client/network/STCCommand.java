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

/**
 * Ein Netzwerkbefehl, den der Client ausführen kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class STCCommand extends NetCommand {
    
    /**
     * Führt diesen Clientbefehl aus.
     *
     * @param data die Daten, die der Server mitgeschickt hat
     */
    public abstract void execute(byte[] data);

}
