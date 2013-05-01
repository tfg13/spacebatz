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
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.DefaultSettings;
import java.util.*;

/**
 * Verwaltet das Spawnen von Gegnern in der Nähe der Spieler
 *
 * @author tobi
 */
public class EnemySpawner {

    /**
     * Muss jeden Gametick aufgerufen werden. Entscheidet für alle Spieler, ob
     * neue Gegner benötigt werden Rechner nicht bei jedem Tick.
     */
    public static void tick() {
        Random random = new Random();
        if (Server.game.getTick() % (1000 / CompileTimeParameters.SERVER_TICKRATE / DefaultSettings.SERVER_SPAWNER_EXECSPERSEC) == 0) {
            System.out.println("FIXME: Re-implement spawning!");
        }
    }

    /**
     * Löscht alle Position aus der Liste, die derzeit nicht frei sind.
     *
     * @param positions die Liste
     */
    private static void removeNonFree(List<double[]> positions, double size) {
        Iterator<double[]> iter = positions.iterator();
        outer:
        while (iter.hasNext()) {
            double[] pos = iter.next();
            int x = (int) Math.round(pos[0]);
            int y = (int) Math.round(pos[1]);
            // Ein deltaxdelta-Feld auf Kollision überprüfen:
            int delta = (int) (size + 0.5);
            for (int tx = x - delta; tx < x + delta; tx++) {
                for (int ty = y - delta; ty < y + delta; ty++) {
                    if (tx < 0 || ty < 0 || tx >= Server.game.getLevel().getSizeX() || ty >= Server.game.getLevel().getSizeY() || Server.game.getLevel().getCollisionMap()[tx][ty]) {
                        iter.remove();
                        continue outer;
                    }
                }
            }
        }
    }
}
