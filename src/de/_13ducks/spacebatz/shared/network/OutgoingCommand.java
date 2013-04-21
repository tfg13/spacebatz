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

/**
 * Repräsentiert einen ausgehenden Befehl
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OutgoingCommand {

    /**
     * Die Befehlsnummer dieses Befehls
     */
    public final int cmdID;
    /**
     * Die mitgesendeten Daten. Kann leer sein
     */
    public final byte[] data;

    /**
     * Erzeugt einen neuen ausgehenden Befehl
     *
     * @param cmdID die Befehlsnummer, muss eine gültige custom-cmdID sein (siehe network_docs) derzeit noch egal
     * @param data die Daten. Maximalgröße: 128. Kann leer, darf aber nicht null sein.
     */
    public OutgoingCommand(int cmdID, byte[] data) {
        /*if (cmdID < 1 || cmdID > 0x80) {
         throw new IllegalArgumentException("Illegal cmdID");
         }*/
//	if (data == null || data.length > 128) {
//	    throw new IllegalArgumentException("Illegal data segment");
//	}
        this.cmdID = cmdID;
        this.data = data;
    }

    /**
     * Prüft die Größe des fertigen Datenpakets und wirft Fehler, wenn die Größe nicht eingehalten wurde.
     * Wird vom Netzwerksystem direkt vor dem versenden Aufgerufen, um sicherzustellen, dass die Größe stimmt.
     */
    public final void checkSTCSize() {
        internalCheckSize(MessageRegister.getSTC(cmdID));
    }

    /**
     * Prüft die Größe des fertigen Datenpakets und wirft Fehler, wenn die Größe nicht eingehalten wurde.
     * Wird vom Netzwerksystem direkt vor dem versenden Aufgerufen, um sicherzustellen, dass die Größe stimmt.
     */
    public final void checkCTSSize() {
        internalCheckSize(MessageRegister.getCTS(cmdID));
    }

    private void internalCheckSize(NetCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ERROR: NET: Cannot send command, unknown id!");
        }
        int specifiedSize;
        if (cmd.isVariableSize()) {
            specifiedSize = cmd.getSize(data[0]);
        } else {
            specifiedSize = cmd.getSize((byte) 0);
        }
        // Check
        if (specifiedSize != data.length) {
            throw new IllegalArgumentException("ERROR: NET: Command data length mismatch, should be " + specifiedSize + " but is " + data.length);
        }
        // OK
    }
}
