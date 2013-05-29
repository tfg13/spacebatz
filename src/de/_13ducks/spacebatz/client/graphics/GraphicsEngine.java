package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.input.Input;
import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.overlay.impl.HudOverlay;
import de._13ducks.spacebatz.client.graphics.overlay.impl.Inventory;
import de._13ducks.spacebatz.client.graphics.overlay.impl.NetGraph;
import de._13ducks.spacebatz.client.graphics.overlay.impl.QuestControl;
import de._13ducks.spacebatz.client.graphics.overlay.impl.TerminalOverlay;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer;
import de._13ducks.spacebatz.client.graphics.skilltree.SkillTreeOverlay;
import static de._13ducks.spacebatz.shared.DefaultSettings.*;
import de._13ducks.spacebatz.shared.network.StatisticRingBuffer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
     * Das derzeit aktive Kern-Rendermodul.
     */
    private CoreRenderer coreRenderer;
    /**
     * Das derzeit aktive InputSystem.
     */
    private Input input;
    /**
     * Die Liste mit den aktiven Overlays.
     */
    private List<Overlay> overlays = new ArrayList<>();
    /**
     * Das God-Control, das auch Effekte und FX zeichent.
     */
    private LegacyRenderer godControl;
    /**
     * Der Skilltree.
     */
    private SkillTreeOverlay skilltree;
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

            // Komponenten erzeugen:
            input = new Input();

            godControl = new LegacyRenderer();
            skilltree = new SkillTreeOverlay();
            skilltree.init(new int[]{Keyboard.KEY_T}, true);
            overlays.add(new HudOverlay());
            overlays.add(new QuestControl());
            overlays.add(skilltree);
            Inventory inventory = new Inventory();
            inventory.init(new int[]{Keyboard.KEY_I}, true);
            overlays.add(inventory);
            overlays.add(new NetGraph());
            TerminalOverlay terminal = new TerminalOverlay();
            terminal.init(new int[]{Keyboard.KEY_F1}, true);
            overlays.add(terminal);

            TextWriter.initialize();

            coreRenderer = godControl;
            coreRenderer.setupShaders();


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

        // Haupt-Renderer:
        coreRenderer.render();


        // Alle aktiven Overlays:
        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            overlay.render();
        }

//        hudControl.render(renderer);
//        questControl.render(renderer);
        long ns2 = System.nanoTime();
        timing.push((int) (ns2 - ns));

        // Wenn ein Menü aktiv ist wird es gerendert und bekommt die Eingaben, wenn nicht bekommt das GodControl die Eingaben:
        input.syncInput();

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
     * Erzeugt eine Schadenszahl.
     *
     * @param damage
     * @param x
     * @param y
     */
    public void createDamageNumber(int damage, double x, double y) {
        LegacyRenderer.createDamageNumber(damage, x, y);
    }

    /**
     * Erzeugt einen Effekt.
     *
     * @param fx
     */
    public void addFx(Fx fx) {
        LegacyRenderer.addFx(fx);
    }

    public SkillTreeOverlay getSkillTree() {
        return skilltree;
    }

    public LegacyRenderer defactoRenderer() {
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
        if (GLContext.getCapabilities().OpenGL31) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 3.2 is ");
        if (GLContext.getCapabilities().OpenGL32) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 3.3 is ");
        if (GLContext.getCapabilities().OpenGL33) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 4.0 is ");
        if (GLContext.getCapabilities().OpenGL40) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 4.1 is ");
        if (GLContext.getCapabilities().OpenGL41) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 4.2 is ");
        if (GLContext.getCapabilities().OpenGL42) {
            System.out.println("SUPPORTED");
        } else {
            System.out.println("NOT SUPPORTED");
        }

        System.out.print("INFO: GFX: OpenGL 4.3 is ");
        if (GLContext.getCapabilities().OpenGL43) {
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

    /**
     * @return the input
     */
    public Input getInput() {
        return input;
    }
}
