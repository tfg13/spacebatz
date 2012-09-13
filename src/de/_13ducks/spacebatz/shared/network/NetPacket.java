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
 * Oberklasse für CTS und STC Pakete. Bietet, was ohnehin für beide gleich ist.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class NetPacket {

    /**
     * Die empfangenen Rohdaten.
     */
    protected byte[] rawData;
    /**
     * Die fortlaufende (wrap-around) Indexnummer.
     */
    protected short index;

    /**
     * Erzeugt ein neues Datenpaket aus den gegebenen Rohdaten
     *
     * @param rawData die Rohdaten
     */
    protected NetPacket(byte[] rawData) {
        if (rawData == null || rawData.length == 0) {
            throw new IllegalArgumentException("rawData must neither be null nor empty");
        }
        this.rawData = rawData;
        index = readIndex();
    }

    /**
     * Liest den Index aus dem Datenpaket aus. Extra Methode, da für CTS und STC
     * unterschiedlich.
     *
     * @return
     */
    protected abstract short readIndex();

    /**
     * Liefert den Index der Befehlsnummer des ersten Befehls.
     *
     * @return den Index der ersten Befehlsnummer
     */
    protected abstract int getInitialCmdIndex();

    /**
     * Sucht und liefert den Befehl mit der gegebenen Nummer.
     *
     * @param id die BefehlsID
     * @return der gefundene Befehl oder null
     */
    protected abstract NetCommand getCommand(int id);

    /**
     * Führt den gegebenen Befehl mit den berechneten Daten aus. Es sich sicher,
     * cmd auf den Typ zu casten, den getCommand gegeben hat.
     *
     * @param cmd der Befehl der ausgeführt werden soll
     * @param data die Daten die für diesen Befehl in diesem Paket enthalten
     * waren
     */
    protected abstract void runCommand(NetCommand cmd, byte[] data);

    /**
     * Pre-Executed den gegebenen Befehl mit den berechneten Daten. Diese
     * Methode wird nur für netzwerkinterne Kommandos ausgeführt.
     *
     * @param cmd der Befehl, der preexecuted werden soll
     * @param data die Daten die für diesen Befehl in diesem Paket enthalten
     * waren
     */
    protected abstract void preRunCommand(NetCommand cmd, byte[] data);

    /**
     * Liefert den Index dieses Pakets.
     *
     * @return den Index
     */
    public short getIndex() {
        return index;
    }

    public void compute() {
        internalCompute(false);
    }

    public void preCompute() {
        internalCompute(true);
    }

    private void internalCompute(boolean pre) {
        int nextCmdIndex = getInitialCmdIndex();
        while (nextCmdIndex < rawData.length) {
            int cmdID = rawData[nextCmdIndex++];
            if (cmdID < 0) {
                cmdID += 256;
            }
            if (cmdID == 0) {
                // NOOP
                continue;
            }
            NetCommand cmd = getCommand(cmdID);
            if (cmd == null) {
                System.out.println("WARNING: NET: ignoring unknown cmd! (id: " + cmdID);
                continue;
            }
            int dataSize = cmd.isVariableSize() ? cmd.getSize(rawData[nextCmdIndex]) : cmd.getSize((byte) 0);
            byte[] data = new byte[dataSize];
            // Daten kopieren
            if (nextCmdIndex + dataSize <= rawData.length) {
                for (int i = 0; i < dataSize; i++) {
                    data[i] = rawData[nextCmdIndex + i];
                }
            } else {
                System.out.println("WARNING: NET: illegal cmd size, insufficient data bytes! (id: " + cmdID + " size: " + dataSize);
                // In diesem Fall ist wirklich gar nichts mehr zu retten abbrechen!
                break;
            }
            if (!pre || cmdID >= 0x80) {
                // Ausführen
                try {
                    if (!pre) {
                        runCommand(cmd, data);
                    } else {
                        preRunCommand(cmd, data);
                    }
                } catch (Exception ex) {
                    System.out.println("WARNING: NET: Execution of packet failed with Exception: " + ex);
                    ex.printStackTrace();
                }
            }
            nextCmdIndex += dataSize;
        }
    }
}
