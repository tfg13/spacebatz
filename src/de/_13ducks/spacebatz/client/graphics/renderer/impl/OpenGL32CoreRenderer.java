package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.graphics.ShaderLoader;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;
import org.lwjgl.opengl.GL20;

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
    }

    @Override
    public void render() {
    }
}
