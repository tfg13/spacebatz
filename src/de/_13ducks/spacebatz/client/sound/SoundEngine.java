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
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;

/**
 * Das OpenAL-Soundmodul. 
 * Lädt alle Ogg-Dateien im "sound"-Ordner beim Starten.
 * Kann Soundeffekte und Musik abspielen, pausieren und stoppen.
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
            AL10.alListener3f(AL10.AL_POSITION, 10.0f, 1.0f, 1.0f);
            AL10.alListener3f(AL10.AL_VELOCITY, 0.0f, 0.0f, 0.0f);
            AL10.alListener3f(AL10.AL_ORIENTATION, 0.0f, 0.0f, 0.0f);
            
            // Sounds laden:
            File soundFolder = new File("sound");
            File[] soundFiles = soundFolder.listFiles();
            for (int i = 0; i < soundFiles.length; i++) {
                File file = soundFiles[i];
                if (file.getName().toLowerCase().matches(".+\\.ogg")) {
                    OggData data = new OggDecoder().getData(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
                    int format = data.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
                    int buffer = AL10.alGenBuffers();
                    AL10.alBufferData(buffer, format, data.data, data.rate);
                    buffers.put(file.getName(), buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("SoundEngine initialised (" + buffers.size() + " sounds loaded).");
    }

    /**
     * Spielt einen Soundeffekt asynchron ab.
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
     * Gibt einen neuen Soundeffekt zurück, der dann abgespielt und wieder angehalten werden kann.
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
    private void shutdown() {
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
    public void deleteUnusedSounds() {
        Iterator<Sound> iter = sounds.iterator();
        while (iter.hasNext()) {
            Sound s = iter.next();
            if (s.deleteMe()) {
                iter.remove();
            }
        }
    }
}
