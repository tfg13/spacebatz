package de._13ducks.spacebatz.util.mapview;

import de._13ducks.spacebatz.server.levelgenerator.LevelGenerator;
import de._13ducks.spacebatz.shared.Level;
import java.io.IOException;
import java.lang.reflect.Field;
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

    private final Level level;
    boolean renderFrame = false;
    boolean dragging = false;
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
        this.level = level;
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
                        if ((Mouse.getEventButton() == -1 && dragging) || (Mouse.getEventButton() == 0 && Mouse.getEventButtonState())) {
                            if (!dragging) {
                                Mouse.setGrabbed(true);
                                dragging = true;
                            }
                            panX += Mouse.getDX() / zoom;
                            panY += Mouse.getDY() / zoom;
                            repaint = true;
                        } else {
                            if (dragging) {
                                Mouse.setGrabbed(false);
                                dragging = false;
                            }
                            zoom += Mouse.getDWheel() / 20f;
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
        glColor3f(1f,1f,1f);
        glEnable(GL_TEXTURE_2D);
        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        float oneX = 1f / level.getSizeX();
        float oneY = 1f / level.getSizeY();
        for (int x = 0; x < level.getSizeX(); x++) {
            for (int y = 0; y < level.getSizeY(); y++) {
                int tex = texAt(level.getGround(), x, y);
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
    }

    private int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 0;
        } else {
            return layer[x][y];
        }
    }

    public static void main(String[] args) {
        Level level = LevelGenerator.generateLevel();
        new MapViewer(level);
    }
}
