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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * Die Soundengine
 *
 * @author michael
 */
public class SoundEngine {

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

    public static void main(String[] args) {
        SoundEngine s = new SoundEngine();

        s.playSound("sound.wav");
        s.tick();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SoundEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * Die Puffer für geladene Audiodateien
     */
    private HashMap<String, Integer> buffers;
    /**
     * Liste mit Sounds die permanent sind oder gerade laufen
     */
    private ArrayList<Sound> sounds;

    /**
     * Konstruktor.
     *
     * Lädt alle WAVE-Sounds aus dem "sound"-Verzeichnis
     */
    public SoundEngine() {
        buffers = new HashMap<>();
        sounds = new ArrayList<>();
        try {
            // AL initialisieren:
            AL.create();
            AL10.alListener3f(AL10.AL_POSITION, 1.0f, 1.0f, 1.0f);
            AL10.alListener3f(AL10.AL_VELOCITY, 0.0f, 0.0f, 0.0f);
            AL10.alListener3f(AL10.AL_ORIENTATION, 0.0f, 0.0f, 0.0f);

            // Sounds laden:
            File soundFolder = new File("sound");
            File[] soundFiles = soundFolder.listFiles();

            for (int i = 0; i < soundFiles.length; i++) {
                File file = soundFiles[i];
                if (file.getName().contains(".wav")) {
                    WaveData o = WaveData.create(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
                    int buffer = AL10.alGenBuffers();
                    AL10.alBufferData(buffer, o.format, o.data, o.samplerate);
                    buffers.put(file.getName(), buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spielt einen Soundeffekt ab
     *
     * @param name der Name der Sounddatei
     */
    public void playSound(String name) {
        if (buffers.containsKey(name)) {
            Sound sound = new Sound(buffers.get(name), false);
            sounds.add(sound);
            sound.play();
        } else {
            throw new RuntimeException("Sound " + name + " not found.");
        }
    }

    /**
     * Gibt einen neuen Soundeffekt zurück.
     *
     * @param name der Name der Audiodate
     * @return ein Sound-Objekt
     */
    public Sound createSound(String name) {
        Sound sound;
        if (buffers.containsKey(name)) {
            sound = new Sound(buffers.get(name), true);
            sounds.add(sound);

        } else {
            throw new RuntimeException("Sound " + name + " not found.");
        }
        return sound;
    }

    /**
     * Gibt benutzten Speicher wieder frei.
     */
    private void cleanUp() {
        for (int s : buffers.values()) {
            AL10.alDeleteBuffers(s);
        }
        for (Sound s : sounds) {
            AL10.alDeleteSources(s.getSource());
        }
        AL.destroy();
    }

    /**
     * Löscht bereits abgespielte Sounds.
     */
    public void tick() {
        Iterator<Sound> iter = sounds.iterator();
        while (iter.hasNext()) {
            Sound s = iter.next();
            if (s.isDisposable()) {
                iter.remove();
            }
        }
    }
}
