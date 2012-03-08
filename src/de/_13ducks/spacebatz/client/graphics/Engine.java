package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.*;
import de._13ducks.spacebatz.client.network.NetStats;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Iterator;
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
     * Tilemaps.
     */
    private Texture groundTiles;
    private Texture playerTiles;
    private Texture enemyTiles;
    private Texture bulletTiles;
    private Texture itemTiles;
    private Texture inventoryPic;
    private Texture font;
    /**
     * Charset, zum Textoutput-Encoding
     */
    private Charset charset;
    /**
     * Wieviel Platz die einzelnen Buchstaben brauchen.
     */
    private byte[] spaceing;
    /**
     * Die Anzahl der Tiles auf dem Bildschirm.
     */
    private int tilesX, tilesY;
    /**
     * Der fps-Counter.
     */
    private int fpsCount;
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
    /**
     * Sagt, ob Inventar gerade geöffnet ist (Taste i)
     */
    private boolean showinventory; // wird Inventar gerade gerendert
    private boolean lmbpressed; // linke maustaste gedrückt
    private int inventorypage; // aktuelle Inventarseite
    private int selecteditemslot; // zuletzt angeklickter Inventarslot

    public Engine() {
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        charset = Charset.forName("cp437");
        selecteditemslot = -1;
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
        // Daten laden
        try {
            loadTex();
            loadBin();
        } catch (IOException ex) {
            ex.printStackTrace();
            Display.destroy();
            return;
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
        // Netzwerk abmelden:
        Client.getMsgSender().sendDisconnect();

        // Ende der Mainloop.
        Display.destroy();

    }

    /**
     * Verarbeitet den Input, der UDP-Relevant ist.
     */
    private void directInput() {
        byte[] udp = new byte[NET_UDP_CTS_SIZE];
        udp[0] = Client.getClientID();
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
            sendShootRequest();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            if (Client.getPlayer().getSelectedattack() != 0) {
                Client.getMsgSender().sendSwitchWeapon((byte) 0);
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            if (Client.getPlayer().getSelectedattack() != 1) {
                Client.getMsgSender().sendSwitchWeapon((byte) 1);
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            if (Client.getPlayer().getSelectedattack() != 2) {
                Client.getMsgSender().sendSwitchWeapon((byte) 2);
            }
        }
        // Mausklick suchen
        if (Mouse.isButtonDown(0)) {
            if (showinventory) {
                if (!lmbpressed) {
                    lmbpressed = true;
                    float x = (float) Mouse.getX() / CLIENT_GFX_RES_X;
                    float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y;
                    //System.out.println("x " + x + ",y " + y);

                    // Equipslot angeklickt?

                    if (y > 0.8 && y < 0.92) {
                        if (x > 0.4 && x < 0.54) {
                            // Hut-Slot
                            if (selecteditemslot != -1) {
                                Item selecteditem = Client.getInventorySlots()[selecteditemslot].getItem();
                                if ((int) selecteditem.getStats().itemStats.get("itemclass") == 2) {
                                    Client.getMsgSender().sendEquipItem(selecteditem, (byte) 0); // 2 = Hut-Slot
                                    selecteditemslot = -1;
                                }
                            } else {
                                if (Client.getEquippedItems().getEquipslots()[2][0] != null) {
                                    Client.getMsgSender().sendDequipItem(2, (byte) 0); // 2 = Hut-Slot
                                }
                            }
                        }
                    }
                    if (y > 0.61 && y < 0.74) {
                        byte weaponslot = -1;
                        if (x > 0.22 && x < 0.36) {
                            weaponslot = 0;
                        } else if (x > 0.4 && x < 0.54) {
                            weaponslot = 1;
                        } else if (x > 0.58 && x < 0.72) {
                            weaponslot = 2;
                        }
                        if (weaponslot != -1) {
                            // Waffenslot
                            if (selecteditemslot != -1) {
                                Item selecteditem = Client.getInventorySlots()[selecteditemslot].getItem();
                                if ((int) selecteditem.getStats().itemStats.get("itemclass") == 1) {
                                    Client.getMsgSender().sendEquipItem(selecteditem, weaponslot); // Slotnummer, zum Auseinanderhalten von den 3 Waffenslots
                                    selecteditemslot = -1;
                                }
                            } else {
                                if (Client.getEquippedItems().getEquipslots()[1][weaponslot] != null) {
                                    Client.getMsgSender().sendDequipItem(1, weaponslot); // 1 = Waffen-Slot
                                }
                            }
                        }
                    }

                    // Inventarslot angeklickt?
                    int slotklicked = -1;
                    if (y > 0.1812 && y <= 0.3156) {
                        for (int i = 0; i < 6; i++) {
                            if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                                slotklicked = i + inventorypage * 12;
                                break;
                            }
                        }
                    } else if (y > 0.05156 && y <= 0.1813) {
                        for (int i = 0; i < 6; i++) {
                            if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                                slotklicked = i + 6 + inventorypage * 12;
                                break;
                            }
                        }
                    }

                    if (slotklicked != -1) {
                        // gültiger Inventar-Slot angeklickt

                        if (selecteditemslot == -1) {
                            // zur Zeit war kein Slot ausgewählt -> der hier wird
                            if (Client.getInventorySlots()[slotklicked] != null) {
                                // nur wenn hier ein item drin ist
                                selecteditemslot = slotklicked;
                            }

                        } else {
                            // es war bereits ein Slot ausgewählt
                            if (Client.getInventorySlots()[slotklicked] == null) {
                                // angeklickter Slot leer -> Item verschieben
                                Client.getInventorySlots()[slotklicked] = Client.getInventorySlots()[selecteditemslot];
                                Client.getInventorySlots()[selecteditemslot] = null;
                                selecteditemslot = -1;
                            } else {
                                // angeklickter Slot belegt -> Items tauschen
                                InventorySlot swapSlot = Client.getInventorySlots()[slotklicked];
                                Client.getInventorySlots()[slotklicked] = Client.getInventorySlots()[selecteditemslot];
                                Client.getInventorySlots()[selecteditemslot] = swapSlot;
                                selecteditemslot = -1;
                            }
                        }
                    }

                    // nächste / vorherige Seite
                    if (y > 0.14 && y < 0.22) {
                        if (x < 0.1) {
                            if (inventorypage <= 0) {
                                inventorypage = 0;
                            } else {
                                inventorypage--;
                            }
                        } else if (x > 0.9) {
                            if (inventorypage >= 7) {
                                inventorypage = 7;
                            } else {
                                inventorypage++;
                            }
                        }
                    }
                }
            } else {
                sendShootRequest();
            }
        } else {
            lmbpressed = false;
        }

        Client.udpOut(udp);
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_I) {
                if (Keyboard.getEventKeyState()) {
                    showinventory = !showinventory;
                    inventorypage = 0;
                }
            } else if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                if (Keyboard.getEventKeyState()) {
                    showinventory = false;
                }
            }
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
            fps = fpsCount;
            fpsCount = 0;
            lastFPS += 1000;
        }
        fpsCount++;
    }

    /**
     * OpenGL initialisieren
     */
    private void initGL() {
        // Orthogonalperspektive mit korrekter Anzahl an Tiles initialisieren.
        GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        glEnable(GL_TEXTURE_2D); // Aktiviert Textur-Mapping
        //glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE); // Zeichenmodus auf überschreiben stellen
        glEnable(GL_BLEND); // Transparenz in Texturen erlauben
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Transparenzmodus
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    private void render() {

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

        // Items auf der Map
        itemTiles.bind();
        Iterator<Item> iterator = Client.getItemMap().values().iterator();

        while (iterator.hasNext()) {
            Item item = iterator.next();

            float x = (float) item.getPosX();
            float y = (float) item.getPosY();

            float v = 0.25f * (int) item.getStats().itemStats.get("pic");
            float w = 0.25f * ((int) item.getStats().itemStats.get("pic") / 4);

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
                int tilex = dir;
                int tiley = 0;
                // Bei Gegnertyp 0 andere Tiles benutzen
                if (enemy.getEnemytypeid() == 0) {
                    tilex = dir + 8;
                }
                if (enemy.getEnemytypeid() == 2) {
                    tiley = 2;
                }
                glBegin(GL_QUADS);
                glTexCoord2f(0.0625f * tilex, 0.0625f * tiley);
                glVertex3f((float) c.getX() + panX - 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + tilex), 0.0625f * tiley);
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY + 1, 0);
                glTexCoord2f(0.0625f * (2 + tilex), 0.0625f * (tiley + 2));
                glVertex3f((float) c.getX() + panX + 1, (float) c.getY() + panY - 1, 0);
                glTexCoord2f(0.0625f * tilex, 0.0625f * (tiley + 2));
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
        for (Char c : Client.netIDMap.values()) {
            if (c instanceof Bullet) {
                Bullet bullet = (Bullet) c;

                float v = bullet.bulletpic * 0.25f;
                float w = bullet.bulletpic / 4 * 0.25f;

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(v, w + 0.25f);
                glVertex3f((float) c.getX() + panX - 0.75f, (float) c.getY() + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w + 0.25f);
                glVertex3f((float) c.getX() + panX + 0.75f, (float) c.getY() + panY - 0.75f, 0.0f);
                glTexCoord2f(v + 0.25f, w);
                glVertex3f((float) c.getX() + panX + 0.75f, (float) c.getY() + panY + 0.75f, 0.0f);
                glTexCoord2f(v, w);
                glVertex3f((float) c.getX() + panX - 0.75f, (float) c.getY() + panY + 0.75f, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }
            }
        }

        // Lebensenergie-Balken
        int maxhp = Math.max(1, Client.getPlayer().getHealthpointsmax());
        int hp = Math.min(Client.getPlayer().getHealthpoints(), maxhp);
        glDisable(GL_TEXTURE_2D);
        // schwarzer Hintergrund
        glColor3f(0.0f, 0.0f, 0.0f);
        glRectf(0.02f * tilesX, 0.02f * tilesY, 0.3f * tilesX, 0.06f * tilesY);
        // roter HP-Balken
        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(0.03f * tilesX, 0.03f * tilesY, (0.03f + 0.26f * ((float) hp / maxhp)) * tilesX, 0.05f * tilesY);
        glEnable(GL_TEXTURE_2D);
	glColor3f(1f, 1f, 1f);

        // Inventory-Hintergrund
        inventoryPic.bind();
        if (showinventory) {
            //System.out.println("inventory");

            glBegin(GL_QUADS);
            glTexCoord2f(0, 1);
            glVertex3f(0, 0, 0);
            glTexCoord2f(1, 1);
            glVertex3f(tilesX, 0, 0);
            glTexCoord2f(1, 0);
            glVertex3f(tilesX, tilesY, 0);
            glTexCoord2f(0, 0);
            glVertex3f(0, tilesY, 0);
            glEnd();
        }

        // Items im Inventory
        if (showinventory) {
            renderText(String.valueOf(Client.getMoney()), 0.12f * tilesX, 0.44f * tilesY);
            for (int i = 12 * inventorypage; i < 12 * inventorypage + 12; i++) {

                if (Client.getInventorySlots()[i] == null || i == selecteditemslot) {
                    // Slot leer oder gerade selected -> nicht zeichnen
                    continue;
                }
                itemTiles.bind();

                Item item = Client.getInventorySlots()[i].getItem();

                float x = (0.1075f + 0.133f * (i % 6)) * tilesX;

                float y;
                if (i % 12 < 6) {
                    y = 0.191f * tilesY;
                } else {
                    y = 0.061f * tilesY;
                }

                float width = 0.11f * tilesX;
                float height = 0.11f * tilesY;

                float v = 0.25f * (int) item.getStats().itemStats.get("pic");
                float w = 0.25f * ((int) item.getStats().itemStats.get("pic") / 4);

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(v, w + 0.25f);
                glVertex3f(x, y, 0.0f);
                glTexCoord2f(v + 0.25f, w + 0.25f);
                glVertex3f(x + width, y, 0.0f);
                glTexCoord2f(v + 0.25f, w);
                glVertex3f(x + width, y + height, 0.0f);
                glTexCoord2f(v, w);
                glVertex3f(x, y + height, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }
            }
        }

        // ausgewählten Waffenslot markieren:
        if (showinventory) {
            glDisable(GL_TEXTURE_2D);
            float wx = 0.227f + 0.172f * Client.getPlayer().getSelectedattack();

            glColor3f(0.7f, 0.0f, 0.0f);
            glRectf(wx * tilesX, 0.59f * tilesY, (wx + 0.14f) * tilesX, 0.6f * tilesY);
	    glColor3f(1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
        }

        // angelegte Items in ihre Slots im Inventar zeichnen
        if (showinventory) {
            itemTiles.bind();
            for (int i = 1; i <= 2; i++) {
                for (int j = 0; j < Client.getEquippedItems().getEquipslots()[i].length; j++) {
                    Item item = Client.getEquippedItems().getEquipslots()[i][j];
                    if (item != null) {
                        // Item zeichnen;
                        float x;
                        if (i == 1) {
                            x = (0.24f + 0.17f * j) * tilesX;
                        } else {
                            x = 0.41f * tilesX;
                        }
                        float y = (0.61f + 0.2f * (i - 1)) * tilesY;

                        float width = 0.11f * tilesX;
                        float height = 0.11f * tilesY;

                        float v = 0.25f * (int) item.getStats().itemStats.get("pic");
                        float w = 0.25f * ((int) item.getStats().itemStats.get("pic") / 4);

                        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        glTexCoord2f(v, w + 0.25f);
                        glVertex3f(x, y, 0.0f);
                        glTexCoord2f(v + 0.25f, w + 0.25f);
                        glVertex3f(x + width, y, 0.0f);
                        glTexCoord2f(v + 0.25f, w);
                        glVertex3f(x + width, y + height, 0.0f);
                        glTexCoord2f(v, w);
                        glVertex3f(x, y + height, 0.0f);
                        glEnd(); // Zeichnen des QUADs fertig } }
                    }
                }
            }
        }

        // selected Item zum Mauszeiger zeichnen
        if (selecteditemslot != -1) {
            itemTiles.bind();
            Item item = Client.getInventorySlots()[selecteditemslot].getItem();
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X * tilesX;
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y * tilesY;

            float size = 0.08f;

            float v = 0.25f * (int) item.getStats().itemStats.get("pic");
            float w = 0.25f * ((int) item.getStats().itemStats.get("pic") / 4);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.25f);
            glVertex3f(x - tilesX * size / 2, y - tilesX * size / 2, 0.0f);
            glTexCoord2f(v + 0.25f, w + 0.25f);
            glVertex3f(x + tilesX * size / 2, y - tilesX * size / 2, 0.0f);
            glTexCoord2f(v + 0.25f, w);
            glVertex3f(x + tilesX * size / 2, y + tilesX * size / 2, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x - tilesX * size / 2, y + tilesX * size / 2, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Mousehover über Item?
        if (showinventory) {
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X;
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y;

            // Maus über Item im Inventar?
            int slothovered = -1;
            if (y > 0.1812 && y <= 0.3156) {
                for (int i = 0; i < 6; i++) {
                    if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                        slothovered = i + inventorypage * 12;
                        break;
                    }
                }
            } else if (y > 0.05156 && y <= 0.1813) {
                for (int i = 0; i < 6; i++) {
                    if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                        slothovered = i + 6 + inventorypage * 12;
                        break;
                    }
                }
            }
            Item item = null;
            if (slothovered != -1 && slothovered != selecteditemslot) {
                if (Client.getInventorySlots()[slothovered] != null) {
                    item = Client.getInventorySlots()[slothovered].getItem();
                }
                // Einer der Ausrüstungsslots?
            } else if (x > 0.4 && x < 0.54) {
                if (y > 0.8 && y < 0.92) {
                    item = Client.getEquippedItems().getEquipslots()[2][0];
                } else if (y > 0.61 && y < 0.74) {
                    item = Client.getEquippedItems().getEquipslots()[1][1];
                }
            } else if (y > 0.8 && y < 0.92) {
                if (x > 0.4 && x < 0.54) {
                    // Hutslot
                    item = Client.getEquippedItems().getEquipslots()[2][0];
                }
            } else if (y > 0.61 && y < 0.74) {
                // ein Waffenslot?
                if (x > 0.22 && x < 0.36) {
                    item = Client.getEquippedItems().getEquipslots()[1][0];
                } else if (x > 0.4 && x < 0.54) {
                    item = Client.getEquippedItems().getEquipslots()[1][1];
                } else if (x > 0.58 && x < 0.72) {
                    item = Client.getEquippedItems().getEquipslots()[1][2];
                }
            }

            if (item != null) {
                // Mousehovern rendern, zuerst Rechteck
                glDisable(GL_TEXTURE_2D);
                glColor3f(0.9f, 0.9f, 0.9f);
                glRectf((x - 0.01f) * tilesX, (y - 0.01f) * tilesY, (x + 0.3f) * tilesX, (y + 0.05f * (0.7f + item.getItemattributes().size())) * tilesY);
		glColor3f(1f, 1f, 1f);
                glEnable(GL_TEXTURE_2D);
                // Namen der Itemattribute
                for (int i = 0; i < item.getItemattributes().size(); i++) {
                    renderText(String.valueOf(item.getItemattributes().get(i).getName()), x * tilesX, y * tilesY);
                    y += 0.05f;
                }
                // Itemname
                renderText((String) item.getStats().itemStats.get("name"), x * tilesX, y * tilesY);
            }
        }

        // Net-Graph?
        if (NetStats.netGraph) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(.9f, .9f, .9f, .7f);
            glRectf(0, tilesY, 10, tilesY - 1.5f);
	    glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            renderText("delay: spec " + (NET_TICKSYNC_MAXPING / Client.tickrate) + " real " + NetStats.getLastTickDelay() + " avg " + NetStats.getAvgTickDelay(), 0, tilesY - .5f);
            renderText("netIn/tick: number " + NetStats.getAndResetInCounter() + " bytes " + NetStats.getAndResetInBytes(), 0, tilesY - 1);
            renderText("fps: " + fps + " ping: " + NetStats.ping, 0, tilesY - 1.5f);
        }
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     */
    private void renderText(String text, float x, float y) {
        float next = 0;
        byte[] chars = text.getBytes(charset);
        font.bind();
        for (int i = 0; i < chars.length; i++) {
            byte c = chars[i];
            int tileX = c % 16;
            int tileY = c / 16;
            float tx = tileX / 16f;
            float ty = tileY / 16f;
            glBegin(GL_QUADS);
            glTexCoord2f(tx, ty);
            glVertex3f(x + next, y + .5f, 0.0f);
            glTexCoord2f(tx + .0625f, ty);
            glVertex3f(x + next + .5f, y + .5f, 0.0f);
            glTexCoord2f(tx + .0625f, ty + .0625f);
            glVertex3f(x + next + .5f, y, 0.0f);
            glTexCoord2f(tx, ty + .0635f);
            glVertex3f(x + next, y, 0.0f);
            glEnd();
            // Spacing dieses chars weiter gehen:
            next += spaceing[c] / 16f;
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
     * Läd alle benötigten Texturen.
     *
     * @throws IOException Wenn was schief geht
     */
    private void loadTex() throws IOException {
        // Der letzte Parameter sagt OpenGL, dass es Pixel beim vergrößern/verkleinern nicht aus Mittelwerten von mehreren berechnen soll,
        // sondern einfach den nächstbesten nehmen. Das sort für den Indie-Pixelart-Look
        groundTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/ground.png"), GL_NEAREST);
        playerTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/player.png"), GL_NEAREST);
        enemyTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/enemy.png"), GL_NEAREST);
        bulletTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/bullet.png"), GL_NEAREST);
        itemTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/item.png"), GL_NEAREST);
        inventoryPic = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/inventory2.png"), GL_NEAREST);
        font = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/font.png"), GL_NEAREST);
    }

    /**
     * Läd alle benötigten Binärdateien, die keine Bilder sind.
     *
     * @throws IOException Wenn was schief geht
     */
    private void loadBin() throws IOException {
        spaceing = new byte[256];
        InputStream r = ResourceLoader.getResourceAsStream("tex/font_spacing.bin");
        int b;
        int i = 0;
        while ((b = r.read()) != -1) {
            spaceing[i++] = (byte) b;
        }
    }

    /**
     * Sagt dem Server, das geschossen werden soll
     */
    private void sendShootRequest() {
        byte[] udp2 = new byte[NET_UDP_CTS_SIZE];
        udp2[0] = Client.getClientID();
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
}
