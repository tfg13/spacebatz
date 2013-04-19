package de._13ducks.spacebatz.client.sound;

/**
 *
 * @author michael
 */
public class SilentSoundEngine implements SoundProvider {

    @Override
    public void backgroundMusic(String filename, Boolean loop) {
    }

    @Override
    public void soundEffect(String filename) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
