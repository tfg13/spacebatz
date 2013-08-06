package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.input.Input;
import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.overlay.impl.HudOverlay;
import de._13ducks.spacebatz.client.graphics.overlay.impl.TerminalOverlay;
import de._13ducks.spacebatz.client.graphics.overlay.impl.inventory.InventoryOverlay;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer;
import de._13ducks.spacebatz.client.graphics.renderer.impl.OpenGL32CoreRenderer;
import de._13ducks.spacebatz.client.graphics.skilltree.SkillTreeOverlay;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import static de._13ducks.spacebatz.shared.DefaultSettings.*;
import de._13ducks.spacebatz.shared.network.StatisticRingBuffer;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Die GrafikEngine. Rendert das Spiel (Map, Entities, Bullets...) und genau ein Menü (z.B. Inventar, Hauptmenü, ...). Wenn ein Menü aktiv ist bekommt es Alle Eingaben.
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
     * Index des Game-Shaders im Shader-Array.
     */
    public static final int SHADER_INDEX_GAME = 0;
    /**
     * Index des Overlay-Shaders im Shader-Array.
     */
    public static final int SHADER_INDEX_OVERLAYS = 1;
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
     * Alle Shaderadressen.
     */
    private int[] shader;
    /**
     * Der Skilltree.
     */
    private SkillTreeOverlay skilltree;
    /**
     * Das Inventar
     */
    private InventoryOverlay inventory;
    private ShadowAnimator shadowAnimator = new ShadowAnimator();
    //DEBUG
    public static StatisticRingBuffer timing = new StatisticRingBuffer(60);
    /**
     * Allow OpenGL 3+? (non-legacy requires 3.2)
     */
    private boolean legacyMode = false;

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
            Display.setDisplayMode(new DisplayMode(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y));
            PixelFormat pixelFormat = new PixelFormat();
            Display.create(new PixelFormat(8, 8, 8)); // Die dritte acht erzeugt/aktiviert den Stencil-Buffer mit 8 Bits pro Pixel.
            Display.setVSyncEnabled(CLIENT_GFX_VSYNC);
            glEnable(GL_BLEND); // Transparenz in Texturen erlauben
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus
            // Hat die Platform alles was wir brauchen?
            // Erst nach dem Fenster-erzeugen, manche Tests brauchen einen aktiven OpenGL-Context
            checkCapabilities();

            // Tastatureingaben einstallen:
            Keyboard.enableRepeatEvents(true);

            // Shader setup
            shader = ShaderLoader.load();
            VAOFactory.init(GL20.glGetUniformLocation(shader[SHADER_INDEX_OVERLAYS], "colorTexMode"));

            // Komponenten erzeugen:
            input = new Input();

            skilltree = new SkillTreeOverlay();
            skilltree.init(new int[]{Keyboard.KEY_T}, true);
            overlays.add(skilltree);

            inventory = new InventoryOverlay();
            overlays.add(getInventory());
            overlays.add(new HudOverlay());


//                overlays.add(new QuestControl());
//                Inventory inventory = new Inventory();
//                inventory.init(new int[]{Keyboard.KEY_I}, true);
//                overlays.add(inventory);
//                overlays.add(new NetGraph());
            TerminalOverlay terminal = new TerminalOverlay();
            terminal.init(new int[]{Keyboard.KEY_F1}, true);
            overlays.add(terminal);
            TextWriter.initialize(shader[SHADER_INDEX_OVERLAYS]);

            // maybe some day...
            //coreRenderer = new OpenGL32CoreRenderer();
            coreRenderer = new LegacyRenderer();

            coreRenderer.setupShaders(shader);

            // Shader für Overlays bauen:
            setupOverlayShader();

            coreRenderer.reEnableShader();


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

        // Zeitmessung
        long ns = System.nanoTime();

        // Subtick einfrieren
        SubTick.freezeSubTick();

        // Input
        input.syncInput();

        // Haupt-Renderer:
        coreRenderer.render();

        // Overlay-Shader:
        GL20.glUseProgram(shader[SHADER_INDEX_OVERLAYS]);

        // Alle aktiven Overlays:
        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            overlay.render();
        }

        // Overlay-Shader abschalten
        coreRenderer.reEnableShader();

        // Timing-Messung
        long ns2 = System.nanoTime();
        timing.push((int) (ns2 - ns));
        if (GameClient.frozenGametick % 60 == 0) {
            System.out.println("AFT: " + timing.getNiceAvg());
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
     * Erzeugt eine Schadenszahl.
     *
     * @param damage
     * @param x
     * @param y
     */
    public void createDamageNumber(int damage, double x, double y) {
        System.out.println("AddMe: Add Particle System to re-enable Damagenumbers");
    }

    /**
     * Erzeugt einen Effekt.
     *
     * @param fx
     */
    public void addFx(Fx fx) {
        System.out.println("AddMe: Re-implement addFx");
    }

    public SkillTreeOverlay getSkillTree() {
        return skilltree;
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

        switch (DefaultSettings.CLIENT_GFX_OPENGL_MODE) {
            case 0:
                if (GLContext.getCapabilities().OpenGL32) {
                    System.out.println("INFO: GFX: OpenGL 3.2 mode enabled.");
                    legacyMode = false;
                } else if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
                    System.out.println("INFO: GFX: OpenGL 3.2 not available, launching in legacy mode.");
                    legacyMode = true;
                } else {
                    System.out.println("ERROR: GFX: Missing support for OpenGL 2.1 and/or VertexBufferObjects (VBOs). Both are required. sorry.");
                }
                break;
            case 1:
                System.out.println("INFO: GFX: Legacy mode forced by config.");
                legacyMode = true;
                break;
            case 2:
                System.out.println("INFO: GFX: Advanced mode forced by config.");
                legacyMode = false;
                break;
        }
    }

    /**
     * @return the input
     */
    public Input getInput() {
        return input;
    }

    /**
     * Setzt die Mausposition, notwendig, weil sich die Ansicht der Maus anpasst.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     */
    public void setMouseXY(double x, double y) {
        coreRenderer.setMouseXY(x, y);
    }

    public void levelChanged(int chunksX, int chunksY) {
        if (coreRenderer instanceof OpenGL32CoreRenderer) {
            ((OpenGL32CoreRenderer) coreRenderer).setLevelSize(chunksX, chunksY);
        }
    }

    public void chunkReceived(int chunkX, int chunkY) {
        if (coreRenderer instanceof OpenGL32CoreRenderer) {
            ((OpenGL32CoreRenderer) coreRenderer).chunkReceived(chunkX, chunkY);
        }
    }

    public void minorTopChange(int x, int y) {
        if (coreRenderer instanceof OpenGL32CoreRenderer) {
            ((OpenGL32CoreRenderer) coreRenderer).minorTopChange(x, y);
        }
    }

    public double getPanX() {
        return coreRenderer.getPanX();
    }

    public double getPanY() {
        return coreRenderer.getPanY();
    }

    public void setShowNickNames(int i) {
        System.out.println("AddMe: Re-implement nicknames");
    }

    private void setupOverlayShader() {
        GL20.glUseProgram(shader[SHADER_INDEX_OVERLAYS]);
        // Projection-Matrix erstellen, auf Pixel mappen
        Matrix4f projection = new Matrix4f();
        projection.m30 = -1f;
        projection.m31 = -1f;
        projection.m00 = 2f / DefaultSettings.CLIENT_GFX_RES_X;
        projection.m11 = 2f / DefaultSettings.CLIENT_GFX_RES_Y;
        projection.m22 = -1f;
        // In Buffer packen
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        projection.store(buffer);
        buffer.flip();
        // Zur Grafikkarte hochladen
        int projectionUniformAdr = GL20.glGetUniformLocation(shader[SHADER_INDEX_OVERLAYS], "projectionM");
        GL20.glUniformMatrix4(projectionUniformAdr, false, buffer);
        // Model-Matrix bauen und hochladen (Identität)
        Matrix4f model = new Matrix4f();
        model.store(buffer);
        buffer.flip();
        int modelUniformAdr = GL20.glGetUniformLocation(shader[SHADER_INDEX_OVERLAYS], "modelM");
        GL20.glUniformMatrix4(modelUniformAdr, false, buffer);
    }

    /**
     * @return the inventory
     */
    public InventoryOverlay getInventory() {
        return inventory;
    }

    public static class SubTick {

        public static double frozenSubTick = 0;

        private SubTick() {
        }

        private static void freezeSubTick() {
            frozenSubTick = GameClient.getNetwork2().getSubTick();
        }
    }
}
