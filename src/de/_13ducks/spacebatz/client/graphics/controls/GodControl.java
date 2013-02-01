package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Engine;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.DamageNumber;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.network.ClientNetwork2;
import de._13ducks.spacebatz.client.network.NetStats;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_SHOOT;
import java.awt.Color;
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
public class GodControl implements Control {

    /**
     * Tilemaps.
     */
    private Texture groundTiles;
    private Texture topTiles;
    private Texture playerTiles;
    private Texture enemyTiles;
    private Texture bulletTiles;
    private Texture itemTiles;
    private Texture inventoryPic;
    private Texture fxTiles;
    /**
     * Ob das Terminal offen ist. Ein offenes Terminal verhindert jegliche
     * andere Eingaben.
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
     * Kopie vom pan der Kamera, aus Performance-Gründen
     */
    private float panX, panY;
    /**
     * Links auf die Shader.
     */
    private int[] shader;
    /**
     * Schalter für Shadow an oder aus.
     */
    private boolean shadowEnabled = false;
    /**
     * Schalter für HQ-Schatten (smooth)
     */
    private boolean smoothShadows = true;
    /**
     * Hier ist reincodiert, welches Muster sich bei welchen Nachbarschaften
     * ergibt. Bitweise Texturvergleich und OR. Reihenfolge fängt Rechts an,
     * Uhrzeigersinn Angabe 0xFF = Muster F, Rotation F
     */
    private final short[] patternRotationLookupTable = new short[]{0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 0 - 7
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 8 - 15
        0x12, 0x30, 0x12, 0x30, 0x63, 0x70, 0x63, 0x80,// 16 - 23
        0x12, 0x30, 0x12, 0x30, 0x23, 0x90, 0x23, 0x41,// 24 - 31
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 32 - 39
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 40 - 47
        0x12, 0x30, 0x12, 0x30, 0x63, 0x70, 0x63, 0x80,// 48 - 55
        0x12, 0x30, 0x12, 0x30, 0x23, 0x90, 0x23, 0x41,// 56 - 63
        0x11, 0x61, 0x11, 0x61, 0x31, 0x71, 0x31, 0x91,// 64 - 71
        0x11, 0x61, 0x11, 0x61, 0x31, 0x71, 0x31, 0x91,// 72 - 79
        0x62, 0x72, 0x62, 0x72, 0x73, 0xA0, 0x73, 0xB0,// 80 - 87
        0x62, 0x72, 0x62, 0x72, 0x83, 0xB3, 0x83, 0xC0,// 88 - 95
        0x11, 0x61, 0x11, 0x61, 0x31, 0x71, 0x31, 0x91,// 96 - 103
        0x11, 0x61, 0x11, 0x61, 0x31, 0x71, 0x31, 0x91,// 104 - 111
        0x22, 0x82, 0x22, 0x82, 0x93, 0xB2, 0x93, 0xD0,// 112 - 119
        0x22, 0x82, 0x22, 0x82, 0x40, 0xC3, 0x40, 0xE0,// 120 - 127
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 128 - 135
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 136 - 143
        0x12, 0x30, 0x12, 0x30, 0x63, 0x70, 0x63, 0x80,// 144 - 151
        0x12, 0x30, 0x12, 0x30, 0x23, 0x90, 0x23, 0x41,// 152 - 159
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 160 - 167
        0x00, 0x10, 0x00, 0x10, 0x13, 0x60, 0x13, 0x20,// 168 - 175
        0x12, 0x30, 0x12, 0x30, 0x63, 0x70, 0x63, 0x80,// 176 - 183
        0x12, 0x30, 0x12, 0x30, 0x23, 0x90, 0x23, 0x41,// 184 - 191
        0x11, 0x21, 0x11, 0x21, 0x31, 0x81, 0x31, 0x42,// 192 - 199
        0x11, 0x21, 0x11, 0x21, 0x31, 0x81, 0x31, 0x42,// 200 - 207
        0x62, 0x92, 0x62, 0x92, 0x73, 0xB1, 0x73, 0xC1,// 208 - 215
        0x62, 0x92, 0x62, 0x92, 0x83, 0xD1, 0x83, 0xE1,// 216 - 223
        0x11, 0x21, 0x11, 0x21, 0x31, 0x81, 0x31, 0x42,// 224 - 231
        0x11, 0x21, 0x11, 0x21, 0x31, 0x81, 0x31, 0x42,// 232 - 239
        0x22, 0x43, 0x22, 0x43, 0x93, 0xC2, 0x93, 0xE2,// 240 - 247
        0x22, 0x43, 0x22, 0x43, 0x40, 0xE3, 0x40, 0x50};// 248 - 255

    public GodControl(Renderer renderer) {
        groundTiles = renderer.getTextureByName("ground.png");
        topTiles = renderer.getTextureByName("top.png");
        playerTiles = renderer.getTextureByName("player.png");
        enemyTiles = renderer.getTextureByName("enemy.png");
        bulletTiles = renderer.getTextureByName("bullet.png");
        itemTiles = renderer.getTextureByName("item.png");
        inventoryPic = renderer.getTextureByName("inventory2.png");
        fxTiles = renderer.getTextureByName("fx.png");

        // Shader laden
        //System.out.println("INFO: GFX: Loading/compiling shaders...");
        //shader = ShaderLoader.load();
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
                sendAbilityRequest((byte) 1);
            }
            if (Mouse.isButtonDown(0)) {
                sendShootRequest();
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
                            GameClient.getEngine().getGraphics().toggleInventory();

                            break;

                        case Keyboard.KEY_T:
                            GameClient.getEngine().getGraphics().toggleSkillTree();
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
                        if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= ' ' && c <= '?')) {
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

        glClear(GL_STENCIL_BUFFER_BIT); // Stencil-Buffer löschen.
        // Boden und Wände zeichnen
        // Werte cachen
        panX = renderer.getCamera().getPanX();
        panY = renderer.getCamera().getPanY();
        int[][] ground = GameClient.currentLevel.ground;
        int[][] dye_ground = GameClient.currentLevel.dye_ground;
        // Boden zuerst
        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + camera.getTilesX() + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + camera.getTilesY() + 2; y++) {
                int tex = texAt(ground, x, y);
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                int dye = dyeAt(dye_ground, x, y, Color.WHITE.getRGB());
                if (shadow != 127 || !shadowEnabled || smoothShadows) {
                    glColor3ub((byte) ((0x00FF0000 & dye) >>> 16), (byte) ((0x0000FF00 & dye) >>> 8), (byte) (0x000000FF & dye));
                    drawColoredTile(tex, x + 0.5f, y + 0.5f, 0, dye, dyeAt(dye_ground, x, y + 1, Color.WHITE.getRGB()), dyeAt(dye_ground, x + 1, y, Color.WHITE.getRGB()), dyeAt(dye_ground, x + 1, y + 1, Color.WHITE.getRGB()));
                }
            }
        }
        glColor3f(1f, 1f, 1f);
        
        int[][] top = GameClient.currentLevel.top;
        int[][] dye_top = GameClient.currentLevel.dye_top;
        topTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + camera.getTilesX() + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + camera.getTilesY() + 2; y++) {
                int tex = texAt(top, x, y);
                int patRot = patternAt(top, x, y);
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                int dye = dyeAt(dye_top, x, y, Color.GRAY.getRGB());
                if ((shadow != 127 || !shadowEnabled || smoothShadows) && tex != 0) {
                    if ((patRot >> 4) != 5) {
                        int rot = patRot & 0x0F;
                        // Bild im Stencil-Buffer erzeugen:
                        glEnable(GL_STENCIL_TEST); // Stenciling ist an
                        // Stencil-Test schlägt immer fehl, malt also nix. Aber alle verwendeten Pixel erhöhen den Stencil-Buffer:
                        glStencilFunc(GL_NEVER, 0x0, 0x0);
                        glStencilOp(GL_INCR, GL_INCR, GL_INCR);
                        // Pixel dem Alpha nach ignorieren
                        glAlphaFunc(GL_NOTEQUAL, 0f);
                        glEnable(GL_ALPHA_TEST);
                        // Pattern malen, beeinflusst Stencil-Buffer
                        drawUncoloredTile(241 + (patRot >> 4), x, y, rot);
                        glDisable(GL_ALPHA_TEST);
                        // Jetzt ist der Stencil-Buffer ok. Jetzt Modus auf "malen, wenn stencil das sagt" stellen
                        glStencilFunc(GL_NOTEQUAL, 0x0, 0x1);
                        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
                        // Jetzt Wand malen:
                        glColor3ub((byte) ((0x00FF0000 & dye) >>> 16), (byte) ((0x0000FF00 & dye) >>> 8), (byte) (0x000000FF & dye));
                        drawUncoloredTile(tex, x, y, 0);
                        // Fertig, Stencil abschalten:
                        glDisable(GL_STENCIL_TEST);
                        // Zweite Maske drüber, für schönes Aussehen
                        drawUncoloredTile(225 + (patRot >> 4), x, y, rot);
                    } else {
                        glColor3ub((byte) ((0x00FF0000 & dye) >>> 16), (byte) ((0x0000FF00 & dye) >>> 8), (byte) (0x000000FF & dye));
                        drawUncoloredTile(tex, x, y, 0);
                    }
                }
            }
        }
        glColor4f(1f, 1f, 1f, 1f);
        

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
                if (!((PlayerCharacter) c).isDead()) {
                    renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0, renderer);
                }
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
                if (f.getOwner() != null) {
                    renderAnim(f.getAnim(), f.getOwner().getX(), f.getOwner().getY(), f.getAnim().getDirection(), f.getStarttick(), renderer);
                } else {
                    renderAnim(f.getAnim(), f.getX(), f.getY(), f.getAnim().getDirection(), f.getStarttick(), renderer);
                }
            }
        }

        // Schadenszahlen zeichnen
        Iterator<DamageNumber> iter = damageNumbers.iterator();
        while (iter.hasNext()) {
            DamageNumber d = iter.next();
            if (Engine.getTime() > d.getSpawntime() + DAMAGENUMBER_LIFETIME) {
                // alt - > löschen
                iter.remove();
            } else {
                //rendern:
                float height = (Engine.getTime() - d.getSpawntime()) / 250.0f;
                float visibility = 1 - ((float) (Engine.getTime() - d.getSpawntime())) / DAMAGENUMBER_LIFETIME; // Anteil der vergangenen Zeit an der Gesamtlebensdauer
                visibility = Math.min(visibility * 2, 1); // bis 0.5 * lifetime: visibility 1, dann linear auf 0
                textWriter.renderText(String.valueOf(d.getDamage()), (float) d.getX() + renderer.getCamera().getPanX(), (float) d.getY() + renderer.getCamera().getPanY() + height, 1f, .1f, .2f, visibility);
            }
        }

        // Shadow zeichnen:
        if (shadowEnabled) {
            if (!smoothShadows) {
                int lastShadow = -1;
                glDisable(GL_TEXTURE_2D);
                for (int x = -(int) (1 + panX); x < -(1 + panX) + camera.getTilesX() + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + camera.getTilesY() + 2; y++) {
                        int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                        if (shadow != lastShadow) {
                            glColor4f(0f, 0f, 0f, 0.0078740157f * shadow);
                            lastShadow = shadow;
                        }
                        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        glVertex3f(x + 1 + panX, y + panY, 0);
                        glVertex3f(x + panX, y + panY, 0);
                        glEnd(); // Zeichnen des QUADs fertig
                    }
                }
                glEnable(GL_TEXTURE_2D);
            } else {
                byte[][] shadowMap = GameClient.currentLevel.shadow;
                // Neue smooth-Schatten:
                glDisable(GL_TEXTURE_2D);
                for (int x = -(int) (1 + panX); x < -(1 + panX) + camera.getTilesX() + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + camera.getTilesY() + 2; y++) {
                        int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                        // 4 Schattenpunkte
                        float lo, lu, ro, ru;
                        // Default-Schatten
                        lo = (shadowAt(shadowMap, x - 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x - 1, y) + shadow) / 4f;
                        lu = (shadowAt(shadowMap, x - 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x - 1, y) + shadow) / 4f;
                        ro = (shadowAt(shadowMap, x + 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x + 1, y) + shadow) / 4f;
                        ru = (shadowAt(shadowMap, x + 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x + 1, y) + shadow) / 4f;
                        lo *= 0.0078740157f;
                        lu *= 0.0078740157f;
                        ro *= 0.0078740157f;
                        ru *= 0.0078740157f;
                        // Jetzt zeichnen
                        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        glColor4f(0f, 0f, 0f, lo);
                        glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                        glColor4f(0f, 0f, 0f, ro);
                        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        glColor4f(0f, 0f, 0f, ru);
                        glVertex3f(x + 1 + panX, y + panY, 0);
                        glColor4f(0f, 0f, 0f, lu);
                        glVertex3f(x + panX, y + panY, 0);
                        glEnd(); // Zeichnen des QUADs fertig
                    }
                }
                glEnable(GL_TEXTURE_2D);
            }
        }

        // Net-Graph?
        if (NetStats.netGraph > 0) {
            ClientNetwork2 net = GameClient.getNetwork2();
            boolean connectionAlive = net.connectionAlive();
            glDisable(GL_TEXTURE_2D);
            if (connectionAlive) {
                glColor4f(.9f, .9f, .9f, .7f);
            } else {
                glColor4f(1f, 0f, 0f, 1f);
            }
            glRectf(0, camera.getTilesY(), 10, NetStats.netGraph == 2 ? camera.getTilesY() - 2.5f : camera.getTilesY() - 1.5f);
            glColor4f(1f, 1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            if (connectionAlive) {
                textWriter.renderText("lerp: " + net.getLerp() + " (~" + (Settings.SERVER_TICKRATE * net.getLerp() + "ms)"), 0, camera.getTilesY() - .5f);
                //renderText("netIn/tick: number " + NetStats.getAndResetInCounter() + " bytes " + NetStats.getAndResetInBytes(), 0, camera.getTilesY() - 1);
                textWriter.renderText("fps: " + GameClient.getEngine().getFps() + " ping: " + NetStats.ping, 0, camera.getTilesY() - 1f);
                textWriter.renderText("Net %health: " + net.getConnectionHealthPercent(), 0, camera.getTilesY() - 1.5f, net.getConnectionHealthPercent() < 95 ? 1 : 0, 0, 0, 1);
                textWriter.renderText("%load: " + net.getConnectionLoadPercent(), 6.5f, camera.getTilesY() - 1.5f, net.getConnectionLoadPercent() > 80 ? 1 : 0, 0, 0, 1);
                if (NetStats.netGraph == 2) {
                    // Einheitenposition:
                    textWriter.renderText("playerpos: " + GameClient.getPlayer().getX(), 0, camera.getTilesY() - 2f);
                    textWriter.renderText(String.valueOf(GameClient.getPlayer().getY()), 6.5f, camera.getTilesY() - 2f);
                    // Mausposition:
                    textWriter.renderText(String.format("Mouse: %.2f", -camera.getPanX() + (Mouse.getX() / (double) Settings.CLIENT_GFX_RES_X) * camera.getTilesX()), 0, camera.getTilesY() - 2.5f);
                    textWriter.renderText(String.format("%.2f", -camera.getPanY() + (Mouse.getY() / (double) Settings.CLIENT_GFX_RES_Y) * camera.getTilesY()), 6.5f, camera.getTilesY() - 2.5f);
                }
            } else {
                textWriter.renderText(" LOST CONNECTION TO SERVER", 0, camera.getTilesY() - 1.5f);
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
                textWriter.renderText(GameClient.terminal.getHistory(i), camera.getTilesX() / 3, camera.getTilesY() * ((i + 1) / (float) numberoflines / 2.0f), true);
            }
        }

    }

    private static int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 1;
        } else {
            return layer[x][y];
        }
    }

    private static int shadowAt(byte[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 127;
        } else {
            return layer[x][y];
        }
    }
    
    private static int dyeAt(int[][] layer, int x, int y, int errorColor) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return errorColor;
        } else {
            return layer[x][y];
        }
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
        DamageNumber d = new DamageNumber(damage, x, y, Engine.getTime());
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
     * @param starttick Zu welchem Tick die Animation begonnen hat, wichtig,
     * wenn sie beim ersten Bild anfangen soll. Bei Einzelbild egal.
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

    public void sendAbilityRequest(byte ability) {
        double dx = Mouse.getX() - Display.getWidth() / 2;
        double dy = Mouse.getY() - Display.getHeight() / 2;
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        // Fragwürdige Berechnung der Distanz:
        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * GameClient.getEngine().getGraphics().getCamera().getTilesX() / Display.getWidth();
        CTS_REQUEST_USE_ABILITY.sendAbilityUseRequest(ability, (float) dir, distance);
    }

    private void drawUncoloredTile(int tile, float x, float y, int numRot) {
        int tx = tile % 16;
        int ty = tile / 16;
        float[][] tileCoords = new float[2][4]; // X, Y dann 4 Ecken
        tileCoords[0][0] = tx * 0.0625f + 0.001953125f;
        tileCoords[0][1] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][2] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][3] = tx * 0.0625f + 0.001953125f;
        tileCoords[1][0] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][1] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][2] = ty * 0.0625f + 0.060546875f;
        tileCoords[1][3] = ty * 0.0625f + 0.060546875f;
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(tileCoords[0][(0 + numRot) % 4], tileCoords[1][(0 + numRot) % 4]); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
        glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
        // Die weiteren 3 Ecken im Uhrzeigersinn:
        glTexCoord2f(tileCoords[0][(1 + numRot) % 4], tileCoords[1][(1 + numRot) % 4]);
        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
        glTexCoord2f(tileCoords[0][(2 + numRot) % 4], tileCoords[1][(2 + numRot) % 4]);
        glVertex3f(x + 1 + panX, y + panY, 0);
        glTexCoord2f(tileCoords[0][(3 + numRot) % 4], tileCoords[1][(3 + numRot) % 4]);
        glVertex3f(x + panX, y + panY, 0);
        glEnd(); // Zeichnen des QUADs fertig
    }
    
    private void drawColoredTile(int tile, float x, float y, int numRot, int colLU, int colLO, int colRU, int colRO) {
        int tx = tile % 16;
        int ty = tile / 16;
        float[][] tileCoords = new float[2][4]; // X, Y dann 4 Ecken
        tileCoords[0][0] = tx * 0.0625f + 0.001953125f;
        tileCoords[0][1] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][2] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][3] = tx * 0.0625f + 0.001953125f;
        tileCoords[1][0] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][1] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][2] = ty * 0.0625f + 0.060546875f;
        tileCoords[1][3] = ty * 0.0625f + 0.060546875f;
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(tileCoords[0][(0 + numRot) % 4], tileCoords[1][(0 + numRot) % 4]); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
        glColor3ub((byte) ((0x00FF0000 & colLO) >>> 16), (byte) ((0x0000FF00 & colLO) >>> 8), (byte) (0x000000FF & colLO));
        glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
        // Die weiteren 3 Ecken im Uhrzeigersinn:
        glTexCoord2f(tileCoords[0][(1 + numRot) % 4], tileCoords[1][(1 + numRot) % 4]);
        glColor3ub((byte) ((0x00FF0000 & colRO) >>> 16), (byte) ((0x0000FF00 & colRO) >>> 8), (byte) (0x000000FF & colRO));
        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
        glTexCoord2f(tileCoords[0][(2 + numRot) % 4], tileCoords[1][(2 + numRot) % 4]);
        glColor3ub((byte) ((0x00FF0000 & colRU) >>> 16), (byte) ((0x0000FF00 & colRU) >>> 8), (byte) (0x000000FF & colRU));
        glVertex3f(x + 1 + panX, y + panY, 0);
        glTexCoord2f(tileCoords[0][(3 + numRot) % 4], tileCoords[1][(3 + numRot) % 4]);
        glColor3ub((byte) ((0x00FF0000 & colLU) >>> 16), (byte) ((0x0000FF00 & colLU) >>> 8), (byte) (0x000000FF & colLU));
        glVertex3f(x + panX, y + panY, 0);
        glEnd(); // Zeichnen des QUADs fertig
    }

    /**
     * Liest das Pattern aus.
     */
    private int patternAt(int[][] tex, int x, int y) {
        int myTex = texAt(tex, x, y);
        return patternRotationLookupTable[(myTex == texAt(tex, x + 1, y) ? 1 : 0) | (myTex == texAt(tex, x + 1, y - 1) ? 2 : 0) | (myTex == texAt(tex, x, y - 1) ? 4 : 0) | (myTex == texAt(tex, x - 1, y - 1) ? 8 : 0) | (myTex == texAt(tex, x - 1, y) ? 16 : 0) | (myTex == texAt(tex, x - 1, y + 1) ? 32 : 0) | (myTex == texAt(tex, x, y + 1) ? 64 : 0) | (myTex == texAt(tex, x + 1, y + 1) ? 128 : 0)];
    }

    /**
     * @return the shadowEnabled
     */
    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    /**
     * @param shadowEnabled the shadowEnabled to set
     */
    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
    }

    /**
     * @return the smoothShadows
     */
    public boolean isShadowSmoothing() {
        return smoothShadows;
    }

    /**
     * @param smoothShadows the smoothShadows to set
     */
    public void setShadowSmoothing(boolean smoothShadows) {
        this.smoothShadows = smoothShadows;
    }
}
