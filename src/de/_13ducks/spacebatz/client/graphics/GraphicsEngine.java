package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.Settings;
import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.*;
import de._13ducks.spacebatz.client.graphics.controls.GodControl;
import de._13ducks.spacebatz.client.network.NetStats;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_SHOOT;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

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
    TextWriter textWriter;
    Camera camera;
    private ArrayList<Control> controls;
    private GodControl godControl;

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

            // Text initialisiern:
            textWriter = new TextWriter();

            // Controls erzeugen:
            godControl = new GodControl();
            godControl.setActive(true);
            controls.add(godControl);

            // Die Controls initialisierne (achtung, wird warscheinlich entfernt werden!)
            for (Control c : controls) {
                c.initialise();
            }


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
                c.render(camera, textWriter);
                c.input();
            }
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void createDamageNumber(int damage, double x, double y) {
        godControl.createDamageNumber(damage, x, y);
    }

    public void addFx(Fx fx) {
        godControl.addFx(fx);
    }
}
