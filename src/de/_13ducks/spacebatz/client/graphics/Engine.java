package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import java.io.IOException;
import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Kern der Grafikengine. Startet die Grafikausgabe
 *
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
     * Die Ground-Tilemap
     */
    private Texture groundTiles;

    /**
     * Startet die Grafik. Verwendet den gegebenen Thread (forkt *nicht* selbstständig!).
     */
    public void start() {
        // Fenster aufmachen:
        try {
            Display.setDisplayMode(new DisplayMode(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y));
            Display.create();

            // Fähigkeiten ausgeben, bleibt mal noch drin.
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
        // OpenGL-Init
        initGL();
        // Texturen laden
        try {
            loadTex();
        } catch (IOException ex) {
            ex.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        // Render-Mainloop:
        while (!Display.isCloseRequested()) {
            // Render-Code
            render();
            // Frames limitieren:
            Display.sync(60);
            // Fertig, Puffer swappen:
            Display.update();
        }

        // Ende der Mainloop.
        Display.destroy();

    }

    /**
     * OpenGL initialisieren
     */
    private void initGL() {
        // Orthogonalperspektive mit korrekter Anzahl an Tiles initialisieren.
        GLU.gluOrtho2D(0, 0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM), CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    private void render() {
    }

    /**
     * Läd alle benötigten Texturen
     */
    private void loadTex() throws IOException {
        groundTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/ground.png"));
    }
}
