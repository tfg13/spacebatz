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

/**
 * Ein Netzwerkbefehl, den der Server ausführen kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class CTSCommand extends NetCommand {

    /**
     * Führt diesen Serverbefehl aus.
     *
     * @param client der Client, von dem dieser Befehl kommt
     * @param data die Daten, die der Client mitgeschickt hat
     */
    public abstract void execute(Client client, byte[] data);
}
