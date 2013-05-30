package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.graphics.ShaderLoader;
import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;

/**
 * Neuer Haupt-Renderer, arbeitet ausschließlich mit OpenGL >=3.2,
 * verwendet das Core-Profil ohne Abwärtskompatibilität
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OpenGL32CoreRenderer extends CoreRenderer {

    private int[] shader;

    @Override
    public void setupShaders() {
        // Shader laden, compilen, linken
        shader = ShaderLoader.load();

    }

    @Override
    public void render() {
    }
}
