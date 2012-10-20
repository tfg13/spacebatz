package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.graphics.controls.GodControl;
import de._13ducks.spacebatz.client.graphics.controls.SkillTreeControl;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 * Die GrafikEngine. Zeichnet den Bildschirm und verarbeitet Eingaben.
 *
 * @author michael
 */
public class GraphicsEngine {

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
     * Zeichnet Text.
     */
    TextWriter textWriter;
    /**
     * Die Kamera mit Position und Zoom.
     */
    Camera camera;
    /**
     * Die Liste der Controls (z.B. Inventar, HUD, ...).
     */
    private ArrayList<Control> controls;
    /**
     * Das God-Control, das auch Effekte und FX zeichent.
     */
    private GodControl godControl;
    /**
     * Der Renderer, der Geometrie und Texturen zeichnet.
     */
    private Renderer renderer;

    /**
     * Initialisiert die GrafikEngine.
     */
    public GraphicsEngine() {
        controls = new ArrayList<>();
    }

    /**
     * Erzeugt ein Fenster und initialisiert die Grafik.
     */
    public void initialise() {
        try {
            // Kamera erzeugen:
            camera = new Camera();

            // Fenster erzeugen:
            Display.setDisplayMode(new DisplayMode(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y));
            Display.create();
            Display.setVSyncEnabled(CLIENT_GFX_VSYNC);

            // OpenGL-Init:
            // Orthogonalperspektive mit korrekter Anzahl an Tiles initialisieren.
            GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
            glEnable(GL_TEXTURE_2D); // Aktiviert Textur-Mapping
            //glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); // Zeichenmodus auf überschreiben stellen
            glEnable(GL_BLEND); // Transparenz in Texturen erlauben
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus

            // Tastatureingaben einstallen:
            Keyboard.enableRepeatEvents(true);

            // Renderer Initialisieren:
            renderer = new Renderer(camera);

            // Text initialisiern:
            textWriter = new TextWriter();

            // Controls erzeugen:
            godControl = new GodControl(renderer);
            godControl.setActive(true);
            controls.add(godControl);
            
            controls.add(new SkillTreeControl(renderer));

        } catch (Exception ex) {
            ex.printStackTrace();
            Display.destroy();
            return;
        }
    }

    /**
     * Zerstört das Fenster der Graphikengine.
     */
    public void shutDown() {
        // Ende der Mainloop.
        Display.destroy();
    }

    /**
     * Rendert den Bildschirm und verarbeitet Eingaben.
     */
    public void tick() {
        for (Control c : controls) {
            if (c.isActive()) {
                c.render(renderer);
                c.input();
            }
        }
    }

    /**
     * Gibt die Kamera zurück.
     *
     * @return
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Erzeugt eine Schadenszahl.
     *
     * @param damage
     * @param x
     * @param y
     */
    public void createDamageNumber(int damage, double x, double y) {
        godControl.createDamageNumber(damage, x, y);
    }

    /**
     * Erzeugt einen Effekt.
     *
     * @param fx
     */
    public void addFx(Fx fx) {
        godControl.addFx(fx);
    }
}
