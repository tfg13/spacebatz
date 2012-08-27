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

/**
 * Ein Netzwerkbefehl, den der Server ausführen kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class CTSCommand {

    /**
     * Führt diesen Serverbefehl aus.
     *
     * @param client der Client, von dem dieser Befehl kommt
     * @param data die Daten, die der Client mitgeschickt hat
     */
    public abstract void execute(Client client, byte[] data);

    /**
     * Sagt, ob die Größe dieses Pakets fest oder variabel ist.
     * Falls fest, bekommt getSize keine Daten und muss die Größe liefern.
     * Fall flexibel bekommt getSize das erste byte danach (falls vorhanden)
     * @return true, wenn Größe variabel
     */
    public abstract boolean isVariableSize();

    /**
     * Liefert die Größe des Datensegments dieses Commands.
     * Die Größe des Datensegments ist ohne die Kommandonummer selbst,
     * also z.B. 0, wenn durch das pure Auftreten dieses Kommandos schon alles gesagt ist.
     * Das Verhalten dieser Methode hängt von der Antwort von isVaribleSize() ab.
     * Wenn die Größe flexibel ist, muss sie im ersten Byte stehen, das wird dann hier übergeben.
     * Aus diesem Grund muss die Größe von flexiblen Datenpaketen mindestens 1 betragen!
     * Wenn die Größe fest ist, ist der Übergabewert nicht definiert.
     *
     * @param sizeData der erste Datenblock, falls flexibel, sonst nicht definiert
     * @return die Anzahl der Datenblöcke, eventuell mitgelieferter inklusive, Kommandonummer selber exklusiv.
     */
    public abstract int getSize(byte sizeData);
}
