package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.ShaderLoader;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.shared.DefaultSettings;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Neuer Haupt-Renderer, arbeitet ausschließlich mit OpenGL >=3.2,
 * verwendet das Core-Profil ohne Abwärtskompatibilität
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OpenGL32CoreRenderer extends CoreRenderer {

    /**
     * Enthält die ProgramIDs aller verwendeten Shader.
     * Diese sind bereits fertig gelinkt etc. und können direkt verwendet werden.
     */
    private int[] shader;
    /**
     * Speichert die zu den Chunks gehörenden VAOs.
     * Arrays: X Y {vao, vbo}
     */
    private int[][][] chunkVAOs = new int[GameClient.currentLevel.getSizeX() / 8][GameClient.currentLevel.getSizeY() / 8][2];

    @Override
    public void setupShaders() {
        // Shader laden, compilen, linken
        shader = ShaderLoader.load();
        GL20.glUseProgram(shader[0]);
        //GL11.glViewport(0, 0, DefaultSettings.CLIENT_GFX_RES_X, DefaultSettings.CLIENT_GFX_RES_Y);
        GL11.glClearColor(.4f, .6f, .9f, 0f);
        // Ortho-Projektionsmatrix aufmachen:
        Matrix4f projection = new Matrix4f();
        projection.m00 = (1f * DefaultSettings.CLIENT_GFX_RES_Y / DefaultSettings.CLIENT_GFX_RES_X) / 10f;
        projection.m30 = -1f;
        projection.m11 = 1f / 10f;
        projection.m31 = -1f;
        projection.m22 = -1f;
        // View und Model erstmal Identity
        Matrix4f view = new Matrix4f();
        Matrix4f model = new Matrix4f();
        // Projection und View ändern sich selten (und nicht während eines Frames), vormultiplizieren um dem Shader Zeit zu sparen.
        Matrix4f projectionView = new Matrix4f();
        Matrix4f.mul(projection, view, projectionView);
        // Daten zum Shader hochladen
        FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
        // ProjectionView
        projectionView.store(matrix44Buffer);
        matrix44Buffer.flip();
        int pvmloc = GL20.glGetUniformLocation(shader[0], "projectionViewM");
        GL20.glUniformMatrix4(pvmloc, false, matrix44Buffer);
        // Model
        model.store(matrix44Buffer);
        matrix44Buffer.flip();
        int mmloc = GL20.glGetUniformLocation(shader[0], "modelM");
        GL20.glUniformMatrix4(mmloc, false, matrix44Buffer);
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        RenderUtils.getTextureByName("ground.png").bind();

        int drawChunkX = 0, drawChunkY = 0;

        if (chunkVAOs[drawChunkX][drawChunkY][0] == 0) {
            createChunk(drawChunkX, drawChunkY);
        }
        // Render
        GL30.glBindVertexArray(chunkVAOs[drawChunkX][drawChunkY][0]);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        // Zeichnen
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6 * 8 * 8);

        // Aufräumen
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

    }

    /**
     * Baut einen VAO/VBO für einen Chunk.
     * Falls es schon einen gibt, wird der alte gelöscht und ein neuer gebaut.
     *
     * @param chunkX X-Koordinate
     * @param chunkY Y-Koordinate
     */
    private void createChunk(int chunkX, int chunkY) {
        System.out.println("(Re-)creating chunk for " + chunkX + " " + chunkY);
        if (chunkVAOs[chunkX][chunkY][0] != 0) {
            // VAO und enthaltenenen VBO löschen
            GL30.glBindVertexArray(chunkVAOs[chunkX][chunkY][0]);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(chunkVAOs[chunkX][chunkY][1]);
            GL30.glBindVertexArray(0);
            GL30.glDeleteVertexArrays(chunkVAOs[chunkX][chunkY][0]);
        }
        // Daten für neuen VBO:
        FloatBuffer vtBuffer = BufferUtils.createFloatBuffer((18 + 12) * 8 * 8);
        // Vertex-Koordinaten
        for (int x = 0; x < 8; x++) {
            int rx = chunkX * 8 + x;
            for (int y = 0; y < 8; y++) {
                int ry = chunkY * 8 + y;
                vtBuffer.put(rx).put(ry).put(0);
                vtBuffer.put(rx).put(ry + 1).put(0);
                vtBuffer.put(rx + 1).put(ry).put(0);
                vtBuffer.put(rx).put(ry + 1).put(0);
                vtBuffer.put(rx + 1).put(ry).put(0);
                vtBuffer.put(rx + 1).put(ry + 1).put(0);
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
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, (18 * 8 * 8) << 2);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Speichern:
        chunkVAOs[chunkX][chunkY][0] = vao;
        chunkVAOs[chunkX][chunkY][1] = vbo;
    }

    private static int realTexAt(int[][] layer, byte[][] random, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 1;
        } else {
            return layer[x][y] + random[x][y];
        }
    }

    public void setLevelSize(int chunksX, int chunksY) {
        chunkVAOs = new int[chunksX][chunksY][2];
    }

    @Override
    public void setMouseXY(double x, double y) {
    }
}
