package de._13ducks.spacebatz.client.sound;

import javax.sound.midi.Soundbank;

/**
 *
 * @author michael
 */
public interface SoundProvider {

    public boolean isReady();

    public void backgroundMusic(String filename, Boolean loop);

    public void soundEffect(String filename);

    public void shutdown();
}
