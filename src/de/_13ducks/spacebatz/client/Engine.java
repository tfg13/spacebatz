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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.GraphicsEngine;
import de._13ducks.spacebatz.client.sound.SoundEngine;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.DefaultSettings;

import org.lwjgl.Sys;

/**
 * Die ClientEngine. Läßt Netzwerk und Grafik in einer Endlosschleife laufen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Engine {

    /**
     * Die Grafik-Engine.
     */
    private GraphicsEngine graphics;
    /**
     * Gibt an ob der Mainloop läuft.
     */
    private boolean run;
    /**
     * Der fps-Counter.
     */
    private int fpsCount;
    /**
     * Die aktuelle FPS-Zahl.
     */
    private int fps;
    /**
     * Zeitpunkt der letzten FPS-Messung.
     */
    private long lastFPS;

    /**
     * Initialisiert die Engine.
     */
    public Engine() {
        run = true;
        graphics = new GraphicsEngine();
        //SoundEngine s = new SoundEngine();
//        Sound sound = s.createSound("ragtime_warfare_audacity.ogg");
//        sound.play();

    }

    /**
     * Startet die Grafik. Verwendet den gegebenen Thread (forkt *nicht* selbstständig!).
     */
    public void start() {
        graphics.initialise();
        lastFPS = getTime();
        GameClient.getNetwork2().startSurveillance();
        boolean musicPlaying = false;
        while (run) {
            if ( GameClient.soundEngine.isReady() && !musicPlaying) {
                musicPlaying = true;
                GameClient.soundEngine.backgroundMusic("sound/music/T!.ogg", true);
            }
            // Gametick updaten:
            GameClient.updateGametick();
            // Input neues Netzwerksystem verarbeiten
            GameClient.getNetwork2().inTick();
            // Render-Code
            graphics.tick();
            // Gametick für alle Entities berechnen:
            GameClient.gameTick();
            // Output neues Netzwerksystem:
            GameClient.getNetwork2().outTick();
            if (getTime() - lastFPS > 1000) {
                fps = fpsCount;
                fpsCount = 0;
                lastFPS += 1000;
            }
            fpsCount++;
        }
        // Netzwerk abmelden:
        GameClient.getNetwork2().disconnect();
        graphics.shutDown();

    }

    /**
     * Gibt die GrafikEngine zurück.
     *
     * @return
     */
    public GraphicsEngine getGraphics() {
        return graphics;
    }

    /**
     * Liefert eine wirklich aktuelle Zeit. Nicht so gammlig wie System.currentTimeMillis();
     *
     * @return eine wirklich aktuelle Zeit.
     */
    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Gibt die aktuellen FPS zurück.
     *
     * @return
     */
    public int getFps() {
        return fps;
    }

    /**
     * Beendet den Mainloop und damit die Engine.
     */
    public void stopEngine() {
        run = false;
    }
}
