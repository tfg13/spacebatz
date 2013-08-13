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
import de._13ducks.spacebatz.client.graphics.GraphicsEngine;
import de._13ducks.spacebatz.client.graphics.LegacyShaderLoader;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.input.impl.GameInput;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.Iterator;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

/**
 * Old renderer, uses immediate mode and only commands <= OpenGL 2.1
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class LegacyRenderer extends CoreRenderer {

    private float panX = 0;
    private float panY = 0;
    private float tilesX = 1f * DefaultSettings.CLIENT_GFX_RES_X / DefaultSettings.CLIENT_GFX_RES_Y * 20f;
    private float tilesY = 20f;
    private int[] shader;
    private int showNickNames = 1;
    /*
     * Settings
     */
    private int shadowLevel = 3;
    private boolean smoothGround = true;
    private boolean fancyTop = true;
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

    @Override
    public void setupShaders(int[] shader) {
        // Do not use system shaders...
        this.shader = LegacyShaderLoader.load();
        GL11.glClearStencil(0); // Wert von Stencil-Clear auf 0 setzen.
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0f, tilesX, 0f, tilesY);
    }

    @Override
    public void reEnableShader() {
        GL20.glUseProgram(0);
    }

    @Override
    public void render() {
        if (GameClient.player == null || GameClient.currentLevel == null) {
            // Level noch nicht geladen, abbruch
            return;
        }
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Stencil-Buffer löschen.
        GL11.glColor4f(1f, 1f, 1f, 1f);
        // Boden und Wände zeichnen
        int[][] ground = GameClient.currentLevel.ground;
        byte[][] ground_random = GameClient.currentLevel.ground_randomize;
        // Boden zuerst
        RenderUtils.getTextureByName("ground.png").bind(); // groundTiles-Textur wird jetzt verwendet
        int dx1adr = GL20.glGetUniformLocation(shader[1], "tex1deltaX");
        int dy1adr = GL20.glGetUniformLocation(shader[1], "tex1deltaY");
        int dx2adr = GL20.glGetUniformLocation(shader[1], "tex2deltaX");
        int dy2adr = GL20.glGetUniformLocation(shader[1], "tex2deltaY");
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int tex = realTexAt(ground, ground_random, x, y);
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                if ((shadowLevel == 1 && shadow != 127) || !surroundingDark(GameClient.currentLevel.shadow, x, y) || shadowLevel == 0) {
                    int patRot = patternAt(ground, x, y);
                    int blendTex = blendTexAt(ground, x, y, patRot >> 4, patRot & 0x0F);
                    if (smoothGround && tex < 32 && blendTex >= 32 && (patRot >> 4) != 5) {
                        GL20.glUseProgram(shader[1]);
                        int texX = tex % 16;
                        int texY = tex / 16;
                        int blendX = blendTex % 16;
                        int blendY = blendTex / 16;
                        int patX = (241 + (patRot >> 4)) % 16;
                        int patY = (241 + (patRot >> 4)) / 16;
                        GL20.glUniform1f(dx1adr, (blendX - texX) * 0.0625f);
                        GL20.glUniform1f(dy1adr, (blendY - texY) * 0.0625f);
                        GL20.glUniform1f(dx2adr, (patX - texX) * 0.0625f);
                        GL20.glUniform1f(dy2adr, (patY - texY) * 0.0625f);
                        drawUncoloredTilePanned(tex, x, y, patRot & 0x0F);
                        GL20.glUseProgram(0);
                    } else {
                        // Normales Bodenzeichnen
                        drawUncoloredTilePanned(tex, x, y, 0);
                    }
                }
            }
        }
        GL11.glColor3f(1f, 1f, 1f);

        // Top rendern
        int[][] top = GameClient.currentLevel.top;
        byte[][] top_random = GameClient.currentLevel.top_randomize;
        RenderUtils.getTextureByName("top.png").bind(); // groundTiles-Textur wird jetzt verwendet
        for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
            for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                if (((shadowLevel == 1 && shadow != 127) || !surroundingDark(GameClient.currentLevel.shadow, x, y) || shadowLevel == 0) && baseTexAt(top, x, y) != 0) {
                    int tex = realTexAt(top, top_random, x, y);
                    int patRot = patternAt(top, x, y);
                    if (fancyTop && ((patRot >> 4) != 5)) {
                        int rot = patRot & 0x0F;
                        // Bild im Stencil-Buffer erzeugen:
                        GL11.glEnable(GL11.GL_STENCIL_TEST); // Stenciling ist an
                        // Stencil-Test schlägt immer fehl, malt also nix. Aber alle verwendeten Pixel erhöhen den Stencil-Buffer:
                        GL11.glStencilFunc(GL11.GL_NEVER, 0x0, 0x0);
                        GL11.glStencilOp(GL11.GL_INCR, GL11.GL_INCR, GL11.GL_INCR);
                        // Pixel dem Alpha nach ignorieren
                        GL11.glAlphaFunc(GL11.GL_NOTEQUAL, 0f);
                        GL11.glEnable(GL11.GL_ALPHA_TEST);
                        // Pattern malen, beeinflusst Stencil-Buffer
                        drawUncoloredTilePanned(241 + (patRot >> 4), x, y, rot);
                        GL11.glDisable(GL11.GL_ALPHA_TEST);
                        // Jetzt ist der Stencil-Buffer ok. Jetzt Modus auf "malen, wenn stencil das sagt" stellen
                        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 0x0, 0x1);
                        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
                        // Jetzt Wand malen:
                        drawUncoloredTilePanned(tex, x, y, 0);
                        // Fertig, Stencil abschalten:
                        GL11.glDisable(GL11.GL_STENCIL_TEST);
                        // Zweite Maske drüber, für schönes Aussehen
                        drawUncoloredTilePanned(225 + (patRot >> 4), x, y, rot);
                    } else {
                        drawUncoloredTilePanned(tex, x, y, 0);
                    }
                }
            }
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);


        // Enemies zeichnen:
        RenderUtils.getTextureByName("enemy00.png").bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Enemy) {
                if (inSight(c) && !c.isInvisible()) {
                    Enemy enemy = (Enemy) c;

                    // Werte fürs Einfärben nehmen und rendern
                    EnemyTypeStats ets = GameClient.enemytypes.getEnemytypelist().get(enemy.getEnemytypeid());
                    GL11.glColor4f(ets.color_red, ets.color_green, ets.color_blue, ets.color_alpha);
                    renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
                    GL11.glColor3f(1f, 1f, 1f);
                }
            }
        }

        // Players zeichnen:
        RenderUtils.getTextureByName("player.png").bind();
        for (LogicPlayer p : GameClient.players.values()) {
            PlayerCharacter player = p.getPlayer();
            if (player != null && inSight(player) && !p.isDead()) {
                renderAnim(player.getRenderObject().getBaseAnim(), player.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick), player.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick), player.getDir(), 0);
                renderAnim(player.getTurretRenderObject().getBaseAnim(), player.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick), player.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick), player.getTurretDir(), 0);
            }
        }

        // Namen einblenden?
        for (LogicPlayer p : GameClient.players.values()) {
            PlayerCharacter player = p.getPlayer();
            if (player != null && inSight(player) && !p.isDead()) {
                if ((showNickNames == 1 && mouseOverChar(player)) || showNickNames == 2) {
                    TextWriter.renderTextXCentered(p.getNickName(), toPixelCoordsX((float) player.getX() + panX), toPixelCoordsY((float) player.getY() + panY - 1.5f));
                }
            }
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);

        // Bullets zeichnen
        RenderUtils.getTextureByName("bullet.png").bind();
        for (Char c : GameClient.netIDMap.values()) {
            if (c instanceof Bullet && inSight(c)) {
                renderAnim(c.getRenderObject().getBaseAnim(), c.getX(), c.getY(), c.getDir(), 0);
            }
        }

        // Schadenszahlen zeichnen
        Iterator<DamageNumber> iter = GameClient.getEngine().getGraphics().damageNumberIterator();
        while (iter.hasNext()) {
            DamageNumber d = iter.next();
            if (Engine.getTime() > d.getSpawntime() + CompileTimeParameters.CLIENT_GFX_DAMAGENUMBER_LIFETIME) {
                // alt - > löschen
                iter.remove();
            } else {
                //rendern:
                float height = (Engine.getTime() - d.getSpawntime()) / 250.0f;
                float visibility = 1 - ((float) (Engine.getTime() - d.getSpawntime())) / CompileTimeParameters.CLIENT_GFX_DAMAGENUMBER_LIFETIME; // Anteil der vergangenen Zeit an der Gesamtlebensdauer
                visibility = Math.min(visibility * 2, 1); // bis 0.5 * lifetime: visibility 1, dann linear auf 0
                TextWriter.renderText(String.valueOf(d.getDamage()), toPixelCoordsX((float) d.getX() + panX), toPixelCoordsY((float) d.getY() + panY + height), 1f, .1f, .2f, visibility);
            }
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);

        RenderUtils.getTextureByName("fx.png").bind();
        Iterator<Fx> itera = GameClient.getEngine().getGraphics().fxIterator();
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

        // Shadow zeichnen:
        if (shadowLevel > 0) {
            if (shadowLevel == 1) {
                int lastShadow = -1;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                for (int x = -(int) (1 + panX); x < -(1 + panX) + tilesX + 2; x++) {
                    for (int y = -(int) (1 + panY); y < -(1 + panY) + tilesY + 2; y++) {
                        int shadow = shadowAt(GameClient.currentLevel.shadow, x, y);
                        if (shadow != lastShadow) {
                            GL11.glColor4f(0f, 0f, 0f, 0.0078740157f * shadow);
                            lastShadow = shadow;
                        }
                        GL11.glBegin(GL11.GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        GL11.glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                        GL11.glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        GL11.glVertex3f(x + 1 + panX, y + panY, 0);
                        GL11.glVertex3f(x + panX, y + panY, 0);
                        GL11.glEnd(); // Zeichnen des QUADs fertig
                    }
                }
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            } else if (shadowLevel == 2) {
                byte[][] shadowMap = GameClient.currentLevel.shadow;
                // Neue smooth-Schatten:
                GL11.glDisable(GL11.GL_TEXTURE_2D);
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
                        GL11.glBegin(GL11.GL_QUADS); // QUAD-Zeichenmodus aktivieren
                        GL11.glColor4f(0f, 0f, 0f, lo);
                        GL11.glVertex3f(x + panX, y + 1 + panY, 0);
                        GL11.glColor4f(0f, 0f, 0f, ro);
                        GL11.glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        GL11.glColor4f(0f, 0f, 0f, ru);
                        GL11.glVertex3f(x + 1 + panX, y + panY, 0);
                        GL11.glColor4f(0f, 0f, 0f, lu);
                        GL11.glVertex3f(x + panX, y + panY, 0);
                        GL11.glEnd();
                    }
                }
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            } else if (shadowLevel == 3) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(0f, 0f, 0f, 1f);
                //boolean shaderActive = false;
                // Werte precachen:
                float xFact = 1f / (DefaultSettings.CLIENT_GFX_RES_X / (DefaultSettings.CLIENT_GFX_RES_Y / 20f)) * DefaultSettings.CLIENT_GFX_RES_X;
                float yFact = 1f / (DefaultSettings.CLIENT_GFX_RES_Y / (DefaultSettings.CLIENT_GFX_RES_Y / 20f)) * DefaultSettings.CLIENT_GFX_RES_Y;
                int pixelPerSpriteAdr = GL20.glGetUniformLocation(shader[0], "pixelPerSprite");
                int loAdr = GL20.glGetUniformLocation(shader[0], "shadowLO");
                int luAdr = GL20.glGetUniformLocation(shader[0], "shadowLU");
                int roAdr = GL20.glGetUniformLocation(shader[0], "shadowRO");
                int ruAdr = GL20.glGetUniformLocation(shader[0], "shadowRU");
                int bxAdr = GL20.glGetUniformLocation(shader[0], "bx");
                int byAdr = GL20.glGetUniformLocation(shader[0], "by");
                GL20.glUseProgram(shader[0]);
                // Shader vorkonfigurieren
                GL20.glUniform1f(pixelPerSpriteAdr, DefaultSettings.CLIENT_GFX_RES_Y / 20f);
                GL20.glUseProgram(0);
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
                            GL11.glColor4f(0f, 0f, 0f, lu);
                        } else {
                            shaderOn = true;
                            // Muss mit Shader gezeichnet werden:
                            GL20.glUseProgram(shader[0]);

                            // Shader konfigurieren
                            GL20.glUniform1f(loAdr, lo);
                            GL20.glUniform1f(luAdr, lu);
                            GL20.glUniform1f(roAdr, ro);
                            GL20.glUniform1f(ruAdr, ru);
                            GL20.glUniform1f(bxAdr, (x + panX) * xFact);
                            GL20.glUniform1f(byAdr, (y + panY) * yFact);
                        }
                        GL11.glBegin(GL11.GL_QUADS);
                        GL11.glVertex3f(x + panX, y + 1 + panY, 0);
                        GL11.glVertex3f(x + 1 + panX, y + 1 + panY, 0);
                        GL11.glVertex3f(x + 1 + panX, y + panY, 0);
                        GL11.glVertex3f(x + panX, y + panY, 0);
                        GL11.glEnd();
                        if (shaderOn) {
                            GL20.glUseProgram(0);
                        }
                    }
                }
                // Shader abschalten
                GL20.glUseProgram(0);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
        }
    }

    @Override
    public void setMouseXY(double mouseX, double mouseY) {
        if (GameClient.player == null) {
            // Level noch nicht geladen, abbruch
            return;
        }
        double playerX = GameClient.player.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick);
        double playerY = GameClient.player.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick);
        // Neuen Sichtmittelpunkt bestimmen:
        Vector vec = new Vector(mouseX - Display.getWidth() / 2f, mouseY - Display.getHeight() / 2f).invert().multiply(20f / Display.getHeight());
        panX = ((float) (-playerX + (1f * Display.getWidth() / Display.getHeight() * 20f) / 2.0f + vec.x));
        panY = ((float) (-playerY + 20 / 2.0f + vec.y));
        // Turret zeigt auf Maus
        GameClient.player.setTurretDir(GeoTools.toAngle(((1f * mouseX / Display.getWidth() * (1f * Display.getWidth() / Display.getHeight() * 20f)) - GameClient.getEngine().getGraphics().getPanX()) - GameClient.player.getX(), ((1f * mouseY / Display.getHeight() * 20f) - GameClient.getEngine().getGraphics().getPanY()) - GameClient.player.getY()));
    }

    @Override
    public double getPanX() {
        return panX;
    }

    @Override
    public double getPanY() {
        return panY;
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
     * Findet heraus, ob der Mauszeiger derzeit über dem gegebenen Char schwebt.
     *
     * @param c der zu untersuchende Char
     * @return true, wenn drüber, sonst false
     */
    private boolean mouseOverChar(Char c) {
        return (GameInput.getLogicMouseX() >= c.getX() - c.getSize() && GameInput.getLogicMouseX() <= c.getX() + c.getSize() && GameInput.getLogicMouseY() >= c.getY() - c.getSize() && GameInput.getLogicMouseY() <= c.getY() + c.getSize());
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
        float[][] tileCoords = new float[2][4]; // X, Y dann 4 Ecken
        tileCoords[0][0] = tx * 0.0625f + 0.001953125f;
        tileCoords[0][1] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][2] = tx * 0.0625f + 0.060546875f;
        tileCoords[0][3] = tx * 0.0625f + 0.001953125f;
        tileCoords[1][0] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][1] = ty * 0.0625f + 0.001953125f;
        tileCoords[1][2] = ty * 0.0625f + 0.060546875f;
        tileCoords[1][3] = ty * 0.0625f + 0.060546875f;
        GL11.glBegin(GL11.GL_QUADS); // QUAD-Zeichenmodus aktivieren
        GL11.glTexCoord2f(tileCoords[0][(0 + numRot) % 4], tileCoords[1][(0 + numRot) % 4]); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
        GL11.glVertex3f(x + panX, y + 1 + panY, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
        // Die weiteren 3 Ecken im Uhrzeigersinn:
        GL11.glTexCoord2f(tileCoords[0][(1 + numRot) % 4], tileCoords[1][(1 + numRot) % 4]);
        GL11.glVertex3f(x + 1 + panX, y + 1 + panY, 0);
        GL11.glTexCoord2f(tileCoords[0][(2 + numRot) % 4], tileCoords[1][(2 + numRot) % 4]);
        GL11.glVertex3f(x + 1 + panX, y + panY, 0);
        GL11.glTexCoord2f(tileCoords[0][(3 + numRot) % 4], tileCoords[1][(3 + numRot) % 4]);
        GL11.glVertex3f(x + panX, y + panY, 0);
        GL11.glEnd(); // Zeichnen des QUADs fertig
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

        GL11.glPushMatrix();
        GL11.glTranslated(x + panX, y + panY, 0);
        GL11.glRotated(dir / Math.PI * 180.0, 0, 0, 1);
        GL11.glTranslated(-(x + panX), -(y + panY), 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(v + onepixel, w + picsizey - onepixel);
        GL11.glVertex3f((float) x + panX - 1, (float) y + panY + 1, 0);
        GL11.glTexCoord2f(v + picsizex - onepixel, w + picsizey - onepixel);
        GL11.glVertex3f((float) x + panX + 1, (float) y + panY + 1, 0);
        GL11.glTexCoord2f(v + picsizex - onepixel, w + onepixel);
        GL11.glVertex3f((float) x + panX + 1, (float) y + panY - 1, 0);
        GL11.glTexCoord2f(v + onepixel, w + onepixel);
        GL11.glVertex3f((float) x + panX - 1, (float) y + panY - 1, 0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private float toPixelCoordsX(float input) {
        return input / tilesX * DefaultSettings.CLIENT_GFX_RES_X;
    }

    private float toPixelCoordsY(float input) {
        return input / tilesY * DefaultSettings.CLIENT_GFX_RES_Y;
    }
}
