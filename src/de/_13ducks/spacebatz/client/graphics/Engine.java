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
package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.*;

/**
 * Kern der Grafikengine. Startet die Grafikausgabe
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Engine {

    private GraphicsEngine graphics;
    private boolean run;

    public Engine() {
        run = true;
        graphics = new GraphicsEngine();

    }

    /**
     * Startet die Grafik. Verwendet den gegebenen Thread (forkt *nicht* selbstständig!).
     */
    public void start() {
        graphics.start();
        while (run) {
            // Gametick updaten:
            GameClient.updateGametick();
            // Input neues Netzwerksystem verarbeiten
            GameClient.getNetwork2().inTick();
            // Render-Code
            graphics.tick();
            // Output neues Netzwerksystem:
            GameClient.getNetwork2().outTick();

        }
        // Netzwerk abmelden:
        GameClient.getNetwork2().disconnect();
        graphics.shutDown();

    }

    public GraphicsEngine getGraphics() {
        return graphics;
    }
}
