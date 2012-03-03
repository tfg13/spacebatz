package de._13ducks.spacebatz.client.sound;

import org.lwjgl.openal.AL10;

/**
 * Ein Soundeffekt Sounds werden gelöscht, wenn sie fertig abgespielt sind, es sei denn sie sind permanent.
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
    public Sound(int buffer, boolean permanent) {
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
    public int getSource() {
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
     * Gibt true zurück, wenn der Sound fertig ist und nicht permanent ist
     *
     * @return true, wenn der Sound gelöscht werden soll
     */
    public boolean isDisposable() {
        if (permanent || AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
            return false;
        } else {
            // Das Source-Objekt löschen:
            AL10.alDeleteSources(source);
            return true;
        }

    }
}
