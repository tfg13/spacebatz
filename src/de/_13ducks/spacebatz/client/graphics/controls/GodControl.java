package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.Settings;
import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.InventorySlot;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.DamageNumber;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
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
import java.util.Iterator;
import java.util.LinkedList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author michael
 */
public class GodControl extends Control {

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

    public GodControl(Renderer renderer) {
        tilemaps = new Texture[10];
        selecteditemslot = -1;

        // Der letzte Parameter sagt OpenGL, dass es Pixel beim vergrößern/verkleinern nicht aus Mittelwerten von mehreren berechnen soll,
        // sondern einfach den nächstbesten nehmen. Das sort für den Indie-Pixelart-Look
        groundTiles = renderer.getTextureByName("ground.png");
        playerTiles = renderer.getTextureByName("player.png");
        enemyTiles = renderer.getTextureByName("enemy.png");
        bulletTiles = renderer.getTextureByName("bullet.png");
        itemTiles = renderer.getTextureByName("item.png");
        inventoryPic = renderer.getTextureByName("inventory2.png");
        fxTiles = renderer.getTextureByName("fx.png");
        tilemaps[0] = groundTiles;
        tilemaps[1] = playerTiles;
        tilemaps[2] = enemyTiles;
        tilemaps[3] = bulletTiles;
        tilemaps[4] = itemTiles;
        tilemaps[5] = inventoryPic;
        tilemaps[6] = fxTiles;

    }

    /**
     * Verarbeitet den Input, der UDP-Relevant ist.
     */
    @Override
    public void input() {
        byte move = 0;
        if (!terminal) {
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                move |= 0x20;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                move |= 0x80;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                move |= 0x40;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                move |= 0x10;
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
        CTS_MOVE.sendMove(move);
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    @Override
    public void render(Renderer renderer) {

        Camera camera = renderer.getCamera();
        TextWriter textWriter = renderer.getTextWriter();

        renderer.getCamera().setPanX((float) -GameClient.getPlayer().getX() + camera.getTilesX() / 2.0f);
        renderer.getCamera().setPanY((float) -GameClient.getPlayer().getY() + camera.getTilesY() / 2.0f);



        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + renderer.getCamera().getPanX()); x < -(1 + renderer.getCamera().getPanX()) + camera.getTilesX() + 2; x++) {
            for (int y = -(int) (1 + renderer.getCamera().getPanY()); y < -(1 + renderer.getCamera().getPanY()) + camera.getTilesY() + 2; y++) {
                int tex = textWriter.texAt(GameClient.currentLevel.getGround(), x, y);
                int tx = tex % 16;
                int ty = tex / 16;
                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(tx * 0.0625f, ty * 0.0625f); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
                glVertex3f(x + renderer.getCamera().getPanX(), y + 1 + renderer.getCamera().getPanY(), 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                // Die weiteren 3 Ecken im Uhrzeigersinn:
                glTexCoord2f((tx + 1) * 0.0625f, ty * 0.0625f);
                glVertex3f(x + 1 + renderer.getCamera().getPanX(), y + 1 + renderer.getCamera().getPanY(), 0);
                glTexCoord2f((tx + 1) * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + 1 + renderer.getCamera().getPanX(), y + renderer.getCamera().getPanY(), 0);
                glTexCoord2f(tx * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + renderer.getCamera().getPanX(), y + renderer.getCamera().getPanY(), 0);
                glEnd(); // Zeichnen des QUADs fertig
            }
        }

        // Enemies zeichnen:
        enemyTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Enemy) {
                Enemy enemy = (Enemy) c;

                // Werte fürs Einfärben nehmen und rendern
                EnemyTypeStats ets = GameClient.enemytypes.getEnemytypelist().get(enemy.getEnemytypeid());
                glColor4f(ets.getColor_red(), ets.getColor_green(), ets.getColor_blue(), ets.getColor_alpha());
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0, renderer);
                glColor3f(1f, 1f, 1f);

            }
        }

        // Players zeichnen:
        playerTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof PlayerCharacter) {
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0, renderer);
            }
        }

        // Bullets zeichnen
        bulletTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Bullet) {
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0, renderer);
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
                renderAnim(f.getAnim(), f.getX(), f.getY(), 0.0, f.getStarttick(), renderer);
            }
        }

        // Schadenszahlen zeichnen
        Iterator<DamageNumber> iter = damageNumbers.iterator();
        while (iter.hasNext()) {
            DamageNumber d = iter.next();
            if (GameClient.getEngine().getTime() > d.getSpawntime() + DAMAGENUMBER_LIFETIME) {
                // alt - > löschen
                iter.remove();
            } else {
                //rendern:
                float height = (GameClient.getEngine().getTime() - d.getSpawntime()) / 250.0f;
                float visibility = 1 - ((float) (GameClient.getEngine().getTime() - d.getSpawntime())) / DAMAGENUMBER_LIFETIME; // Anteil der vergangenen Zeit an der Gesamtlebensdauer
                visibility = Math.min(visibility * 2, 1); // bis 0.5 * lifetime: visibility 1, dann linear auf 0
                textWriter.renderText(String.valueOf(d.getDamage()), (float) d.getX() + renderer.getCamera().getPanX(), (float) d.getY() + renderer.getCamera().getPanY() + height, 1f, .1f, .2f, visibility);
            }
        }

        // Lebensenergie-Balken im HUD zeichnen
        int maxhp = Math.max(1, GameClient.getPlayer().getHealthpointsmax());
        int hp = Math.min(GameClient.getPlayer().getHealthpoints(), maxhp);
        hp = Math.max(hp, 0);

        glDisable(GL_TEXTURE_2D);
        // schwarzer Hintergrund
        glColor3f(0.0f, 0.0f, 0.0f);
        glRectf(0.02f * camera.getTilesX(), 0.02f * camera.getTilesY(), 0.3f * camera.getTilesX(), 0.06f * camera.getTilesY());
        // roter HP-Balken, Länge anhängig von HP
        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(0.03f * camera.getTilesX(), 0.03f * camera.getTilesY(), (0.03f + 0.26f * ((float) hp / maxhp)) * camera.getTilesX(), 0.05f * camera.getTilesY());
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);

        // Inventory-Hintergrund zeichnen
        inventoryPic.bind();
        if (showinventory) {
            glBegin(GL_QUADS);
            glTexCoord2f(0, 1);
            glVertex3f(0, 0, 0);
            glTexCoord2f(1, 1);
            glVertex3f(camera.getTilesX(), 0, 0);
            glTexCoord2f(1, 0);
            glVertex3f(camera.getTilesX(), camera.getTilesY(), 0);
            glTexCoord2f(0, 0);
            glVertex3f(0, camera.getTilesY(), 0);
            glEnd();
        }

        // Items im Inventory zeichnen
        if (showinventory) {
            // Anzahl der Materialien:
            textWriter.renderText(String.valueOf(GameClient.getMaterial(0)), 0.12f * camera.getTilesX(), 0.44f * camera.getTilesY());
            textWriter.renderText(String.valueOf(GameClient.getMaterial(1)), 0.45f * camera.getTilesX(), 0.44f * camera.getTilesY());
            textWriter.renderText(String.valueOf(GameClient.getMaterial(2)), 0.75f * camera.getTilesX(), 0.44f * camera.getTilesY());

            for (int i = 12 * inventorypage; i < 12 * inventorypage + 12; i++) {

                if (GameClient.getInventorySlots()[i] == null || i == selecteditemslot) {
                    // Slot leer oder gerade selected -> nicht zeichnen
                    continue;
                }
                itemTiles.bind();

                Item item = GameClient.getInventorySlots()[i].getItem();

                float x = (0.1075f + 0.133f * (i % 6)) * camera.getTilesX();

                float y;
                if (i % 12 < 6) {
                    y = 0.191f * camera.getTilesY();
                } else {
                    y = 0.061f * camera.getTilesY();
                }

                float width = 0.11f * camera.getTilesX();
                float height = 0.11f * camera.getTilesY();

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

        // ausgewählten Waffenslot im Inventar markieren:
        if (showinventory) {
            glDisable(GL_TEXTURE_2D);
            float wx = 0.227f + 0.172f * GameClient.getPlayer().getSelectedattack();

            glColor3f(0.7f, 0.0f, 0.0f);
            glRectf(wx * camera.getTilesX(), 0.59f * camera.getTilesY(), (wx + 0.14f) * camera.getTilesX(), 0.6f * camera.getTilesY());
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
                            x = (0.24f + 0.17f * j) * camera.getTilesX();
                        } else {
                            x = 0.41f * camera.getTilesX();
                        }
                        float y = (0.61f + 0.2f * (i - 1)) * camera.getTilesY();

                        float width = 0.11f * camera.getTilesX();
                        float height = 0.11f * camera.getTilesY();

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
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X * camera.getTilesX();
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y * camera.getTilesY();

            float size = 0.08f;

            float v = 0.0625f * (int) item.getPic();
            float w = 0.0625f * ((int) item.getPic() / 16);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.0625f);
            glVertex3f(x - camera.getTilesX() * size / 2, y - camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w + 0.0625f);
            glVertex3f(x + camera.getTilesX() * size / 2, y - camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w);
            glVertex3f(x + camera.getTilesX() * size / 2, y + camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x - camera.getTilesX() * size / 2, y + camera.getTilesX() * size / 2, 0.0f);
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
                glRectf((x - 0.01f) * camera.getTilesX(), (y - 0.01f) * camera.getTilesY(), (x + 0.3f) * camera.getTilesX(), (y - 0.015f + 0.05f * item.getItemAttributes().size()) * camera.getTilesY());
                glColor3f(1f, 1f, 1f);
                glEnable(GL_TEXTURE_2D);
                // Namen von Item und Itemattributen, umgekehrte Reihenfolge damit Name oben ist
                float yadd = 0.0f;
                for (int i = item.getItemAttributes().size() - 1; i >= 0; i--) {
                    textWriter.renderText(String.valueOf(item.getItemAttributes().get(i).getName()), x * camera.getTilesX(), (y + yadd) * camera.getTilesY());
                    yadd += 0.05f;
                }
            }
        }

        // Net-Graph?
        if (NetStats.netGraph > 0) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(.9f, .9f, .9f, .7f);
            glRectf(0, camera.getTilesY(), 10, NetStats.netGraph == 2 ? camera.getTilesY() - 2f : camera.getTilesY() - 1.5f);
            glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            textWriter.renderText("lerp: " + GameClient.getNetwork2().getLerp() + " (~" + (Settings.SERVER_TICKRATE * GameClient.getNetwork2().getLerp() + "ms)"), 0, camera.getTilesY() - .5f);
            //renderText("netIn/tick: number " + NetStats.getAndResetInCounter() + " bytes " + NetStats.getAndResetInBytes(), 0, camera.getTilesY() - 1);
            textWriter.renderText("fps: " + GameClient.getEngine().getFps() + " ping: " + NetStats.ping, 0, camera.getTilesY() - 1.5f);
            if (NetStats.netGraph == 2) {
                // Einheitenposition:
                textWriter.renderText("playerpos: " + GameClient.getPlayer().getX(), 0, camera.getTilesY() - 2f);
                textWriter.renderText(String.valueOf(GameClient.getPlayer().getY()), 6.5f, camera.getTilesY() - 2f);
            }
        }

        if (terminal) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(.9f, .9f, .9f, .7f);
            glRectf(camera.getTilesX() / 3, camera.getTilesY() / 2, camera.getTilesX(), 0);
            glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            textWriter.renderText(GameClient.terminal.getCurrentLine(), camera.getTilesX() / 3, 0, true);
            int numberoflines = (int) ((int) camera.getTilesY() * camera.getZoomFactor());
            for (int i = 0; i < numberoflines - 1; i++) {
                textWriter.renderText(GameClient.terminal.getHistory(i), camera.getTilesX() / 3, (float) camera.getTilesY() * ((i + 1) / (float) numberoflines / 2.0f), true);
            }
        }
       
    }

    /**
     * Läd alle benötigten Texturen.
     *
     * @throws IOException Wenn was schief geht
     */
    private void loadTex() throws IOException {
    }

    /**
     * Sagt dem Server, das geschossen werden soll
     */
    private void sendShootRequest() {
        double dx = Mouse.getX() - Display.getWidth() / 2;
        double dy = Mouse.getY() - Display.getHeight() / 2;
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        // Fragwürdige Berechnung der Distanz:
        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * GameClient.getEngine().getGraphics().getCamera().getTilesX() / Display.getWidth();
        CTS_SHOOT.sendShoot(dir, distance);
    }

    /**
     * Legt eine Schadenszahl an, die in der nächsten Sekunde gerendert wird
     *
     * @param damage Schaden der angezeigt wird
     * @param x x-Position
     * @param y y-Position
     */
    public static void createDamageNumber(int damage, double x, double y) {
        DamageNumber d = new DamageNumber(damage, x, y, GameClient.getEngine().getTime());
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
    private void renderAnim(Animation animation, double x, double y, double dir, int starttick, Renderer renderer) {
        float picsizex = 0.0625f * animation.getPicsizex();
        float picsizey = 0.0625f * animation.getPicsizey();

        int currentpic = ((GameClient.frozenGametick - starttick) / animation.getPicduration()) % animation.getNumberofpics();
        currentpic += animation.getStartpic();

        float v = (currentpic % (16 / animation.getPicsizex())) * picsizex;
        float w = (currentpic / (16 / animation.getPicsizey())) * picsizey;

        glPushMatrix();
        glTranslated(x + renderer.getCamera().getPanX(), y + renderer.getCamera().getPanY(), 0);
        glRotated(dir / Math.PI * 180.0, 0, 0, 1);
        glTranslated(-(x + renderer.getCamera().getPanX()), -(y + renderer.getCamera().getPanY()), 0);
        glBegin(GL_QUADS);
        glTexCoord2f(v, w + picsizey);
        glVertex3f((float) x + renderer.getCamera().getPanX() - 1, (float) y + renderer.getCamera().getPanY() + 1, 0);
        glTexCoord2f(v + picsizex, w + picsizey);
        glVertex3f((float) x + renderer.getCamera().getPanX() + 1, (float) y + renderer.getCamera().getPanY() + 1, 0);
        glTexCoord2f(v + picsizex, w);
        glVertex3f((float) x + renderer.getCamera().getPanX() + 1, (float) y + renderer.getCamera().getPanY() - 1, 0);
        glTexCoord2f(v, w);
        glVertex3f((float) x + renderer.getCamera().getPanX() - 1, (float) y + renderer.getCamera().getPanY() - 1, 0);
        glEnd();
        glPopMatrix();
    }

    public void sendAbilityRequest() {
        double dx = Mouse.getX() - Display.getWidth() / 2;
        double dy = Mouse.getY() - Display.getHeight() / 2;
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        // Fragwürdige Berechnung der Distanz:
        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * GameClient.getEngine().getGraphics().getCamera().getTilesX() / Display.getWidth();
        CTS_REQUEST_USE_ABILITY.sendAbilityUseRequest((byte) 0, (float) dir, distance);
    }
}
