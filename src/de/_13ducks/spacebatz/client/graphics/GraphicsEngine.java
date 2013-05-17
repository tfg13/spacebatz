package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.controls.GodControl;
import de._13ducks.spacebatz.client.graphics.controls.HudControl;
import de._13ducks.spacebatz.client.graphics.controls.Inventory;
import de._13ducks.spacebatz.client.graphics.controls.QuestControl;
import de._13ducks.spacebatz.client.graphics.skilltree.SkillTreeControl;
import static de._13ducks.spacebatz.shared.DefaultSettings.*;
import de._13ducks.spacebatz.shared.network.StatisticRingBuffer;
import java.lang.reflect.Field;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

/**
 * Die GrafikEngine. Rendert das Spiel (Map, Entities, Bullets...) und genau ein Menü (z.B. Inventar, Hauptmenü, ...).
 * Wenn ein Menü aktiv ist bekommt es Alle Eingaben.
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
     * Die Kamera mit Position und Zoom.
     */
    Camera camera;
    /**
     * Das God-Control, das auch Effekte und FX zeichent.
     */
    private GodControl godControl;
    /**
     * Das Hud-Control, zeichnet HP-Balken etc.
     */
    private HudControl hudControl;
    /**
     * Infos über Quests.
     */
    private QuestControl questControl;
    /**
     * Der Skilltree.
     */
    private SkillTreeControl skilltree;
    /**
     * Das Inventar
     */
    private Inventory inventory;
    /**
     * Das aktive Menü, das über das Spiel gerendert wird.
     * z.B. Inventar
     */
    private Control activeMenu;
    /**
     * Der Renderer, der Geometrie und Texturen zeichnet.
     */
    private Renderer renderer;
    private ShadowAnimator shadowAnimator = new ShadowAnimator();
    //DEBUG
    public static StatisticRingBuffer timing = new StatisticRingBuffer(60);

    /**
     * Initialisiert die GrafikEngine.
     */
    public GraphicsEngine() {
    }

    /**
     * Erzeugt ein Fenster und initialisiert die Grafik.
     */
    public void initialise() {
        try {
            // Fenster erzeugen:
            Display.setDisplayMode(new DisplayMode(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y));
            Display.create(new PixelFormat(8, 8, 8)); // Die dritte acht erzeugt/aktiviert den Stencil-Buffer mit 8 Bits pro Pixel.
            Display.setVSyncEnabled(CLIENT_GFX_VSYNC);

            // Hat die Platform alles was wir brauchen?
            // Erst nach dem Fenster-erzeugen, manche Tests brauchen einen aktiven OpenGL-Context
            checkCapabilities();

            // Kamera erzeugen:
            camera = new Camera(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y);
            // OpenGL-Init:
            // Orthogonalperspektive mit korrekter Anzahl an Tiles initialisieren.
            // GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE));
            glEnable(GL_TEXTURE_2D); // Aktiviert Textur-Mapping
            //glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); // Zeichenmodus auf überschreiben stellen
            glEnable(GL_BLEND); // Transparenz in Texturen erlauben
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus
            glClearStencil(0); // Wert von Stencil-Clear auf 0 setzen.

            // Tastatureingaben einstallen:
            Keyboard.enableRepeatEvents(true);

            // Renderer Initialisieren:
            renderer = new Renderer(camera);

            // Controls erzeugen:
            godControl = new GodControl(renderer);
            hudControl = new HudControl(renderer);
            questControl = new QuestControl();
            skilltree = new SkillTreeControl(renderer);
            inventory = new Inventory(renderer);


        } catch (Exception ex) {
            ex.printStackTrace();
            Display.destroy();
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
        // Schatten-Aufdecken animieren:
        shadowAnimator.tick();

        // Bild löschen, neu malen
        glClear(GL_COLOR_BUFFER_BIT);
        long ns = System.nanoTime();
        godControl.render(renderer);
        hudControl.render(renderer);
        questControl.render(renderer);
        long ns2 = System.nanoTime();
        timing.push((int) (ns2 - ns));

        // Wenn ein Menü aktiv ist wird es gerendert und bekommt die Eingaben, wenn nicht bekommt das GodControl die Eingaben:
        if (activeMenu == null) {
            godControl.input();
        } else {
            activeMenu.render(renderer);
            activeMenu.input();
        }

        // Fertig, Puffer swappen:
        Display.update();

        // Frames limitieren:
        Display.sync(CLIENT_GFX_FRAMELIMIT);

        // Beenden, wenn auf das X geklickt wurde.
        if (Display.isCloseRequested()) {
            GameClient.getNetwork2().disconnect();
            GameClient.getEngine().stopEngine();
            GameClient.soundEngine.shutdown();
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
        GodControl.createDamageNumber(damage, x, y);
    }

    /**
     * Erzeugt einen Effekt.
     *
     * @param fx
     */
    public void addFx(Fx fx) {
        GodControl.addFx(fx);
    }

    public SkillTreeControl getSkillTree() {
        return skilltree;
    }

    /**
     * Schält das Skilltreemenü um.
     */
    public void toggleSkillTree() {
        if (activeMenu == null) {
            activeMenu = skilltree;
            GameClient.getEngine().getGraphics().defactoRenderer().scrollFreeze(true);
        } else if (activeMenu == skilltree) {
            activeMenu = null;
            GameClient.getEngine().getGraphics().defactoRenderer().scrollFreeze(false);
        }
    }

    public void toggleInventory() {
        if (activeMenu == null) {
            activeMenu = inventory;
            GameClient.getEngine().getGraphics().defactoRenderer().scrollFreeze(true);
        } else if (activeMenu == inventory) {
            activeMenu = null;
            GameClient.getEngine().getGraphics().defactoRenderer().scrollFreeze(false);
        }
    }

    public GodControl defactoRenderer() {
        return godControl;
    }

    /**
     * Liefert den ShadowAnimator.
     *
     * @return der ShadowAnimator
     */
    public ShadowAnimator getShadowAnimator() {
        return shadowAnimator;
    }

    /**
     * True, wenn derzeit ein Menu eingeblendet wird, also der Input dahin soll.
     *
     * @return true bei eingeblendetem menu
     */
    public boolean isMenuActive() {
        return activeMenu != null;
    }

    private void checkCapabilities() {
        int cursorcap = Cursor.getCapabilities();
        System.out.print("INFO: GFX: 1-Bit cursor transparency is ");
        if ((cursorcap & Cursor.CURSOR_ONE_BIT_TRANSPARENCY) != 0) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: 8-Bit cursor transparency is ");
        if ((cursorcap & Cursor.CURSOR_8_BIT_ALPHA) != 0) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: Cursor animation is ");
        if ((cursorcap & Cursor.CURSOR_ANIMATION) != 0) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 3.0 is ");
        if (GLContext.getCapabilities().OpenGL30) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 3.1 is ");
        if (GLContext.getCapabilities().OpenGL30) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: Vertex Buffer Objects (VBO) are ");
        if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }
    }
}
