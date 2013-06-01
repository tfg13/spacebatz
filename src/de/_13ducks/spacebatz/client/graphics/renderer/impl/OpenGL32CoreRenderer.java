package de._13ducks.spacebatz.client.graphics.renderer.impl;

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

        int tx = 1;
        int ty = 2;
        int x = 8;
        int y = 8;
        FloatBuffer tvBuffer = BufferUtils.createFloatBuffer(8 + 12);

        // Vertex-Koordinaten
        tvBuffer.put(x).put(y).put(0);
        tvBuffer.put(x).put(y + 1).put(0);
        tvBuffer.put(x + 1).put(y).put(0);
        tvBuffer.put(x + 1).put(y + 1).put(0);

        // Texturkoordinaten:
        tvBuffer.put(tx * 0.0625f + 0.001953125f).put(ty * 0.0625f + 0.060546875f);
        tvBuffer.put(tx * 0.0625f + 0.001953125f).put(ty * 0.0625f + 0.001953125f);
        tvBuffer.put(tx * 0.0625f + 0.060546875f).put(ty * 0.0625f + 0.060546875f);
        tvBuffer.put(tx * 0.0625f + 0.060546875f).put(ty * 0.0625f + 0.001953125f);

        tvBuffer.flip();

        // VAO erstellen
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // VBO erstellen
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tvBuffer, GL15.GL_STATIC_DRAW);
        // Daten-Links setzen
        //GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 << 2, 0 << 2);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 12 << 2);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

    }
    int vaoId;
    int vboId;

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        RenderUtils.getTextureByName("ground.png").bind();

        // Render
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        // Zeichnen
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        // Aufräumen
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

    }
}
