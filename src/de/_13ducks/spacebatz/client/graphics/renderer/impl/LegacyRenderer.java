package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Engine;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.data.LogicPlayer;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.DamageNumber;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.ShaderLoader;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.shared.DefaultSettings;
import static de._13ducks.spacebatz.shared.DefaultSettings.CLIENT_GFX_RES_X;
import static de._13ducks.spacebatz.shared.DefaultSettings.CLIENT_GFX_RES_Y;
import static de._13ducks.spacebatz.shared.DefaultSettings.CLIENT_GFX_TILESIZE;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBShaderObjects;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.Texture;

/**
 * Haupt-Rendermodul.
 *
 * @author Tobias Fleig <tobifleig@gmail.com>
 */
public class LegacyRenderer extends CoreRenderer {

    /**
     * Tilemaps.
     */
    private Texture groundTiles;
    private Texture topTiles;
    private Texture playerTiles;
    private Texture enemyTiles;
    private Texture bulletTiles;
    private Texture fxTiles;
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
     * Scrollen auf der Map, in Feldern.
     * TODO: Change public static to private, after Overlays switched to absolute rendering
     */
    public static float panX, panY;
    /**
     * Anzahl Tiles auf dem Bildschrim, in Feldern.
     * TODO: Change public static to private, after Overlays switched to absolute rendering
     */
    public static float tilesX, tilesY;
    /**
     * Zoomfaktor, fliegt bald raus.
     */
    @Deprecated
    private float zoomFactor;
    /**
     * Links auf die Shader.
     */
    private int[] shader;
    /**
     * Schatten-Einstellung.
     * 0 - Aus
     * 1 - Blöcke
     * 2 - Smooth
     * 3 - Shader (best)
     */
    private int shadowLevel = DefaultSettings.CLIENT_GFX_SHADOWLEVEL;
    /**
     * Übergänge in Top mit Konturen zeichnen?
     */
    private boolean fancyTop = DefaultSettings.CLIENT_GFX_TOP_FANCY;
    /**
     * Bestimmte Übergänge im Boden weichzeichnen?
     */
    private boolean smoothGround = DefaultSettings.CLIENT_GFX_GROUND_SMOOTH;
    /**
     * Alternativer Sichtmodus, bei dem die Einheit nicht immer in der Mitte ist, sondern stark von der Mausposition abhängt.
     */
    private boolean lookahead = DefaultSettings.CLIENT_GFX_LOOKAHEAD;
    /**
     * Ob und wie Spielernamen angezeigt werden.
     * 0 - Aus
     * 1 - Hover
     * 2 - Immer
     */
    private int showNickNames = DefaultSettings.CLIENT_GFX_SHOW_NICKNAMES;
    /**
     * Ob für (viele, nicht alle) Zeichenoperationen VBOs statt immediate-Zeichnen verwendet werden soll.
     * Normalerweise viel schneller, eventuell aber auf sehr, sehr antiken Karten nicht unterstützt.
     */
    private boolean useVBOs = DefaultSettings.CLIENT_GFX_USE_VBOS;
    private FloatBuffer tvBuffer;
    private IntBuffer ib;
    private int tvHandle;
    /**
     * Fadenkreuz-Cursor.
     */
    private Cursor crossHairCursor;
    /**
     * Die Position der Maus in Pixeln.
     */
    private double mouseX, mouseY;
    /**
     * Die Position der Maus in Spielkoordinaten.
     */
    private double logicMouseX, logicMouseY;
    /**
     * Hier ist reincodiert, welches Muster sich bei welchen Nachbarschaften
     * ergibt. Bitweise Texturvergleich und OR. Reihenfolge fängt Rechts an,
     * Gegen den Uhrzeigersinn Angabe 0xFF = Muster F, Rotation F
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

    public LegacyRenderer() {
        groundTiles = RenderUtils.getTextureByName("ground.png");
        topTiles = RenderUtils.getTextureByName("top.png");
        playerTiles = RenderUtils.getTextureByName("player.png");
        enemyTiles = RenderUtils.getTextureByName("enemy00.png");
        bulletTiles = RenderUtils.getTextureByName("bullet.png");
        fxTiles = RenderUtils.getTextureByName("fx.png");
        try {
            crossHairCursor = CursorLoader.get().getCursor("tex/cursor00.png", 16, 16);
            Mouse.setNativeCursor(crossHairCursor);
        } catch (LWJGLException | IOException ex) {
            ex.printStackTrace();
        }

        // Shader laden
        System.out.println("INFO: GFX: Loading/compiling shaders...");
        shader = ShaderLoader.load();
    }

    /**
     * Wird bei jedem Frame aufgerufen, hier ist aller Rendercode.
     */
    @Override
    public void render() {

        // Player in der Mitte
        if (!lookahead) {
            panX = ((float) -GameClient.player.getX() + tilesX / 2.0f);
            panY = ((float) -GameClient.player.getY() + tilesY / 2.0f);
        } else {
            // Maus-Richtung von der Mitte aus:
            Vector vec = new Vector(mouseX - Display.getWidth() / 2, mouseY - Display.getHeight() / 2).invert().multiply(20f / Display.getHeight());
            panX = ((float) (-GameClient.player.getX() + tilesX / 2.0f + vec.x));
            panY = ((float) (-GameClient.player.getY() + tilesY / 2.0f + vec.y));
        }

        // Turret zeigt auf Maus
        GameClient.player.setTurretDir(GeoTools.toAngle(logicMouseX - GameClient.player.getX(), logicMouseY - GameClient.player.getY()));

        glClear(GL_STENCIL_BUFFER_BIT); // Stencil-Buffer löschen.
        glColor4f(1f, 1f, 1f, 1f);
        if (useVBOs) {
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glEnableClientState(GL_VERTEX_ARRAY);
            // Daten für VBO erstellen
            tvBuffer = BufferUtils.createFloatBuffer(8 + 12);
            ib = BufferUtils.createIntBuffer(1);
            glGenBuffersARB(ib);
            tvHandle = ib.get(0);

            // VBO aktivieren
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, tvHandle);
            //glBufferDataARB(GL_ARRAY_BUFFER_ARB, tvBuffer, GL_STATIC_DRAW_ARB);
            glTexCoordPointer(2, GL_FLOAT, 2 << 2, 0 << 2);
            glVertexPointer(3, GL_FLOAT, 3 << 2, 8 << 2);
        }
        // Boden und Wände zeichnen
        int[][] ground = GameClient.currentLevel.ground;
        byte[][] ground_random = GameClient.currentLevel.ground_randomize;
        // Boden zuerst
        groundTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        int dx1adr = ARBShaderObjects.glGetUniformLocationARB(shader[1], "tex1deltaX");
        int dy1adr = ARBShaderObjects.glGetUniformLocationARB(shader[1], "tex1deltaY");
        int dx2adr = ARBShaderObjects.glGetUniformLocationARB(shader[1], "tex2deltaX");
        int dy2adr = ARBShaderObjects.glGetUniformLocationARB(shader[1], "tex2deltaY");
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int tex = realTexAt(ground, ground_random, x, y);
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                if ((shadowLevel == 1 && shadow != 127) || !surroundingDark(GameClient.currentLevel.shadow, x, y) || shadowLevel == 0) {
                    int patRot = patternAt(ground, x, y);
                    int blendTex = blendTexAt(ground, x, y, patRot >> 4, patRot & 0x0F);
                    if (smoothGround && tex < 32 && blendTex >= 32 && (patRot >> 4) != 5) {
                        ARBShaderObjects.glUseProgramObjectARB(shader[1]);
                        int texX = tex % 16;
                        int texY = tex / 16;
                        int blendX = blendTex % 16;
                        int blendY = blendTex / 16;
                        int patX = (241 + (patRot >> 4)) % 16;
                        int patY = (241 + (patRot >> 4)) / 16;
                        ARBShaderObjects.glUniform1fARB(dx1adr, (blendX - texX) * 0.0625f);
                        ARBShaderObjects.glUniform1fARB(dy1adr, (blendY - texY) * 0.0625f);
                        ARBShaderObjects.glUniform1fARB(dx2adr, (patX - texX) * 0.0625f);
                        ARBShaderObjects.glUniform1fARB(dy2adr, (patY - texY) * 0.0625f);
                        drawUncoloredTilePanned(tex, x, y, patRot & 0x0F);
                        ARBShaderObjects.glUseProgramObjectARB(0);
                    } else {
                        // Normales Bodenzeichnen
                        drawUncoloredTilePanned(tex, x, y, 0);
                    }
                }
            }
        }
        glColor3f(1f, 1f, 1f);

        // Top rendern
        int[][] top = GameClient.currentLevel.top;
        byte[][] top_random = GameClient.currentLevel.top_randomize;
        topTiles.bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                if (((shadowLevel == 1 && shadow != 127) || !surroundingDark(GameClient.currentLevel.shadow, x, y) || shadowLevel == 0) && baseTexAt(top, x, y) != 0) {
                    int tex = realTexAt(top, top_random, x, y);
                    int patRot = patternAt(top, x, y);
                    if (fancyTop && ((patRot >> 4) != 5)) {
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
                        drawUncoloredTilePanned(241 + (patRot >> 4), x, y, rot);
                        glDisable(GL_ALPHA_TEST);
                        // Jetzt ist der Stencil-Buffer ok. Jetzt Modus auf "malen, wenn stencil das sagt" stellen
                        glStencilFunc(GL_NOTEQUAL, 0x0, 0x1);
                        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
                        // Jetzt Wand malen:
                        drawUncoloredTilePanned(tex, x, y, 0);
                        // Fertig, Stencil abschalten:
                        glDisable(GL_STENCIL_TEST);
                        // Zweite Maske drüber, für schönes Aussehen
                        drawUncoloredTilePanned(225 + (patRot >> 4), x, y, rot);
                    } else {
                        drawUncoloredTilePanned(tex, x, y, 0);
                    }
                }
            }
        }
        glColor4f(1f, 1f, 1f, 1f);

        if (useVBOs) {
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisableClientState(GL_VERTEX_ARRAY);


            // Unbinden
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);

            // Handles löschen
            ib.put(0, tvHandle);
            glDeleteBuffersARB(ib);
        }


        // Enemies zeichnen:
        enemyTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Enemy) {
                if (inSight(c) && !c.isInvisible()) {
                    Enemy enemy = (Enemy) c;

                    // Werte fürs Einfärben nehmen und rendern
                    EnemyTypeStats ets = GameClient.enemytypes.getEnemytypelist().get(enemy.getEnemytypeid());
                    glColor4f(ets.color_red, ets.color_green, ets.color_blue, ets.color_alpha);
                    renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
                    glColor3f(1f, 1f, 1f);
                }
            }
        }

        // Players zeichnen:
        playerTiles.bind();
        for (LogicPlayer p : GameClient.players.values()) {
            PlayerCharacter player = p.getPlayer();
            if (player != null && inSight(player) && !p.isDead()) {
                renderAnim(player.getRenderObject().getBaseAnim(), player.getX(), player.getY(), player.getDir(), 0);
                renderAnim(player.getTurretRenderObject().getBaseAnim(), player.getX(), player.getY(), player.getTurretDir(), 0);
            }
        }

        // Namen einblenden?
        for (LogicPlayer p : GameClient.players.values()) {
            PlayerCharacter player = p.getPlayer();
            if (player != null && inSight(player) && !p.isDead()) {
                if ((showNickNames == 1 && mouseOverChar(player)) || showNickNames == 2) {
                    TextWriter.renderTextXCentered(p.getNickName(), (float) player.getX() + panX, (float) player.getY() + panY - 1.5f);
                }
            }
        }
        glColor4f(1f, 1f, 1f, 1f);

        // Bullets zeichnen
        bulletTiles.bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Bullet && inSight(c)) {
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
                if (f.getOwner() != null) {
                    renderAnim(f.getAnim(), f.getOwner().getX(), f.getOwner().getY(), f.getAnim().getDirection(), f.getStarttick());
                } else {
                    renderAnim(f.getAnim(), f.getX(), f.getY(), f.getAnim().getDirection(), f.getStarttick());
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
                TextWriter.renderText(String.valueOf(d.getDamage()), (float) d.getX() + panX, (float) d.getY() + panY + height, 1f, .1f, .2f, visibility);
            }
        }
        glColor4f(1f, 1f, 1f, 1f);

        // Shadow zeichnen:
        if (shadowLevel > 0) {
            if (shadowLevel == 1) {
                int lastShadow = -1;
                glDisable(GL_TEXTURE_2D);
                for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
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
            } else if (shadowLevel == 2) {
                byte[][] shadowMap = GameClient.currentLevel.shadow;
                // Neue smooth-Schatten:
                glDisable(GL_TEXTURE_2D);
                for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                        int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                        // 4 Schattenpunkte
                        float lo, lu, ro, ru;
                        // Default-Schatten
                        lo = (shadowAt(shadowMap, x - 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x - 1, y) + shadow) * 0.0019685039f;
                        lu = (shadowAt(shadowMap, x - 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x - 1, y) + shadow) * 0.0019685039f;
                        ro = (shadowAt(shadowMap, x + 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x + 1, y) + shadow) * 0.0019685039f;
                        ru = (shadowAt(shadowMap, x + 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x + 1, y) + shadow) * 0.0019685039f;
                        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        glColor4f(0f, 0f, 0f, lo);
                        glVertex3f(x + panX, y + 1 + panY, 0);
                        glColor4f(0f, 0f, 0f, ro);
                        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        glColor4f(0f, 0f, 0f, ru);
                        glVertex3f(x + 1 + panX, y + panY, 0);
                        glColor4f(0f, 0f, 0f, lu);
                        glVertex3f(x + panX, y + panY, 0);
                        glEnd();
                    }
                }
                glEnable(GL_TEXTURE_2D);
            } else if (shadowLevel == 3) {
                glDisable(GL_TEXTURE_2D);
                glColor4f(0f, 0f, 0f, 1f);
                //boolean shaderActive = false;
                // Werte precachen:
                float xFact = 1f / (DefaultSettings.CLIENT_GFX_RES_X / (DefaultSettings.CLIENT_GFX_TILESIZE * zoomFactor)) * DefaultSettings.CLIENT_GFX_RES_X;
                float yFact = 1f / (DefaultSettings.CLIENT_GFX_RES_Y / (DefaultSettings.CLIENT_GFX_TILESIZE * zoomFactor)) * DefaultSettings.CLIENT_GFX_RES_Y;
                int pixelPerSpriteAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "pixelPerSprite");
                int loAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "shadowLO");
                int luAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "shadowLU");
                int roAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "shadowRO");
                int ruAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "shadowRU");
                int bxAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "bx");
                int byAdr = ARBShaderObjects.glGetUniformLocationARB(shader[0], "by");
                ARBShaderObjects.glUseProgramObjectARB(shader[0]);
                // Shader vorkonfigurieren
                ARBShaderObjects.glUniform1fARB(pixelPerSpriteAdr, lookahead ? DefaultSettings.CLIENT_GFX_RES_Y / 20f : DefaultSettings.CLIENT_GFX_RES_Y / 34f);
                ARBShaderObjects.glUseProgramObjectARB(0);
                byte[][] shadowMap = GameClient.currentLevel.shadow;
                // Renderloop
                for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                        int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                        // Schatten an 4 Punkten ausrechnen
                        float lo, lu, ro, ru;
                        lo = (shadowAt(shadowMap, x - 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x - 1, y) + shadow) * 0.0019685039f;
                        lu = (shadowAt(shadowMap, x - 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x - 1, y) + shadow) * 0.0019685039f;
                        ro = (shadowAt(shadowMap, x + 1, y + 1) + shadowAt(shadowMap, x, y + 1) + shadowAt(shadowMap, x + 1, y) + shadow) * 0.0019685039f;
                        ru = (shadowAt(shadowMap, x + 1, y - 1) + shadowAt(shadowMap, x, y - 1) + shadowAt(shadowMap, x + 1, y) + shadow) * 0.0019685039f;
                        boolean shaderOn = false;
                        // Schatten überall gleich?
                        if (lo == lu && lu == ro && ro == ru) {
                            glColor4f(0f, 0f, 0f, lu);
                        } else {
                            shaderOn = true;
                            // Muss mit Shader gezeichnet werden:
                            ARBShaderObjects.glUseProgramObjectARB(shader[0]);

                            // Shader konfigurieren
                            ARBShaderObjects.glUniform1fARB(loAdr, lo);
                            ARBShaderObjects.glUniform1fARB(luAdr, lu);
                            ARBShaderObjects.glUniform1fARB(roAdr, ro);
                            ARBShaderObjects.glUniform1fARB(ruAdr, ru);
                            ARBShaderObjects.glUniform1fARB(bxAdr, (x + panX) * xFact);
                            ARBShaderObjects.glUniform1fARB(byAdr, (y + panY) * yFact);
                        }
                        glBegin(GL_QUADS);
                        glVertex3f(x + panX, y + 1 + panY, 0);
                        glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        glVertex3f(x + 1 + panX, y + panY, 0);
                        glVertex3f(x + panX, y + panY, 0);
                        glEnd();
                        if (shaderOn) {
                            ARBShaderObjects.glUseProgramObjectARB(0);
                        }
                    }
                }
                // Shader abschalten
                ARBShaderObjects.glUseProgramObjectARB(0);
                glEnable(GL_TEXTURE_2D);
            }
        }
        glColor4f(1f, 1f, 1f, 1f);
    }

    private static int realTexAt(int[][] layer, byte[][] random, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 1;
        } else {
            return layer[x][y] + random[x][y];
        }
    }

    private static int baseTexAt(int[][] layer, int x, int y) {
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

    private boolean inSight(Char c) {
        double x = c.getX();
        double y = c.getY();
        double size = c.getSize();
        return (x + size >= -panX && x - size <= -panX + tilesX && y + size >= -panY && y - size <= -panY + tilesY);
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
    private void renderAnim(Animation animation, double x, double y, double dir, int starttick) {
        float picsizex = 0.0625f * animation.getPicsizex();
        float picsizey = 0.0625f * animation.getPicsizey();

        int currentpic = ((GameClient.frozenGametick - starttick) / animation.getPicduration()) % animation.getNumberofpics();
        currentpic += animation.getStartpic();

        float v = (currentpic % (16 / animation.getPicsizex())) * picsizex;
        float w = (currentpic / (16 / animation.getPicsizey())) * picsizey;
        float onepixel = 1.0f / 512; // einen pixel vom Bild in jede Richtung abschneiden

        glPushMatrix();
        glTranslated(x + panX, y + panY, 0);
        glRotated(dir / Math.PI * 180.0, 0, 0, 1);
        glTranslated(-(x + panX), -(y + panY), 0);
        glBegin(GL_QUADS);
        glTexCoord2f(v + onepixel, w + picsizey - onepixel);
        glVertex3f((float) x + panX - 1, (float) y + panY + 1, 0);
        glTexCoord2f(v + picsizex - onepixel, w + picsizey - onepixel);
        glVertex3f((float) x + panX + 1, (float) y + panY + 1, 0);
        glTexCoord2f(v + picsizex - onepixel, w + onepixel);
        glVertex3f((float) x + panX + 1, (float) y + panY - 1, 0);
        glTexCoord2f(v + onepixel, w + onepixel);
        glVertex3f((float) x + panX - 1, (float) y + panY - 1, 0);
        glEnd();
        glPopMatrix();
    }

    /**
     * Zeichnet ein Tile einer Tilemap an die gegebenen Position.
     * Die Position sind dabei absolute Spielkoordinaten, das aktuelle Scrollen wird automatisch eingerechnet.
     *
     * @param tile Tilenummer auf der Tilemap
     * @param x Koordinate auf der Map, die mitgescrollt wird
     * @param y Koordinate auf der Map, die mitgescrollt wird
     * @param numRot Rotation (0-3) in Schritten von 90° Winkel
     */
    private void drawUncoloredTilePanned(int tile, float x, float y, int numRot) {
        int tx = tile % 16;
        int ty = tile / 16;

        if (useVBOs) {
            //tvBuffer.position(0);
            // Textur-Koordinaten
            tvBuffer.put(tx * 0.0625f + 0.001953125f).put(ty * 0.0625f + 0.001953125f);
            tvBuffer.put(tx * 0.0625f + 0.060546875f).put(ty * 0.0625f + 0.001953125f);
            tvBuffer.put(tx * 0.0625f + 0.060546875f).put(ty * 0.0625f + 0.060546875f);
            tvBuffer.put(tx * 0.0625f + 0.001953125f).put(ty * 0.0625f + 0.060546875f);

            // Vertex-Koordinaten
            tvBuffer.put(x + panX).put(y + 1 + panY).put(0);
            tvBuffer.put(x + 1 + panX).put(y + 1 + panY).put(0);
            tvBuffer.put(x + 1 + panX).put(y + panY).put(0);
            tvBuffer.put(x + panX).put(y + panY).put(0);
            tvBuffer.flip();

            // Daten zur Grafikkarte hochladen
            glBufferDataARB(GL_ARRAY_BUFFER_ARB, tvBuffer, GL_STREAM_DRAW_ARB);

            // Zeichnen
            glDrawArrays(GL_QUADS, 0, 4);
        } else {
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
    }

    /**
     * Zeichnet ein Tile einer Tilemap an die gegebenen Position und färbt es ein.
     * Die Position sind dabei absolute Spielkoordinaten, das aktuelle Scrollen wird automatisch eingerechnet.
     *
     * @param tile Tilenummer auf der Tilemap
     * @param x Koordinate auf der Map, die mitgescrollt wird
     * @param y Koordinate auf der Map, die mitgescrollt wird
     * @param numRot Rotation (0-3) in Schritten von 90° Winkel
     * @param colLU Farbe der unteren, linken Ecke
     * @param colLO Farbe der oberen, linken Ecke
     * @param colRU Farbe der untern, rechten Ecke
     * @param colRO Farbe der oberen, rechten Ecke
     */
    private void drawColoredTilePanned(int tile, float x, float y, int numRot, int colLU, int colLO, int colRU, int colRO) {
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
        int myTex = baseTexAt(tex, x, y);
        if (myTex >= 2) {
            return patternRotationLookupTable[(2 <= baseTexAt(tex, x + 1, y) ? 1 : 0) | (2 <= baseTexAt(tex, x + 1, y - 1) ? 2 : 0) | (2 <= baseTexAt(tex, x, y - 1) ? 4 : 0) | (2 <= baseTexAt(tex, x - 1, y - 1) ? 8 : 0) | (2 <= baseTexAt(tex, x - 1, y) ? 16 : 0) | (2 <= baseTexAt(tex, x - 1, y + 1) ? 32 : 0) | (2 <= baseTexAt(tex, x, y + 1) ? 64 : 0) | (2 <= baseTexAt(tex, x + 1, y + 1) ? 128 : 0)];
        } else {
            return patternRotationLookupTable[(myTex == baseTexAt(tex, x + 1, y) ? 1 : 0) | (myTex == baseTexAt(tex, x + 1, y - 1) ? 2 : 0) | (myTex == baseTexAt(tex, x, y - 1) ? 4 : 0) | (myTex == baseTexAt(tex, x - 1, y - 1) ? 8 : 0) | (myTex == baseTexAt(tex, x - 1, y) ? 16 : 0) | (myTex == baseTexAt(tex, x - 1, y + 1) ? 32 : 0) | (myTex == baseTexAt(tex, x, y + 1) ? 64 : 0) | (myTex == baseTexAt(tex, x + 1, y + 1) ? 128 : 0)];
        }
    }

    /**
     * Liest in Abhängigkeit von dem gegebenen Pattern und Rotation die Nachbartextur aus.
     */
    private int blendTexAt(int[][] tex, int x, int y, int pattern, int rot) {
        switch (pattern) {
            case 0:
            case 1:
            case 2:
            case 3:
                // oben (ohne Rotation)
                switch (rot) {
                    case 0:
                        return baseTexAt(tex, x, y + 1);
                    case 1:
                        return baseTexAt(tex, x - 1, y);
                    case 2:
                        return baseTexAt(tex, x, y - 1);
                    case 3:
                        return baseTexAt(tex, x + 1, y);
                }
                break;
            case 4:
                // rechts (ohne Rotation)
                switch (rot) {
                    case 0:
                        return baseTexAt(tex, x + 1, y);
                    case 1:
                        return baseTexAt(tex, x, y + 1);
                    case 2:
                        return baseTexAt(tex, x - 1, y);
                    case 3:
                        return baseTexAt(tex, x, y - 1);
                }
                break;
            case 5:
            // Kein Pattern (=egal)
            case 6:
            case 7:
            case 8:
            case 9:
                // oben (ohne Rotation)
                switch (rot) {
                    case 0:
                        return baseTexAt(tex, x, y + 1);
                    case 1:
                        return baseTexAt(tex, x - 1, y);
                    case 2:
                        return baseTexAt(tex, x, y - 1);
                    case 3:
                        return baseTexAt(tex, x + 1, y);
                }
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                // rechts oben (ohne Rotation)
                switch (rot) {
                    case 0:
                        return baseTexAt(tex, x + 1, y + 1);
                    case 1:
                        return baseTexAt(tex, x - 1, y + 1);
                    case 2:
                        return baseTexAt(tex, x - 1, y - 1);
                    case 3:
                        return baseTexAt(tex, x + 1, y - 1);
                }
        }
        return 0; // sollte nie erreicht werden, falls doch findet man so hoffentlich den Fehler
    }

    private boolean surroundingDark(byte[][] shadowMap, int x, int y) {
        return (shadowAt(shadowMap, x, y) == 127 && shadowAt(shadowMap, x - 1, y - 1) == 127 && shadowAt(shadowMap, x, y - 1) == 127 && shadowAt(shadowMap, x + 1, y - 1) == 127 && shadowAt(shadowMap, x - 1, y) == 127 && shadowAt(shadowMap, x + 1, y) == 127 && shadowAt(shadowMap, x - 1, y + 1) == 127 && shadowAt(shadowMap, x, y + 1) == 127 && shadowAt(shadowMap, x + 1, y + 1) == 127);
    }

    /**
     * Findet heraus, ob der Mauszeiger derzeit über dem gegebenen Char schwebt.
     *
     * @param c der zu untersuchende Char
     * @return true, wenn drüber, sonst false
     */
    private boolean mouseOverChar(Char c) {
        return (logicMouseX >= c.getX() - c.getSize() && logicMouseX <= c.getX() + c.getSize() && logicMouseY >= c.getY() - c.getSize() && logicMouseY <= c.getY() + c.getSize());
    }

    /**
     * @return the shadowLevel
     */
    public int getShadowLevel() {
        return shadowLevel;
    }

    /**
     * @param shadowLevel the shadowLevel to set
     */
    public void setShadowLevel(int shadowLevel) {
        this.shadowLevel = shadowLevel;
    }

    /**
     * @return the smoothGround
     */
    public boolean isSmoothGround() {
        return smoothGround;
    }

    /**
     * @param smoothGround the smoothGround to set
     */
    public void setSmoothGround(boolean smoothGround) {
        this.smoothGround = smoothGround;
    }

    /**
     * @return the fancyTop
     */
    public boolean isFancyTop() {
        return fancyTop;
    }

    /**
     * @param fancyTop the fancyTop to set
     */
    public void setFancyTop(boolean fancyTop) {
        this.fancyTop = fancyTop;
    }

    /**
     * @return the lookAhead
     */
    public boolean isLookAhead() {
        return lookahead;
    }

    /**
     * @param lookAhead the lookAhead to set
     */
    public void setLookAhead(boolean lookAhead) {
        if (lookAhead) {
            setZoomFact(Display.getHeight() / 20.0f / CLIENT_GFX_TILESIZE);
        } else {
            setZoomFact(Display.getHeight() / 34.0f / CLIENT_GFX_TILESIZE);
        }
        this.lookahead = lookAhead;
    }

    /**
     * @return the showNickNames
     */
    public int getShowNickNames() {
        return showNickNames;
    }

    /**
     * @param showNickNames the showNickNames to set
     */
    public void setShowNickNames(int showNickNames) {
        this.showNickNames = showNickNames;
    }

    /**
     * @param useVBOs the useVBOs to set
     */
    public void setUseVBOs(boolean useVBOs) {
        this.useVBOs = useVBOs;
    }

    @Override
    public void defineOpenGLMatrices() {
        if (!DefaultSettings.CLIENT_GFX_LOOKAHEAD) {
            // Zoom korrekt berechnen. Man sieht immer 58 * 34 Felder weit.
            // Höhe hat Prio, bei 4:3 sieht man weniger...
            setZoomFact(CLIENT_GFX_RES_Y / 34.0f / CLIENT_GFX_TILESIZE);
        } else {
            // Bei Lookahead sieht man weniger weit, weil man ja die Ansicht verschieben kann.
            setZoomFact(CLIENT_GFX_RES_Y / 20.f / CLIENT_GFX_TILESIZE);
        }
    }

    /**
     * Liest den zoomFactor aus.
     *
     * @return der zoomFactor
     * @deprecated
     */
    @Deprecated
    public float getZoomFact() {
        return zoomFactor;
    }

    /**
     * Setzt den Zoomfaktor.
     * TODO: Erst mal private machen, dann aber bald ganz rauswerfen
     *
     * @param zoomFact
     */
    @Deprecated
    public void setZoomFact(float zoomFact) {
        glLoadIdentity();
        GLU.gluOrtho2D(0f, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact), 0f, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
        tilesX = CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact);
        tilesY = CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact);
        zoomFactor = zoomFact;
    }

    /**
     * Setzt die Mausposition, notwendig, weil sich die Ansicht der Maus anpasst.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     */
    public void setMouseXY(double x, double y) {
        mouseX = x;
        mouseY = y;
        // Maus updaten:
        logicMouseX = (1f * x / Display.getWidth() * tilesX) - panX;
        logicMouseY = (1f * y / Display.getHeight() * tilesY) - panY;
    }
}
