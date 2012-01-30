package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.*;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
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
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println("[ERROR]: Failed to set library lookup path! Details:");
            ex.printStackTrace();
        }
    }
    /**
     * Tilemaps
     */
    private Texture groundTiles;
    private Texture playerTiles;
    private Texture enemyTiles;
    private Texture bulletTiles;
    private Texture itemTiles;
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
            // Gametick updaten:
            Client.updateGametick();
            // UDP-Input verarbeiten:
            Client.udpTick();
            // TCP-Input verarbeiten:
            Client.getMsgInterpreter().interpretAllTcpMessages();
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
        byte[] udp = new byte[NET_UDP_CTS_SIZE];
        udp[0] = (byte) Client.getClientID();
        Bits.putInt(udp, 1, Client.frozenGametick);
        udp[5] = NET_UDP_CMD_INPUT;
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            udp[6] |= 0x20;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            udp[6] |= 0x80;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            udp[6] |= 0x40;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            udp[6] |= 0x10;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            byte[] udp2 = new byte[NET_UDP_CTS_SIZE];
            udp2[0] = (byte) Client.getClientID();
            Bits.putInt(udp2, 1, Client.frozenGametick);
            udp2[5] = NET_UDP_CMD_REQUEST_BULLET;
            double dx = Mouse.getX() - Display.getWidth() / 2;
            double dy = Mouse.getY() - Display.getHeight() / 2;
            double dir = Math.atan2(dy, dx);
            if (dir < 0) {
                dir += 2 * Math.PI;
            }
            Bits.putFloat(udp2, 6, (float) (dir));
            Client.udpOut(udp2);
        }
        Client.udpOut(udp);
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
        long tick = Client.frozenGametick;
        //Client.incrementGametick();

        panX = (float) -Client.getPlayer().getX() + tilesX / 2.0f;
        panY = (float) -Client.getPlayer().getY() + tilesY / 2.0f;

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

        // Items
        itemTiles.bind();
        for (int i = 0; i < Client.getItemList().size(); i++) {
            Item item = Client.getItemList().get(i); // Zu alte Bullets löschen:

                float x = (float) item.getPosX();
                float y = (float) item.getPosY();

                float v = 0.0f;
                float w = 0.0f;
                
                v = 0.25f * (int) item.stats.itemStats.get("pic");

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(v, w + 0.25f);
                glVertex3f(x + panX - 0.75f, y + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w + 0.25f);
                glVertex3f(x + panX + 0.75f, y + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w);
                glVertex3f(x + panX + 0.75f, y + panY + 0.75f, 0.0f);
                glTexCoord2f(v, w);
                glVertex3f(x + panX - 0.75f, y + panY + 0.75f, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }

            
        }

        // Gegner:
        enemyTiles.bind();
        for (Char c : Client.netIDMap.values()) {
            if (c instanceof Enemy) {
                Enemy enemy = (Enemy) c;
                int dir = c.getDir();
                int tilex;
                // Bei Gegnertyp 0 andere Tiles benutzen
                if (enemy.getEnemytypeid() == 0) {
                    tilex = dir + 8;
                } else {
                    tilex = dir;
                }
                glBegin(GL_QUADS);
                glTexCoord2f(0.0625f * tilex, 0);
                glVertex3f((float) c.getX() + panX - 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + tilex), 0);
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + tilex), 0.0625f * 2);
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY - 1, 0);
                glTexCoord2f(0.0625f * tilex, 0.0625f * 2);
                glVertex3f((float) c.getX() + panX - 1, (float) c.getY() + panY - 1, 0);
                glEnd();
            }
        }

        // Players:
        playerTiles.bind();
        for (Char c : Client.netIDMap.values()) {
            if (c instanceof Player) {
                int dir = c.getDir();
                glBegin(GL_QUADS);
                glTexCoord2f(0.0625f * dir, 0);
                glVertex3f((float) c.getX() + panX - 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + dir), 0);
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + dir), 0.0625f * 2);
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY - 1, 0);
                glTexCoord2f(0.0625f * dir, 0.0625f * 2);
                glVertex3f((float) c.getX() + panX - 1, (float) c.getY() + panY - 1, 0);
                glEnd();
            }
        }

        // Bullets
        bulletTiles.bind();
        for (int i = 0; i < Client.getBulletList().size(); i++) {
            Bullet bullet = Client.getBulletList().get(i); // Zu alte Bullets löschen:
            if (bullet.getDeletetick() < Client.frozenGametick) {

                Client.getBulletList().remove(i);
                i--;
            } else {

                float radius = bullet.getSpeed() * (Client.frozenGametick - bullet.getSpawntick());
                float x = (float) bullet.getSpawnposition().getX() + radius * (float) Math.cos(bullet.getDirection());
                float y = (float) bullet.getSpawnposition().getY() + radius * (float) Math.sin(bullet.getDirection());

                float v = Client.bullettypes.getBullettypelist().get(bullet.getBullettypeID()).getPicture() * 0.25f;
                float w = 0.0f;

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(v, w + 0.25f);
                glVertex3f(x + panX - 0.75f, y + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w + 0.25f);
                glVertex3f(x + panX + 0.75f, y + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w);
                glVertex3f(x + panX + 0.75f, y + panY + 0.75f, 0.0f);
                glTexCoord2f(v, w);
                glVertex3f(x + panX - 0.75f, y + panY + 0.75f, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }

            }
        }
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
        enemyTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/ringbot.png"), GL_NEAREST);
        bulletTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/bullet.png"), GL_NEAREST);
        itemTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/item.png"), GL_NEAREST);
    }
}
