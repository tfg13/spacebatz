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
package de._13ducks.spacebatz.client.sound;

import org.lwjgl.openal.AL10;

/**
 * Steuert das Abspielen eines Soundeffekts.
 * Sounds werden gelöscht, wenn sie fertig abgespielt sind, es sei denn sie sind permanent.
 *
 * @author michael
 */
public class Sound {

    /**
     * Der ALINT der die Source beschreibt
     */
    private int source;
    /**
     * Gibt an ob dieser Sound permanent ist
     */
    private boolean permanent;

    /**
     * Erstellt einen neuen Sound
     *
     * @param buffer der Puffer, aus dem der Sound geladen werden soll
     * @param permanent gibt an, ob der Sound permanent ist. permanente Sounds werden nicht gelöscht wenn sie fertig abgespielt sind.
     */
    protected Sound(int buffer, boolean permanent) {
        this.permanent = permanent;

        source = AL10.alGenSources();
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
        AL10.alSource3f(source, AL10.AL_POSITION, 1.0f, 1.0f, 1.0f);
        AL10.alSource3f(source, AL10.AL_VELOCITY, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Gibt den int der die Source beschreibt zurück
     *
     * @return der ALINT der die source beschreibt
     */
    protected int getSource() {
        return source;
    }

    /**
     * Spielt diesen Sound ab
     */
    public void play() {
        AL10.alSourcePlay(source);
    }

    /**
     * Pausiert diesen Sound
     */
    public void pause() {
        AL10.alSourcePause(source);
    }

    /**
     * Löscht den Sound ordnungsgemäss, so dass er keinen Speicher verschwendet.
     */
    public void dispose() {
        permanent = false;
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    /**
     * Gibt true zurück, wenn der Sound gelöscht werden kann.
     *
     * @return true, wenn der Sound fertig abgespielt ist und nicht permanent ist.
     */
    protected boolean deleteMe() {
        if (permanent || isPlaying()) {
            return false;
        } else {
            // Das Source-Objekt löschen:
            AL10.alDeleteSources(source);
            return true;
        }

    }
}
