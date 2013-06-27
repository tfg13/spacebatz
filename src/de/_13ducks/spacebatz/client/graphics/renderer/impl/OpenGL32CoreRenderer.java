package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.GraphicsEngine;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.input.impl.GameInput;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.util.FloatBufferBuilder;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Neuer Haupt-Renderer, arbeitet ausschließlich mit OpenGL >=3.2, verwendet das Core-Profil ohne Abwärtskompatibilität
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OpenGL32CoreRenderer extends CoreRenderer {

    /**
     * Index der Adresse der Projections/ViewMatrix im Array mit Uniformadressen.
     */
    private static final int INDEX_VERT_PROJECTIONVIEW = 0;
    /**
     * Enthält die ProgramIDs aller verwendeten Shader. Diese sind bereits fertig gelinkt etc. und können direkt verwendet werden. Index der Adresse der Modelmatrix im Array mit
     * Uniformadressen.
     */
    private static final int INDEX_VERT_MODEL = 1;
    /**
     * Enthält die ProgramIDs aller verwendeten Shader. Diese sind bereits fertig gelinkt etc. und können direkt verwendet werden.
     */
    private int[] shader;
    /**
     * Adressen der Uniforms.
     */
    private int[] shaderUniformAdr = new int[2];
    /**
     * Projektions-Matrix, bleibt normalerweise immer gleich.
     */
    private Matrix4f projectionMatrix;
    /**
     * View-Matrix, beschreibt Position und Orientierung der Kamera.
     */
    private Matrix4f viewMatrix;
    /**
     * Speichert die zu den Chunks gehörenden VAOs. Arrays: X Y {vao, vbo}
     */
    private int[][][] groundChunkVAOs; // = new int[GameClient.currentLevel.getSizeX() / 8][GameClient.currentLevel.getSizeY() / 8][2];
    /**
     * Speichert die zu den Chunks gehörenden VAOs. Arrays: X Y {vao, vbo, numberoftriangles}
     */
    private int[][][] topChunkVAOs;//= new int[GameClient.currentLevel.getSizeX() / 8][GameClient.currentLevel.getSizeY() / 8][3];
    /**
     * VAO und VBO für Gegner. Mit Color und Drehung.
     */
    private VAO testPlayer;

    @Override
    public void setupShaders(int[] shader) {
        this.shader = shader;
        GL20.glUseProgram(shader[GraphicsEngine.SHADER_INDEX_GAME]);
        //GL11.glViewport(0, 0, DefaultSettings.CLIENT_GFX_RES_X, DefaultSettings.CLIENT_GFX_RES_Y);
        GL11.glClearColor(.4f, .6f, .9f, 0f);
        // Ortho-Projektionsmatrix aufmachen:
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = (1f * DefaultSettings.CLIENT_GFX_RES_Y / DefaultSettings.CLIENT_GFX_RES_X) / 10f;
        projectionMatrix.m30 = -1f;
        projectionMatrix.m11 = 1f / 10f;
        projectionMatrix.m31 = -1f;
        projectionMatrix.m22 = -1f;
        // View und Model erstmal Identity
        viewMatrix = new Matrix4f();
        Matrix4f model = new Matrix4f();
        // Projection und View ändern sich selten (und nicht während eines Frames), vormultiplizieren um dem Shader Zeit zu sparen.
        Matrix4f projectionView = new Matrix4f();
        Matrix4f.mul(projectionMatrix, viewMatrix, projectionView);
        // Daten zum Shader hochladen
        FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
        // ProjectionView
        projectionView.store(matrix44Buffer);
        matrix44Buffer.flip();
        shaderUniformAdr[INDEX_VERT_PROJECTIONVIEW] = GL20.glGetUniformLocation(shader[GraphicsEngine.SHADER_INDEX_GAME], "projectionViewM");
        GL20.glUniformMatrix4(shaderUniformAdr[INDEX_VERT_PROJECTIONVIEW], false, matrix44Buffer);
        // Model
        model.store(matrix44Buffer);
        matrix44Buffer.flip();
        shaderUniformAdr[INDEX_VERT_MODEL] = GL20.glGetUniformLocation(shader[GraphicsEngine.SHADER_INDEX_GAME], "modelM");
        GL20.glUniformMatrix4(shaderUniformAdr[INDEX_VERT_MODEL], false, matrix44Buffer);
    }

    @Override
    public void render() {
        if (GameClient.player == null || GameClient.currentLevel == null) {
            // Level noch nicht geladen, abbruch
            return;
        }
        int playerX = (int) (GameClient.player.getX()) / 8;
        int playerY = (int) (GameClient.player.getY()) / 8;

        // Ground
        RenderUtils.getTextureByName("ground.png").bind();
        for (int x = playerX - 4; x <= playerX + 4; x++) {
            if (x < 0 || x >= groundChunkVAOs.length) {
                continue;
            }
            for (int y = playerY - 2; y <= playerY + 2; y++) {
                if (y < 0 || y >= groundChunkVAOs[0].length) {
                    continue;
                }
                if (groundChunkVAOs[x][y][0] == 0) {
                    createGroundChunk(x, y);
                }
                renderChunk(groundChunkVAOs[x][y][0], 6 * 8 * 8);
            }
        }

        // Top
        RenderUtils.getTextureByName("top.png").bind();
        for (int x = playerX - 4; x <= playerX + 4; x++) {
            if (x < 0 || x >= topChunkVAOs.length) {
                continue;
            }
            for (int y = playerY - 2; y <= playerY + 2; y++) {
                if (y < 0 || y >= topChunkVAOs[0].length) {
                    continue;
                }
                if (topChunkVAOs[x][y][0] == 0) {
                    createTopChunk(x, y);
                }
                renderChunk(topChunkVAOs[x][y][0], topChunkVAOs[x][y][2]);
            }
        }

        if (testPlayer == null) {
            createPlayerVBO(GameClient.player.netID);
        }
        // Player
        updateVBOs();
        RenderUtils.getTextureByName("player.png").bind();
        // Drehen:
        pushRotMatrix((float) GameClient.player.getDir(), (float) GameClient.player.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick), (float) GameClient.player.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick));
        testPlayer.render();
        restoreRot();

        // Enemys
        RenderUtils.getTextureByName("enemy00.png").bind();
        updateEnemyVAOs();
        renderEnemyVAOs();
    }

    /**
     * Schickt eine Rotationsmatrix an den Vertex-Shader.
     *
     * @param rotation rotation, in Bogenmaß, übliche zählweise
     */
    private void pushRotMatrix(float rotation, float centerx, float centery) {
        // Rotationszentrum verschieben:
        Matrix4f trans = new Matrix4f();
        trans.m30 = centerx;
        trans.m31 = centery;
        // XY-Rotationsmatrix bauen
        Matrix4f rot = new Matrix4f();
        rot.m00 = (float) Math.cos(rotation);
        rot.m11 = (float) Math.cos(rotation);
        rot.m10 = (float) -Math.sin(rotation);
        rot.m01 = (float) Math.sin(rotation);
        // Multiplizieren
        rot = Matrix4f.mul(trans, rot, null);
        trans.m30 *= -1;
        trans.m31 *= -1;
        rot = Matrix4f.mul(rot, trans, null);
        // Zur Grafikkarte hochladen:
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        rot.store(buffer);
        buffer.flip();
        GL20.glUniformMatrix4(shaderUniformAdr[INDEX_VERT_MODEL], false, buffer);
    }

    /**
     * Setzt die Rotationsmatrix des Shaders zurück
     */
    private void restoreRot() {
        // Identität laden:
        Matrix4f identity = new Matrix4f();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        identity.store(buffer);
        buffer.flip();
        GL20.glUniformMatrix4(shaderUniformAdr[INDEX_VERT_MODEL], false, buffer);
    }

    private void renderChunk(int chunkVAOAdr, int numberOfTriangles) {
        // Render
        GL30.glBindVertexArray(chunkVAOAdr);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        // Zeichnen
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numberOfTriangles);
        // Aufräumen
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void createPlayerVBO(int netID) {
        PlayerCharacter pl = (PlayerCharacter) GameClient.netIDMap.get(netID);
        Animation anim = pl.getRenderObject().getBaseAnim();

        VAOFactory.setBlockShaderModifications(true);
        testPlayer = VAOFactory.createDynamicTexturedRectVAO();
        VAOFactory.setBlockShaderModifications(false);

        float picsizex = 0.0625f * anim.getPicsizex();
        float picsizey = 0.0625f * anim.getPicsizey();
        int currentpic = (GameClient.frozenGametick / anim.getPicduration()) % anim.getNumberofpics();
        currentpic += anim.getStartpic();
        float v = (currentpic % (16 / anim.getPicsizex())) * picsizex;
        float w = (currentpic / (16 / anim.getPicsizey())) * picsizey;
        float onepixel = 1.0f / 256; // einen pixel vom Bild in jede Richtung abschneiden

        testPlayer.pushRectT((float) pl.getX() - 1, (float) pl.getY() - 1, 2, 2, v + onepixel, w + onepixel, picsizex, picsizey);

        testPlayer.upload();
    }

    /**
     * Baut einen VAO/VBO für einen Chunk. Falls es schon einen gibt, wird der alte gelöscht und ein neuer gebaut.
     *
     * @param chunkX X-Koordinate
     * @param chunkY Y-Koordinate
     */
    private void createGroundChunk(int chunkX, int chunkY) {
        System.out.println("(Re-)creating ground chunk for " + chunkX + " " + chunkY);
        if (groundChunkVAOs[chunkX][chunkY][0] != 0) {
            removeChunkVAO(groundChunkVAOs, chunkX, chunkY);
        }
        // Daten für neuen VBO:
        FloatBuffer vtBuffer = BufferUtils.createFloatBuffer((12 + 12) * 8 * 8);
        // Vertex-Koordinaten
        for (int x = 0; x < 8; x++) {
            int rx = chunkX * 8 + x;
            for (int y = 0; y < 8; y++) {
                int ry = chunkY * 8 + y;
                vtBuffer.put(rx).put(ry);
                vtBuffer.put(rx).put(ry + 1);
                vtBuffer.put(rx + 1).put(ry);
                vtBuffer.put(rx).put(ry + 1);
                vtBuffer.put(rx + 1).put(ry);
                vtBuffer.put(rx + 1).put(ry + 1);
            }
        }
        // Textur-Koordinaten
        for (int x = 0; x < 8; x++) {
            int rx = chunkX * 8 + x;
            for (int y = 0; y < 8; y++) {
                int ry = chunkY * 8 + y;
                int tex = realTexAt(GameClient.currentLevel.ground, GameClient.currentLevel.ground_randomize, rx, ry);
                int texX = tex % 16;
                int texY = tex / 16;
                vtBuffer.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.060546875f);
                vtBuffer.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.001953125f);
                vtBuffer.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.060546875f);
                vtBuffer.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.001953125f);
                vtBuffer.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.060546875f);
                vtBuffer.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.001953125f);
            }
        }
        vtBuffer.flip();
        // VAO erstellen und mit VBO verbinden
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // VBO erstellen
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vtBuffer, GL15.GL_STATIC_DRAW);
        // Daten-Links setzen
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, (12 * 8 * 8) << 2);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Speichern:
        groundChunkVAOs[chunkX][chunkY][0] = vao;
        groundChunkVAOs[chunkX][chunkY][1] = vbo;
    }

    /**
     * Baut einen VAO/VBO für einen Chunk. Falls es schon einen gibt, wird der alte gelöscht und ein neuer gebaut.
     *
     * @param chunkX X-Koordinate
     * @param chunkY Y-Koordinate
     */
    private void createTopChunk(int chunkX, int chunkY) {
        System.out.println("(Re-)creating top chunk for " + chunkX + " " + chunkY);
        if (topChunkVAOs[chunkX][chunkY][0] != 0) {
            removeChunkVAO(topChunkVAOs, chunkX, chunkY);
        }
        // Daten für neuen VBO:
        FloatBufferBuilder vtData = new FloatBufferBuilder((12 + 12) * 8 * 8);
        // Vertex-Koordinaten
        for (int x = 0; x < 8; x++) {
            int rx = chunkX * 8 + x;
            for (int y = 0; y < 8; y++) {
                int ry = chunkY * 8 + y;
                if (baseTexAt(GameClient.currentLevel.top, rx, ry) != 0) { // Luft, nichts malen
                    vtData.put(rx).put(ry);
                    vtData.put(rx).put(ry + 1);
                    vtData.put(rx + 1).put(ry);
                    vtData.put(rx).put(ry + 1);
                    vtData.put(rx + 1).put(ry);
                    vtData.put(rx + 1).put(ry + 1);
                }
            }
        }
        // Textur-Koordinaten
        for (int x = 0; x < 8; x++) {
            int rx = chunkX * 8 + x;
            for (int y = 0; y < 8; y++) {
                int ry = chunkY * 8 + y;
                if (baseTexAt(GameClient.currentLevel.top, rx, ry) != 0) { // Luft, nichts malen
                    int tex = realTexAt(GameClient.currentLevel.top, GameClient.currentLevel.top_randomize, rx, ry);
                    int texX = tex % 16;
                    int texY = tex / 16;
                    vtData.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.060546875f);
                    vtData.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.001953125f);
                    vtData.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.060546875f);
                    vtData.put(texX * 0.0625f + 0.001953125f).put(texY * 0.0625f + 0.001953125f);
                    vtData.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.060546875f);
                    vtData.put(texX * 0.0625f + 0.060546875f).put(texY * 0.0625f + 0.001953125f);
                }
            }
        }
        // VAO erstellen und mit VBO verbinden
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // VBO erstellen
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vtData.toBuffer(), GL15.GL_STATIC_DRAW);
        // Daten-Links setzen
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, ((vtData.size() / (12 + 12)) * 12) << 2);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Speichern:
        topChunkVAOs[chunkX][chunkY][0] = vao;
        topChunkVAOs[chunkX][chunkY][1] = vbo;
        topChunkVAOs[chunkX][chunkY][2] = vtData.size();
    }

    private void removeChunkVAO(int[][][] chunkList, int chunkX, int chunkY) {
        // VAO und enthaltenenen VBO löschen
        GL30.glBindVertexArray(chunkList[chunkX][chunkY][0]);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(chunkList[chunkX][chunkY][1]);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(chunkList[chunkX][chunkY][0]);
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

    public void setLevelSize(int chunksX, int chunksY) {
        groundChunkVAOs = new int[chunksX][chunksY][2];
        this.topChunkVAOs = new int[chunksX][chunksY][3];

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
        Vector vec = new Vector(mouseX - Display.getWidth() / 2, mouseY - Display.getHeight() / 2).invert().multiply(20f / Display.getHeight());
        float panX = ((float) (-playerX + (Display.getWidth() / Display.getHeight() * 20) / 2.0f + vec.x));
        float panY = ((float) (-playerY + 20 / 2.0f + vec.y));
        if (viewMatrix.m30 != panX || viewMatrix.m31 != panY) {
            // View-Matrix updaten:
            viewMatrix.m30 = panX;
            viewMatrix.m31 = panY;
            // Zur Grafikkarte hochladen:
            FloatBuffer vmBuffer = BufferUtils.createFloatBuffer(16);
            Matrix4f.mul(projectionMatrix, viewMatrix, null).store(vmBuffer);
            vmBuffer.flip();
            GL20.glUniformMatrix4(shaderUniformAdr[INDEX_VERT_PROJECTIONVIEW], false, vmBuffer);
        }
        // Turret zeigt auf Maus
        GameClient.player.setTurretDir(GeoTools.toAngle(GameInput.getLogicMouseX() - GameClient.player.getX(), GameInput.getLogicMouseY() - GameClient.player.getY()));
    }

    public void chunkReceived(int chunkX, int chunkY) {
        if (groundChunkVAOs[chunkX][chunkY][0] != 0) {
            // Neu berechnen (komplett)
            createGroundChunk(chunkX, chunkY);
            createTopChunk(chunkX, chunkY);
        }
    }

    public void minorTopChange(int x, int y) {
        if (topChunkVAOs[x / 8][y / 8][0] != 0) {
            // Neu berechnen (nur top)
            createTopChunk(x / 8, y / 8);
        }
    }

    @Override
    public double getPanX() {
        return viewMatrix.m30;
    }

    @Override
    public double getPanY() {
        return viewMatrix.m31;
    }

    @Override
    public void reEnableShader() {
        GL20.glUseProgram(shader[GraphicsEngine.SHADER_INDEX_GAME]);
    }

    private void updateVBOs() {
        PlayerCharacter pl = GameClient.player;
        Animation anim = pl.getRenderObject().getBaseAnim();

        float picsizex = 0.0625f * anim.getPicsizex();
        float picsizey = 0.0625f * anim.getPicsizey();
        int currentpic = (GameClient.frozenGametick / anim.getPicduration()) % anim.getNumberofpics();
        currentpic += anim.getStartpic();
        float v = (currentpic % (16 / anim.getPicsizex())) * picsizex;
        float w = (currentpic / (16 / anim.getPicsizey())) * picsizey;
        float onepixel = 1.0f / 256; // einen pixel vom Bild in jede Richtung abschneiden

        testPlayer.resetData();
        testPlayer.pushRectT((float) pl.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick) - 1, (float) pl.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick) - 1, 2, 2, v + onepixel, w + onepixel, picsizex - (2 * onepixel), picsizey - 2 * (onepixel));
        testPlayer.upload();
    }

    private void updateEnemyVAOs() {
        for (Char c : GameClient.netIDMap.values()) {
            // Hässliches instanceof, sollte weg
            if (c instanceof Enemy) {
                if (c.getRenderObject().getVao() == null) {
                    c.getRenderObject().setVao(VAOFactory.createDynamicTexturedRectVAO());
                }
                VAO vao = c.getRenderObject().getVao();
                Animation animation = c.getRenderObject().getBaseAnim();
                vao.resetData();
                float picsizex = 0.0625f * animation.getPicsizex();
                float picsizey = 0.0625f * animation.getPicsizey();

                int currentpic = ((GameClient.frozenGametick) / animation.getPicduration()) % animation.getNumberofpics();
                currentpic += animation.getStartpic();

                float v = (currentpic % (16 / animation.getPicsizex())) * picsizex;
                float w = (currentpic / (16 / animation.getPicsizey())) * picsizey;
                float onepixel = 1.0f / 512; // einen pixel vom Bild in jede Richtung abschneiden

                vao.pushRectT((float) c.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick) - 1, (float) c.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick) - 1, 2f, 2f, v + onepixel, w + onepixel, picsizex - (2 * onepixel), picsizey - (2 * onepixel));
                vao.upload();
            }
        }
    }

    private void renderEnemyVAOs() {
        for (Char c : GameClient.netIDMap.values()) {
            // Hässliches instanceof, sollte weg
            if (c instanceof Enemy) {
                pushRotMatrix((float) c.getDir(), (float) c.getSubtickedX(GraphicsEngine.SubTick.frozenSubTick), (float) c.getSubtickedY(GraphicsEngine.SubTick.frozenSubTick));
                c.getRenderObject().getVao().render();
                restoreRot();
            }
        }
    }
}
