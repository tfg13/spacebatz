package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.graphics.ShaderLoader;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import de._13ducks.spacebatz.shared.DefaultSettings;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
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
        // Ortho-Projektionsmatrix aufmachen:
        Matrix4f projection = new Matrix4f();
        projection.m00 = 1f / 10f;
        projection.m03 = -1f;
        projection.m11 = (1f * DefaultSettings.CLIENT_GFX_RES_Y / DefaultSettings.CLIENT_GFX_RES_X) / 10f;
        projection.m13 = -1f;
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
    }
}
