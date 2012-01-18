package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
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
     * Tilemaps
     */
    private Texture groundTiles;
    private Texture playerTiles;
    private Texture bulletTiles;
    /**
     * Die Anzahl der Tiles auf dem Bildschirm.
     */
    private int tilesX, tilesY;
    /**
     * Die aktuelle FPS-Zahl.
     */
    private int fps;
    /**
     * Zeitpunkt der letzten FPS-Messung.
     */
    private long lastFPS;
    /**
     * Scrollen des Bildschirms, in Feldern.
     */
    private float panX, panY;

    public Engine() {
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
    }

    /**
     * Startet die Grafik. Verwendet den gegebenen Thread (forkt *nicht* selbstständig!).
     */
    public void start() {
        // Fenster aufmachen:
        try {
            Display.setDisplayMode(new DisplayMode(CLIENT_GFX_RES_X, CLIENT_GFX_RES_Y));
            Display.create();
            Display.setVSyncEnabled(CLIENT_GFX_VSYNC);

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

        lastFPS = getTime();
        // Render-Mainloop:
        while (!Display.isCloseRequested()) {
            // UDP-Input verarbeiten:
            Client.udpTick();
            // Render-Code
            render();

            // Fertig, Puffer swappen:
            Display.update();
            // Frames messen:
            updateFPS();
            // Input verarbeiten:
            directInput();
            // Frames limitieren:
            Display.sync(CLIENT_GFX_FRAMELIMIT);
        }

        // Ende der Mainloop.
        Display.destroy();

    }

    /**
     * Verarbeitet den Input, der UDP-Relevant ist.
     */
    private void directInput() {
        byte[] udp = new byte[7];
        udp[0] = (byte) Client.getClientID();
        Bits.putInt(udp, 1, Client.gametick);
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            udp[5] |= 0x20;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            udp[5] |= 0x80;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            udp[5] |= 0x40;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            udp[5] |= 0x10;
        }
        if (udp[5] != 0) {
            Client.udpOut(udp);
        }
    }

    /**
     * Liefert eine wirklich aktuelle Zeit. Nicht so gammlig wie System.currentTimeMillis();
     *
     * @return eine wirklich aktuelle Zeit.
     */
    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Aktualisiert die FPS-Daten. Muss bei jedem Frame aufgerufen werden.
     */
    private void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    /**
     * OpenGL initialisieren
     */
    private void initGL() {
        // Orthogonalperspektive mit korrekter Anzahl an Tiles initialisieren.
        GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        glEnable(GL_TEXTURE_2D); // Aktiviert Textur-Mapping
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); // Zeichenmodus auf Überschreiben stellen
        glEnable(GL_BLEND); // Transparenz in Texturen erlauben
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    private void render() {
        //Client.incrementGametick();
        panX = (float) -Client.getPlayer().getX() + tilesX / 2;
        panY = (float) -Client.getPlayer().getY() + tilesY / 2;

        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int tex = texAt(Client.currentLevel.getGround(), x, y);
                int tx = tex % 16;
                int ty = tex / 16;
                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(tx * 0.0625f, ty * 0.0625f); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
                glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                // Die weiteren 3 Ecken im Uhrzeigersinn:
                glTexCoord2f((tx + 1) * 0.0625f, ty * 0.0625f);
                glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                glTexCoord2f((tx + 1) * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + 1 + panX, y + panY, 0);
                glTexCoord2f(tx * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + panX, y + panY, 0);
                glEnd(); // Zeichnen des QUADs fertig
            }
        }
        // Player in die Mitte:
        Player p = Client.getPlayer();
        int dir = p.getDir();
        playerTiles.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0.0625f * dir, 0);
        glVertex3f(tilesX / 2, tilesY / 2 + 2, 0);
        glTexCoord2f(0.0625f * (2 + dir), 0);
        glVertex3f(tilesX / 2 + 2, tilesY / 2 + 2, 0);
        glTexCoord2f(0.0625f * (2 + dir), 0.0625f * 2);
        glVertex3f(tilesX / 2 + 2, tilesY / 2, 0);
        glTexCoord2f(0.0625f * dir, 0.0625f * 2);
        glVertex3f(tilesX / 2, tilesY / 2, 0);
        glEnd();

        // Bullets
        /*
         * bulletTiles.bind(); for (int i = 0; i < Client.getBulletList().size(); i++) { Bullet bullet = Client.getBulletList().get(i); // Zu alte Bullets
         * löschen: if (bullet.getDeletetick() < Client.gametick) {
         *
         * Client.getBulletList().remove(i); i--; } else {
         *
         * float radius = (float) bullet.getSpeed() * (Client.gametick - bullet.getSpawntick()); float x = (float) bullet.getSpawnposition().getX() + radius *
         * (float) Math.cos(bullet.getDirection()); float y = (float) bullet.getSpawnposition().getY() + radius * (float) Math.sin(bullet.getDirection());
         *
         * float v = 0.75f; float w = 0.0f;
         *
         * glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren glTexCoord2f(v, w + 0.25f); glVertex3f(x + panX, y + panY, 0); glTexCoord2f(v + 0.25f, w + 0.25f);
         * glVertex3f(x + 2 + panX, y + panY, 0); glTexCoord2f(v + 0.25f, w); glVertex3f(x + 2 + panX, y + 2 + panY, 0); glTexCoord2f(v, w); glVertex3f(x +
         * panX, y + 2 + panY, 0); glEnd(); // Zeichnen des QUADs fertig } }
         */
    }

    private int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer.length) {
            return 0;
        } else {
            return layer[x][y];
        }
    }

    /**
     * Läd alle benötigten Texturen
     */
    private void loadTex() throws IOException {
        // Der letzte Parameter sagt OpenGL, dass es Pixel beim vergrößern/verkleinern nicht aus mittelwerten von mehreren berechnen soll,
        // sondern einfach den nächstbesten nehmen. Das sort für den Indie-Pixelart-Look
        groundTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/ground.png"), GL_NEAREST);
        playerTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/player.png"), GL_NEAREST);
        bulletTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/bullet.png"), GL_NEAREST);
    }
}
