package de._13ducks.spacebatz.util.mapview;

import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.MapGen;
import de._13ducks.spacebatz.util.mapgen.MapParameters;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.Node;
import de._13ducks.spacebatz.util.geo.PolyMesh;
import de._13ducks.spacebatz.util.geo.Rect;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
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
 * Zeigt eine Map verschiebe- und zoombar in einem Fenster an.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MapViewer {

    private final int[][] ground;
    private final HashMap<String, Object> metadata;
    private PolyMesh visPolys;
    private MPolygon currentPoly;
    boolean renderFrame = false;
    float panX = 0;
    float panY = 0;
    float zoom = 1;

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
     * Tilemaps.
     */
    private Texture groundTiles;

    /**
     * Erzeugt einen neuen Mapviewer für das gegebene Level
     *
     * @param level
     */
    public MapViewer(Level level) {
        ground = level.ground;
        metadata = new HashMap<>();
        startRendering();
    }

    public MapViewer(InternalMap map) {
        ground = map.groundTex;
        metadata = map.metadata;
        if (metadata != null && metadata.containsKey("VIS_POLYS")) {
            visPolys = (PolyMesh) metadata.get("VIS_POLYS");
        }
        startRendering();
    }

    /**
     * Startet den Renderthread
     */
    private void startRendering() {
        final Thread input = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean repaint = false;
                    Display.processMessages();
                    if (Display.isCloseRequested()) {
                        System.exit(0);
                    }
                    while (Mouse.next()) {
                        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && currentPoly == null) {
                            // Search Poly and zoom:
                            if (visPolys != null) {
                                currentPoly = visPolys.polyFor((float) Mouse.getX() / Display.getWidth(), (float) Mouse.getY() / Display.getHeight(), false);
                                Rect zoomRect = currentPoly.outRect;
                                zoomPanTo((float) zoomRect.smallX, (float) zoomRect.smallY, (float) zoomRect.largeX - (float) zoomRect.smallX, (float) zoomRect.largeY - (float) zoomRect.smallY);
                                repaint = true;
                            }
                        } else if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
                            // Rechtklick, wieder zurück
                            currentPoly = null;
                            zoomPanTo(0, 0, 1, 1);
                            repaint = true;
                        }
                    }
                    if (repaint) {
                        synchronized (MapViewer.this) {
                            renderFrame = true;
                            MapViewer.this.notifyAll();
                        }
                    }
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }, "MAPVIEWER_INPUT");
        input.setDaemon(true);

        Thread renderer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Display.setDisplayMode(new DisplayMode(600, 600));
                    Display.create();
                    Display.setVSyncEnabled(true);
                } catch (LWJGLException ex) {
                    ex.printStackTrace();
                    return;
                }
                // OpenGL-Init
                initGL();
                // Daten laden
                try {
                    loadTex();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Display.destroy();
                    return;
                }
                input.start();

                // Render-Mainloop:
                while (!Display.isCloseRequested()) {
                    render();
                    // Fertig, Puffer swappen:
                    Display.update();
                    // Schlafen, bis es weiter geht:
                    synchronized (MapViewer.this) {
                        while (!renderFrame) {
                            try {
                                MapViewer.this.wait();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        renderFrame = false;
                    }
                }

                // Ende der Mainloop.
                Display.destroy();
            }
        }, "MAPVIEW_RENDERER");
        renderer.start();
    }

    /**
     * OpenGL initialisieren
     */
    private void initGL() {
        // 0-1 Koordinatensystem
        GLU.gluOrtho2D(0, 1, 0, 1);
        glEnable(GL_TEXTURE_2D); // Aktiviert Textur-Mapping
        //glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); // Zeichenmodus auf überschreiben stellen
        glEnable(GL_BLEND); // Transparenz in Texturen erlauben
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus
        Keyboard.enableRepeatEvents(true);

    }

    /**
     * Läd alle benötigten Texturen.
     *
     * @throws IOException Wenn was schief geht
     */
    private void loadTex() throws IOException {
        groundTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/ground.png"), GL_NEAREST);
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    private void render() {
        glDisable(GL_TEXTURE_2D);
        glColor3f(1f, 0, 1f);
        glRectf(0, 0, 1, 1);
        glColor3f(1f, 1f, 1f);
        glEnable(GL_TEXTURE_2D);
        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        float oneX = 1f / ground.length;
        float oneY = 1f / ground[0].length;
        for (int x = 0; x < ground.length; x++) {
            for (int y = 0; y < ground[0].length; y++) {
                int tex = texAt(ground, x, y);
                int tx = tex % 16;
                int ty = tex / 16;
                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(tx * 0.0625f, ty * 0.0625f); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
                glVertex3f((x + panX) * oneX * zoom, (y + 1 + panY) * oneY * zoom, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                // Die weiteren 3 Ecken im Uhrzeigersinn:
                glTexCoord2f(tx * 0.0625f + 0.05859375f, ty * 0.0625f);
                glVertex3f((x + 1 + panX) * oneX * zoom, (y + 1 + panY) * oneY * zoom, 0);
                glTexCoord2f(tx * 0.0625f + 0.05859375f, ty * 0.0625f + 0.05859375f);
                glVertex3f((x + 1 + panX) * oneX * zoom, (y + panY) * oneY * zoom, 0);
                glTexCoord2f(tx * 0.0625f, ty * 0.0625f + 0.05859375f);
                glVertex3f((x + panX) * oneX * zoom, (y + panY) * oneY * zoom, 0);
                glEnd(); // Zeichnen des QUADs fertig
            }
        }
        // Vis-Polys rendern, falls vorhanden:
        if (visPolys != null) {
            glColor4f(1f, 1f, 0, 0.3f);
            renderPolyMesh(visPolys, oneX, oneY, 0f, 0f, 1f, 1f);
            if (currentPoly != null && currentPoly.getMesh() != null) {
                renderPolyMesh(currentPoly.getMesh(), oneX, oneY, 1f, 0f, 0f, 1f);
            }

        }
    }

    private void renderPolyMesh(PolyMesh mesh, float oneX, float oneY, float lineR, float lineG, float lineB, float lineA) {
        glDisable(GL_TEXTURE_2D);


        // Polygone einfärben
        for (MPolygon poly : mesh.polys) {
            if (poly.border || (poly.solid && poly.resource != 0)) {
                if (poly.border) {
                    glColor4f(1f, 1f, 0, 0.3f);
                } else {
                    glColor4f(1f, 0f, 1f, 0.3f);
                }
                glBegin(GL_POLYGON);
                for (Node n : poly.getNodes()) {
                    glVertex2d((n.x + (panX * oneX)) * zoom, (n.y + (panY * oneY)) * zoom);
                }
                glEnd();
            }
        }

        // Linien malen

        glColor4f(lineR, lineG, lineB, lineA);
        glLineWidth(1);
        for (MPolygon poly : mesh.polys) {
            Node previous = poly.getNodes().get(0);
            for (int i = 1; i < poly.getNodes().size(); i++) {
                Node next = poly.getNodes().get(i);
                glBegin(GL_LINES);
                glVertex2d((previous.x + (panX * oneX)) * zoom, (previous.y + (panY * oneY)) * zoom);
                glVertex2d((next.x + (panX * oneX)) * zoom, (next.y + (panY * oneY)) * zoom);
                glEnd();
                previous = next;
            }
            // Zumachen
            glBegin(GL_LINES);
            glVertex2d((previous.x + (panX * oneX)) * zoom, (previous.y + (panY * oneY)) * zoom);
            glVertex2d((poly.getNodes().get(0).x + (panX * oneX)) * zoom, (poly.getNodes().get(0).y + (panY * oneY)) * zoom);
            glEnd();
        }
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
    }

    private int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 0;
        } else {
            return layer[x][y];
        }
    }

    public static void main(String[] args) throws IOException {
        MapParameters params = new MapParameters();
        System.out.println("Generated Map:");
        System.out.println(params.export());

        // Generate Map
        InternalMap map = MapGen.generateInternal(params);
        new MapViewer(map);

        System.out.println(Mouse.getEventButton());
    }

    /**
     * Stellt die Sicht auf diese Werte ein
     *
     * @param x x in 0..1
     * @param y y in 0..1
     * @param sizex xsize in 0..1
     * @param sizey ysize in 0..1
     */
    private void zoomPanTo(float x, float y, float sizex, float sizey) {
        zoom = Math.min(1 / sizex, 1 / sizey);
        panX = -x * ground.length;
        panY = -y * ground[0].length;
    }
}
