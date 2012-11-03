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

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;

/**
 * Server schickt neue Bewegungsdaten für bekannte Entities.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_ENTITY_UPDATE extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int numberOfUpdates = (data.length - 1) / 32;
        for (int i = 0; i < numberOfUpdates; i++) {
            int netID = Bits.getInt(data, i * 32 + 1);
            Char c = GameClient.netIDMap.get(netID);
            if (c == null) {
                System.out.println("WARNING: CNET: MOVESYNC: Skipping unknown Char " + netID);
                continue;
            }
            Movement m = new Movement(Bits.getFloat(data, i * 32 + 13), Bits.getFloat(data, i * 32 + 17), Bits.getFloat(data, i * 32 + 21), Bits.getFloat(data, i * 32 + 25), Bits.getInt(data, i * 32 + 5), Bits.getFloat(data, i * 32 + 9), Bits.getInt(data, i * 32 + 29));
            c.applyMove(m);
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        int number = sizeData;
        if (number < 0) {
            number += 256;
        }
        return number * 32 + 1;
    }
}
