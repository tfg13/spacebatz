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

import de._13ducks.spacebatz.shared.DefaultSettings;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
 * Das OpenAL-Soundmodul.
 * Lädt alle Ogg-Dateien im "sound"-Ordner beim Starten.
 * Kann Soundeffekte und Musik abspielen, pausieren und stoppen.
 *
 * @author michael
 */
public class SoundEngine implements SoundProvider{

    static {
        // Hack, um nachträglich java.library.path zu setzen.
        try {
            System.setProperty("java.library.path", "native/");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println("[ERROR]: Failed to set library lookup path! Details:");
            ex.printStackTrace();
        }
    }
    private SoundSystem soundSystem;
    private boolean loading = true;

    /**
     * Konstruktor.
     *
     * Lädt in einem neuen thread alle ogg-Sounds aus dem "sound"-Verzeichnis
     */
    public SoundEngine() {
        soundSystem = initSoundSystem();
        loadSounds();
    }

    private void loadSounds() {
        Thread soundLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                File soundFolder = new File("sound/effects/");
                for (int i = 0; i < soundFolder.listFiles().length; i++) {
                    File sound = soundFolder.listFiles()[i];
                    if (sound.getName().contains(".ogg")) {
                        try {
                            soundSystem.loadSound(sound.toURL(), sound.getName());

                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                loading = false;
            }
        });
        soundLoader.setName("soundLoader");
        soundLoader.start();

    }

    /**
     * Spielt einen Soundeffekt einmal ab.
     *
     * @param filename
     */
    public void soundEffect(String filename) {
        if (!loading) {
            soundSystem.quickPlay(false, filename, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
            soundSystem.removeTemporarySources();
        }
    }

    /**
     * Spielt Hintergrundmusik ab.
     *
     * @param identifier
     * @param filename
     */
    public void backgroundMusic(String filename, Boolean loop) {
        if(DefaultSettings.CLIENT_SFX_DISABLE_MUSIC){
            return;
        }
        File sound = new File(filename);
        URL url = null;
        try {
            url = sound.toURL();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        soundSystem.newStreamingSource(true, sound.getName(), url, sound.getName(), loop, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 1);
        soundSystem.play(sound.getName());
    }

    /**
     * Initialisiert das Soundsystem.
     *
     * @return
     */
    private SoundSystem initSoundSystem() {
        try {
            boolean openALCompatible = SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class);
            boolean javaSoundCompatible = SoundSystem.libraryCompatible(LibraryJavaSound.class);

            Class libraryType;
            if (openALCompatible) {
                libraryType = LibraryLWJGLOpenAL.class; // OpenAL
            } else if (javaSoundCompatible) {
                libraryType = LibraryJavaSound.class; // Java Sound
            } else {
                libraryType = Library.class; // "No Sound, Silent Mode"
            }

            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class); // Ogg-Codec laden
            SoundSystem mySoundSystem = new SoundSystem(libraryType); // Soundsystem initialisieren
            return mySoundSystem;
        } catch (Exception sse) {
            sse.printStackTrace();
            return null;
        }
    }

    /**
     * Gibt benutzten Speicher wieder frei.
     */
    public void shutdown() {
        soundSystem.cleanup();
    }

    public boolean isReady() {
        return !loading;
    }
}
