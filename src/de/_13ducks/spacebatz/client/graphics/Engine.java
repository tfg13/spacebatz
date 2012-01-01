package de._13ducks.spacebatz.client.graphics;

import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

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
            ContextCapabilities con = GLContext.getCapabilities();
            boolean FBOEnabled = con.GL_EXT_framebuffer_object;
            System.out.println("OpenGL Major Version Support:");
            System.out.println("OpenGL 1.1 Support: " + con.OpenGL11);
            System.out.println("OpenGL 1.2 Support: " + con.OpenGL12);
            System.out.println("OpenGL 1.3 Support: " + con.OpenGL13);
            System.out.println("OpenGL 1.4 Support: " + con.OpenGL14);
            System.out.println("OpenGL 1.5 Support: " + con.OpenGL15);
            System.out.println("OpenGL 2.0 Support: " + con.OpenGL20);
            System.out.println("OpenGL 2.1 Support: " + con.OpenGL21);
            System.out.println("OpenGL 3.0 Support: " + con.OpenGL30);
            System.out.println("OpenGL 3.1 Support: " + con.OpenGL31);
            System.out.println("OpenGL 3.2 Support: " + con.OpenGL32);
            System.out.println("OpenGL 3.3 Support: " + con.OpenGL33);
            System.out.println("OpenGL 4.0 Support: " + con.OpenGL40);
            System.out.println("OpenGL 4.1 Support: " + con.OpenGL41);
            System.out.println("OpenGL 4.2 Support: " + con.OpenGL42);
            System.out.println("---------------------------------------------");
            System.out.println("Individual Feature support:");
            System.out.println("FBO-Support: " + FBOEnabled);
            System.out.println("VBO-Support: " + con.GL_ARB_vertex_buffer_object);
            System.out.println("Max Number of VBO-Vertices: " + GL12.GL_MAX_ELEMENTS_VERTICES);
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
