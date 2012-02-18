package de._13ducks.spacebatz.client.sound;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.WaveData;
import java.lang.reflect.Field;

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
     * 
     * @param args 
     */
    public static void main(String[] args) {
        new SoundEngine().execute();

    }
    /**
     * Buffers hold sound data.
     */
    IntBuffer buffer = BufferUtils.createIntBuffer(1);
    /**
     * Sources are points emitting sound.
     */
    IntBuffer source = BufferUtils.createIntBuffer(1);
    /**
     * Position of the source sound.
     */
    FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    /**
     * Velocity of the source sound.
     */
    FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    /**
     * Position of the listener.
     */
    FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    /**
     * Velocity of the listener.
     */
    FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    /**
     * Orientation of the listener. (first 3 elements are "at", second 3 are "up")
     */
    FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).rewind();

    /**
     * boolean LoadALData()
     *
     * This function will load our sample data from the disk using the Alut utility and send the data into OpenAL as a buffer. A source is then also
     * created to play that buffer.
     */
    int loadALData() {
        // Load wav data into a buffer.
        AL10.alGenBuffers(buffer);

        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }

        //Loads the wave file from your file system
    /*
         * java.io.FileInputStream fin = null; try { fin = new java.io.FileInputStream("FancyPants.wav"); } catch (java.io.FileNotFoundException ex) {
         * ex.printStackTrace(); return AL10.AL_FALSE; } WaveData waveFile = WaveData.create(fin); try { fin.close(); } catch (java.io.IOException ex)
         * { }
         */

        //Loads the wave file from this class's package in your classpath


//        OggData o = null;
//        try {
//            FileInputStream is = new FileInputStream("/home/michael/Desktop/sound.ogg");
//
//            o = new org.newdawn.slick.openal.OggDecoder().getData(is);
//
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        WaveData o = null;
        BufferedInputStream is;
        try {
            is = new BufferedInputStream(new FileInputStream("/home/michael/Desktop/sound.wav"));
            o = WaveData.create(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SoundEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        AL10.alBufferData(buffer.get(0), o.format, o.data, o.samplerate);


        // Bind the buffer with the source.
        AL10.alGenSources(source);

        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }

        AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
        AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
        AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
        AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

        // Do another error check and return.
        if (AL10.alGetError() == AL10.AL_NO_ERROR) {
            return AL10.AL_TRUE;
        }

        return AL10.AL_FALSE;
    }

    /**
     * void setListenerValues()
     *
     * We already defined certain values for the Listener, but we need to tell OpenAL to use that data. This function does just that.
     */
    void setListenerValues() {
        AL10.alListener(AL10.AL_POSITION, listenerPos);
        AL10.alListener(AL10.AL_VELOCITY, listenerVel);
        AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
    }

    /**
     * void killALData()
     *
     * We have allocated memory for our buffers and sources which needs to be returned to the system. This function frees that memory.
     */
    void killALData() {
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
    }

    public void execute() {
        // Initialize OpenAL and clear the error bit.
        try {
            AL.create();
        } catch (LWJGLException le) {
            le.printStackTrace();
            return;
        }
        AL10.alGetError();

        // Load the wav data.
        if (loadALData() == AL10.AL_FALSE) {
            System.out.println("Error loading data.");
            return;
        }

        setListenerValues();

        AL10.alSourcePlay(source.get(0));
        try {
            Thread.sleep(100000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SoundEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        killALData();
        AL.destroy();
    }
}
