/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.*;
import de._13ducks.spacebatz.client.network.CTS_DISCONNECT;
import de._13ducks.spacebatz.client.network.NetStats;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
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
    private Texture fxTiles;
    private Texture font[] = new Texture[2];
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
    /**
     * Ob das Terminal offen ist. Ein offenes Terminal verhindert jegliche andere Eingaben.
     */
    private boolean terminal = false;
    /**
     * Der aktuelle Zoomfaktor. Wird benötigt, um Schriften immer gleich groß anzeigen zu können
     */
    private int zoomFactor = 2;
    /**
     * Schadenszahlen über getroffenen Gegnern
     */
    private static LinkedList<DamageNumber> damageNumbers = new LinkedList<>();
    /**
     * FX-Effekte
     */
    private static LinkedList<Fx> fx = new LinkedList<>();
    /**
     * Konstante, die angibt wie lange Schadenszahlen sichtbar sind
     */
    private final int DAMAGENUMBER_LIFETIME = 1000;
    /**
     * Array mit allen Tilemaps
     */
    private Texture[] tilemaps;

    public Engine() {
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        charset = Charset.forName("cp437");
        tilemaps = new Texture[10];
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
            GameClient.updateGametick();
            // UDP-Input verarbeiten:
            GameClient.udpTick();
            // Input neues Netzwerksystem verarbeiten
            GameClient.getNetwork2().inTick();
            // Render-Code
            render();
            // Fertig, Puffer swappen:
            Display.update();
            // Frames messen:
            updateFPS();
            // Input verarbeiten:
            directInput();
            // Output neues Netzwerksystem:
            GameClient.getNetwork2().outTick();
            // Frames limitieren:
            Display.sync(CLIENT_GFX_FRAMELIMIT);
        }
        // Netzwerk abmelden:
        CTS_DISCONNECT.sendDisconnect();

        // Ende der Mainloop.
        Display.destroy();

    }

    /**
     * Verarbeitet den Input, der UDP-Relevant ist.
     */
    private void directInput() {
        byte[] udp = new byte[NET_UDP_CTS_SIZE];
        udp[0] = GameClient.getClientID();
        Bits.putInt(udp, 1, GameClient.frozenGametick);
        udp[5] = NET_UDP_CMD_INPUT;
        if (!terminal) {
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
                                    Item selecteditem = GameClient.getInventorySlots()[selecteditemslot].getItem();
                                    if ((int) selecteditem.getItemClass() == 2) {
                                        CTS_EQUIP_ITEM.sendEquipItem(selecteditem, (byte) 0); // 2 = Hut-Slot
                                        selecteditemslot = -1;
                                    }
                                } else {
                                    if (GameClient.getEquippedItems().getEquipslots()[2][0] != null) {
                                        CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(2, (byte) 0); // 2 = Hut-Slot
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
                                    Item selecteditem = GameClient.getInventorySlots()[selecteditemslot].getItem();
                                    if ((int) selecteditem.getItemClass() == 1) {
                                        CTS_EQUIP_ITEM.sendEquipItem(selecteditem, weaponslot); // Slotnummer, zum Auseinanderhalten von den 3 Waffenslots
                                        selecteditemslot = -1;
                                    }
                                } else {
                                    if (GameClient.getEquippedItems().getEquipslots()[1][weaponslot] != null) {
                                        CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(1, weaponslot); // 1 = Waffen-Slot
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
                                if (GameClient.getInventorySlots()[slotklicked] != null) {
                                    // nur wenn hier ein item drin ist
                                    selecteditemslot = slotklicked;
                                }

                            } else {
                                // es war bereits ein Slot ausgewählt
                                if (GameClient.getInventorySlots()[slotklicked] == null) {
                                    // angeklickter Slot leer -> Item verschieben
                                    GameClient.getInventorySlots()[slotklicked] = GameClient.getInventorySlots()[selecteditemslot];
                                    GameClient.getInventorySlots()[selecteditemslot] = null;
                                    selecteditemslot = -1;
                                } else {
                                    // angeklickter Slot belegt -> Items tauschen
                                    InventorySlot swapSlot = GameClient.getInventorySlots()[slotklicked];
                                    GameClient.getInventorySlots()[slotklicked] = GameClient.getInventorySlots()[selecteditemslot];
                                    GameClient.getInventorySlots()[selecteditemslot] = swapSlot;
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

            outer:
            while (Keyboard.next()) {
                int key = Keyboard.getEventKey();
                boolean pressed = Keyboard.getEventKeyState();
                if (pressed) {
                    switch (key) {
                        case Keyboard.KEY_F1:
                            terminal = true;
                            break outer;
                        case Keyboard.KEY_I:
                            showinventory = !showinventory;
                            inventorypage = 0;
                            break;
                        case Keyboard.KEY_ESCAPE:
                            showinventory = false;
                            break;
                        case Keyboard.KEY_S:
                        case Keyboard.KEY_W:
                        case Keyboard.KEY_A:
                        case Keyboard.KEY_D:
                        case Keyboard.KEY_SPACE:
                            // Ignorieren
                            break;
                        case Keyboard.KEY_1:
                            if (GameClient.getPlayer().getSelectedattack() != 0) {
                                CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 0);
                            }
                            break;
                        case Keyboard.KEY_2:
                            if (GameClient.getPlayer().getSelectedattack() != 1) {
                                CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 1);
                            }
                            break;
                        case Keyboard.KEY_3:
                            if (GameClient.getPlayer().getSelectedattack() != 2) {
                                CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 2);
                            }
                            break;
                    }
                }
            }
        } else {
            while (Keyboard.next()) {
                // Nur gedrückte Tasten
                if (Keyboard.getEventKeyState()) {
                    int key = Keyboard.getEventKey();
                    if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
                        GameClient.terminal.enter();
                    } else if (key == Keyboard.KEY_BACK) {
                        GameClient.terminal.backspace();
                    } else if (key == Keyboard.KEY_UP) {
                        GameClient.terminal.scrollBack();
                    } else if (key == Keyboard.KEY_DOWN) {
                        GameClient.terminal.scrollForward();
                    } else if (key == Keyboard.KEY_F1) {
                        terminal = false;
                        break;
                    } else {
                        char c = Keyboard.getEventCharacter();
                        if (c == ' ' || c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '>')) {
                            GameClient.terminal.input(c);
                        }
                    }
                }
            }
        }
        GameClient.udpOut(udp);
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
        Keyboard.enableRepeatEvents(true);
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    private void render() {

        panX = (float) -GameClient.getPlayer().getX() + tilesX / 2.0f;
        panY = (float) -GameClient.getPlayer().getY() + tilesY / 2.0f;

        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int tex = texAt(GameClient.currentLevel.getGround(), x, y);
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

        // Items auf der Map zeichnen
        itemTiles.bind();
        Iterator<Item> iterator = GameClient.getItemMap().values().iterator();

        while (iterator.hasNext()) {
            Item item = iterator.next();

            float x = (float) item.getPosX();
            float y = (float) item.getPosY();

            float v = 0.0625f * (int) item.getPic();
            float w = 0.0625f * ((int) item.getPic() / 16);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.0625f);
            glVertex3f(x + panX - 0.75f, y + panY - 0.75f, 0.0f);
            glTexCoord2f(v + 0.0625f, w + 0.0625f);
            glVertex3f(x + panX + 0.75f, y + panY - 0.75f, 0.0f);
            glTexCoord2f(v + 0.0625f, w);
            glVertex3f(x + panX + 0.75f, y + panY + 0.75f, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x + panX - 0.75f, y + panY + 0.75f, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Enemies zeichnen:
        enemyTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Enemy) {
                Enemy enemy = (Enemy) c;

                // Werte fürs Einfärben nehmen und rendern
                EnemyTypeStats ets = GameClient.enemytypes.getEnemytypelist().get(enemy.getEnemytypeid());
                glColor4f(ets.getColor_red(), ets.getColor_green(), ets.getColor_blue(), ets.getColor_alpha());
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
                glColor3f(1f, 1f, 1f);

            }
        }

        // Players zeichnen:
        playerTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof PlayerCharacter) {
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
            }
        }

        // Bullets zeichnen
        bulletTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Bullet) {
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
            }
        }

        // Fx-Effekte rendern
        fxTiles.bind();
        Iterator<Fx> itera = fx.iterator();
        while (itera.hasNext()) {
            Fx f = itera.next();
            if (GameClient.frozenGametick > f.getStarttick() + f.getLifetime()) {
                itera.remove();
            } else {
                renderAnim(f.getAnim(), f.getX(), f.getY(), 0.0, f.getStarttick());
            }
        }

        // Schadenszahlen zeichnen
        Iterator<DamageNumber> iter = damageNumbers.iterator();
        while (iter.hasNext()) {
            DamageNumber d = iter.next();
            if (getTime() > d.getSpawntime() + DAMAGENUMBER_LIFETIME) {
                // alt - > löschen
                iter.remove();
            } else {
                //rendern:
                float height = (getTime() - d.getSpawntime()) / 250.0f;
                float visibility = 1 - ((float) (getTime() - d.getSpawntime())) / DAMAGENUMBER_LIFETIME; // Anteil der vergangenen Zeit an der Gesamtlebensdauer
                visibility = Math.min(visibility * 2, 1); // bis 0.5 * lifetime: visibility 1, dann linear auf 0
                renderText(String.valueOf(d.getDamage()), (float) d.getX() + panX, (float) d.getY() + panY + height, 1f, .1f, .2f, visibility);
            }
        }

        // Lebensenergie-Balken im HUD zeichnen
        int maxhp = Math.max(1, GameClient.getPlayer().getHealthpointsmax());
        int hp = Math.min(GameClient.getPlayer().getHealthpoints(), maxhp);
        hp = Math.max(hp, 0);

        glDisable(GL_TEXTURE_2D);
        // schwarzer Hintergrund
        glColor3f(0.0f, 0.0f, 0.0f);
        glRectf(0.02f * tilesX, 0.02f * tilesY, 0.3f * tilesX, 0.06f * tilesY);
        // roter HP-Balken, Länge anhängig von HP
        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(0.03f * tilesX, 0.03f * tilesY, (0.03f + 0.26f * ((float) hp / maxhp)) * tilesX, 0.05f * tilesY);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);

        // Inventory-Hintergrund zeichnen
        inventoryPic.bind();
        if (showinventory) {
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

        // Items im Inventory zeichnen
        if (showinventory) {
            // Anzahl der Materialien:
            renderText(String.valueOf(GameClient.getMaterial(0)), 0.12f * tilesX, 0.44f * tilesY);
            renderText(String.valueOf(GameClient.getMaterial(1)), 0.45f * tilesX, 0.44f * tilesY);
            renderText(String.valueOf(GameClient.getMaterial(2)), 0.75f * tilesX, 0.44f * tilesY);

            for (int i = 12 * inventorypage; i < 12 * inventorypage + 12; i++) {

                if (GameClient.getInventorySlots()[i] == null || i == selecteditemslot) {
                    // Slot leer oder gerade selected -> nicht zeichnen
                    continue;
                }
                itemTiles.bind();

                Item item = GameClient.getInventorySlots()[i].getItem();

                float x = (0.1075f + 0.133f * (i % 6)) * tilesX;

                float y;
                if (i % 12 < 6) {
                    y = 0.191f * tilesY;
                } else {
                    y = 0.061f * tilesY;
                }

                float width = 0.11f * tilesX;
                float height = 0.11f * tilesY;

                float v = 0.0625f * (int) item.getPic();
                float w = 0.0625f * ((int) item.getPic() / 16);

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(v, w + 0.0625f);
                glVertex3f(x, y, 0.0f);
                glTexCoord2f(v + 0.0625f, w + 0.0625f);
                glVertex3f(x + width, y, 0.0f);
                glTexCoord2f(v + 0.0625f, w);
                glVertex3f(x + width, y + height, 0.0f);
                glTexCoord2f(v, w);
                glVertex3f(x, y + height, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }

                if (item.getAmount() > 1) {
                    // Amount hinschreiben
                    renderText(String.valueOf(item.getAmount()), x, y);
                }
            }
        }

        // ausgewählten Waffenslot im Inventar markieren:
        if (showinventory) {
            glDisable(GL_TEXTURE_2D);
            float wx = 0.227f + 0.172f * GameClient.getPlayer().getSelectedattack();

            glColor3f(0.7f, 0.0f, 0.0f);
            glRectf(wx * tilesX, 0.59f * tilesY, (wx + 0.14f) * tilesX, 0.6f * tilesY);
            glColor3f(1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
        }

        // angelegte Items in ihre Slots im Inventar zeichnen
        if (showinventory) {
            itemTiles.bind();
            for (int i = 1; i <= 2; i++) {
                for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[i].length; j++) {
                    Item item = GameClient.getEquippedItems().getEquipslots()[i][j];
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

                        float v = 0.0625f * (int) item.getPic();
                        float w = 0.0625f * ((int) item.getPic() / 16);

                        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        glTexCoord2f(v, w + 0.0625f);
                        glVertex3f(x, y, 0.0f);
                        glTexCoord2f(v + 0.0625f, w + 0.0625f);
                        glVertex3f(x + width, y, 0.0f);
                        glTexCoord2f(v + 0.0625f, w);
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
            Item item = GameClient.getInventorySlots()[selecteditemslot].getItem();
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X * tilesX;
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y * tilesY;

            float size = 0.08f;

            float v = 0.0625f * (int) item.getPic();
            float w = 0.0625f * ((int) item.getPic() / 16);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.0625f);
            glVertex3f(x - tilesX * size / 2, y - tilesX * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w + 0.0625f);
            glVertex3f(x + tilesX * size / 2, y - tilesX * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w);
            glVertex3f(x + tilesX * size / 2, y + tilesX * size / 2, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x - tilesX * size / 2, y + tilesX * size / 2, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Mousehover über Item zeichnen
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
                if (GameClient.getInventorySlots()[slothovered] != null) {
                    item = GameClient.getInventorySlots()[slothovered].getItem();
                }
                // Einer der Ausrüstungsslots?
            } else if (x > 0.4 && x < 0.54) {
                if (y > 0.8 && y < 0.92) {
                    item = GameClient.getEquippedItems().getEquipslots()[2][0];
                } else if (y > 0.61 && y < 0.74) {
                    item = GameClient.getEquippedItems().getEquipslots()[1][1];
                }
            } else if (y > 0.8 && y < 0.92) {
                if (x > 0.4 && x < 0.54) {
                    // Hutslot
                    item = GameClient.getEquippedItems().getEquipslots()[2][0];
                }
            } else if (y > 0.61 && y < 0.74) {
                // ein Waffenslot?
                if (x > 0.22 && x < 0.36) {
                    item = GameClient.getEquippedItems().getEquipslots()[1][0];
                } else if (x > 0.4 && x < 0.54) {
                    item = GameClient.getEquippedItems().getEquipslots()[1][1];
                } else if (x > 0.58 && x < 0.72) {
                    item = GameClient.getEquippedItems().getEquipslots()[1][2];
                }
            }

            if (item != null) {
                // Item gefunden, jetzt Mousehover rendern
                glDisable(GL_TEXTURE_2D);
                glColor3f(0.9f, 0.9f, 0.9f);
                glRectf((x - 0.01f) * tilesX, (y - 0.01f) * tilesY, (x + 0.3f) * tilesX, (y - 0.015f + 0.05f * item.getItemAttributes().size()) * tilesY);
                glColor3f(1f, 1f, 1f);
                glEnable(GL_TEXTURE_2D);
                // Namen von Item und Itemattributen, umgekehrte Reihenfolge damit Name oben ist
                float yadd = 0.0f;
                for (int i = item.getItemAttributes().size() - 1; i >= 0; i--) {
                    renderText(String.valueOf(item.getItemAttributes().get(i).getName()), x * tilesX, (y + yadd) * tilesY);
                    yadd += 0.05f;
                }
            }
        }

        // Net-Graph?
        if (NetStats.netGraph > 0) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(.9f, .9f, .9f, .7f);
            glRectf(0, tilesY, 10, NetStats.netGraph == 2 ? tilesY - 2f : tilesY - 1.5f);
            glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            renderText("delay: spec " + (NET_TICKSYNC_MAXPING / GameClient.tickrate) + " real " + NetStats.getLastTickDelay() + " avg " + NetStats.getAvgTickDelay(), 0, tilesY - .5f);
            renderText("netIn/tick: number " + NetStats.getAndResetInCounter() + " bytes " + NetStats.getAndResetInBytes(), 0, tilesY - 1);
            renderText("fps: " + fps + " ping: " + NetStats.ping, 0, tilesY - 1.5f);
            if (NetStats.netGraph == 2) {
                // Einheitenposition:
                renderText("playerpos: " + GameClient.getPlayer().getX(), 0, tilesY - 2f);
                renderText(String.valueOf(GameClient.getPlayer().getY()), 6.5f, tilesY - 2f);
            }
        }

        if (terminal) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(.9f, .9f, .9f, .7f);
            glRectf(tilesX / 3, tilesY / 2, tilesX, 0);
            glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            renderText(GameClient.terminal.getCurrentLine(), tilesX / 3, 0, true);
            int numberoflines = tilesY * zoomFactor;
            for (int i = 0; i < numberoflines - 1; i++) {
                renderText(GameClient.terminal.getHistory(i), tilesX / 3, (float) tilesY * ((i + 1) / (float) numberoflines / 2.0f), true);
            }
        }
    }

    public void setZoomFact(int zoomFact) {
        glLoadIdentity();
        GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
        zoomFactor = zoomFact;
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     */
    private void renderText(String text, float x, float y) {
        renderText(text, x, y, false, 0f, 0f, 0f, 1f);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     * @param mono Monospace-Font und (!) klein?
     */
    private void renderText(String text, float x, float y, boolean mono) {
        renderText(text, x, y, mono, 0f, 0f, 0f, 1f);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     * @param red_color Textfarbe Rotanteil
     * @param blue_color Textfarbe Blauanteil
     * @param green_color Textfarbe Grünanteil
     * @param green_color Textfarbe Alpha-anteil
     */
    private void renderText(String text, float x, float y, float red_color, float blue_color, float green_color, float alpha_color) {
        renderText(text, x, y, false, red_color, blue_color, green_color, alpha_color);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x Relative X-Position (0-1)
     * @param y Relative Y-Position (0-1)
     * @param mono Monospace-Font und (!) klein?
     * @param red_color Textfarbe Rotanteil
     * @param blue_color Textfarbe Blauanteil
     * @param green_color Textfarbe Grünanteil
     * @param green_color Textfarbe Alpha-anteil
     */
    private void renderText(String text, float x, float y, boolean mono, float red_color, float blue_color, float green_color, float alpha_color) {
        glColor4f(red_color, blue_color, green_color, alpha_color);
        float next = 0;
        byte[] chars = text.getBytes(charset);
        font[mono ? 1 : 0].bind();
        float size = .5f;
        if (mono) {
            size /= zoomFactor;
        }
        for (int i = 0; i < chars.length; i++) {
            byte c = chars[i];
            int tileX = c % 16;
            int tileY = c / 16;
            float tx = tileX / 16f;
            float ty = tileY / 16f;
            glBegin(GL_QUADS);
            glTexCoord2f(tx, ty);
            glVertex3f(x + next, y + size, 0.0f);
            glTexCoord2f(tx + .0625f, ty);
            glVertex3f(x + next + size, y + size, 0.0f);
            glTexCoord2f(tx + .0625f, ty + .0625f);
            glVertex3f(x + next + size, y, 0.0f);
            glTexCoord2f(tx, ty + .0625f);
            glVertex3f(x + next, y, 0.0f);
            glEnd();
            // Spacing dieses chars weiter gehen:
            next += (mono ? 6 / 16f / zoomFactor : spaceing[c] / 16f);
        }
        glColor3f(1f, 1f, 1f);
    }

    private int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
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
        tilemaps[0] = groundTiles;
        playerTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/player.png"), GL_NEAREST);
        tilemaps[1] = playerTiles;
        enemyTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/enemy.png"), GL_NEAREST);
        tilemaps[2] = enemyTiles;
        bulletTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/bullet.png"), GL_NEAREST);
        tilemaps[3] = bulletTiles;
        itemTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/item.png"), GL_NEAREST);
        tilemaps[4] = itemTiles;
        inventoryPic = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/inventory2.png"), GL_NEAREST);
        tilemaps[5] = inventoryPic;
        fxTiles = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/fx.png"), GL_NEAREST);
        tilemaps[6] = fxTiles;

        font[0] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/font.png"), GL_NEAREST);
        font[1] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/font_mono.png"), GL_NEAREST);
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
        udp2[0] = GameClient.getClientID();
        Bits.putInt(udp2, 1, GameClient.frozenGametick);
        udp2[5] = NET_UDP_CMD_REQUEST_BULLET;
        double dx = Mouse.getX() - Display.getWidth() / 2;
        double dy = Mouse.getY() - Display.getHeight() / 2;
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        Bits.putFloat(udp2, 6, (float) (dir));
        GameClient.udpOut(udp2);
    }

    /**
     * Legt eine Schadenszahl an, die in der nächsten Sekunde gerendert wird
     *
     * @param damage Schaden der angezeigt wird
     * @param x x-Position
     * @param y y-Position
     */
    public static void createDamageNumber(int damage, double x, double y) {
        DamageNumber d = new DamageNumber(damage, x, y, getTime());
        damageNumbers.add(d);
    }

    /**
     * Nimmt einen Fx-Effekt in die FX-Liste auf, so das er gerendert wird
     *
     * @param newfx der Fx-Effekt
     */
    public static void addFx(Fx newfx) {
        fx.add(newfx);
    }

    /**
     * Rendert eine Animation
     *
     * @param animation die Animation, die gerendert werden soll
     * @param x Position
     * @param y Position
     * @param dir Richtung
     * @param starttick Zu welchem Tick die Animation begonnen hat, wichtig, wenn sie beim ersten Bild anfangen soll.
     * Bei Einzelbild egal.
     */
    private void renderAnim(Animation animation, double x, double y, double dir, int starttick) {
        float picsizex = 0.0625f * animation.getPicsizex();
        float picsizey = 0.0625f * animation.getPicsizey();

        int currentpic = ((GameClient.frozenGametick - starttick) / animation.getPicduration()) % animation.getNumberofpics();
        currentpic += animation.getStartpic();

        float v = (currentpic % (16 / animation.getPicsizex())) * picsizex;
        float w = (currentpic / (16 / animation.getPicsizey())) * picsizey;

        glPushMatrix();
        glTranslated(x + panX, y + panY, 0);
        glRotated(dir / Math.PI * 180.0, 0, 0, 1);
        glTranslated(-(x + panX), -(y + panY), 0);
        glBegin(GL_QUADS);
        glTexCoord2f(v, w + picsizey);
        glVertex3f((float) x + panX - 1, (float) y + panY + 1, 0);
        glTexCoord2f(v + picsizex, w + picsizey);
        glVertex3f((float) x + panX + 1, (float) y + panY + 1, 0);
        glTexCoord2f(v + picsizex, w);
        glVertex3f((float) x + panX + 1, (float) y + panY - 1, 0);
        glTexCoord2f(v, w);
        glVertex3f((float) x + panX - 1, (float) y + panY - 1, 0);
        glEnd();
        glPopMatrix();
    }
}
