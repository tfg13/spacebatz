package de._13ducks.spacebatz.client.graphics;

import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * Kern der Grafikengine. Startet die Grafikausgabe
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Engine {

    static {
        // Hack, um nachträglich java.library.path zu setzen.
        try {
            System.setProperty("java.library.path", "native/");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception ex) {
            System.out.println("[ERROR]: Failed to set library lookup path! Details:");
            ex.printStackTrace();
        }
    }

    /**
     * Startet die Grafik.
     * Verwendet den gegebenen Thread (forkt *nicht* selbstständig!).
     */
    public void start() {
        // Fenster aufmachen:
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
            return;
        }

        // Render-Mainloop:
        while (!Display.isCloseRequested()) {
            // Render-Code...

            // Frames limitieren:
            Display.sync(60);
            // Fertig, Puffer swappen:
            Display.update();
        }

        // Ende der Mainloop.
        Display.destroy();

    }
}
