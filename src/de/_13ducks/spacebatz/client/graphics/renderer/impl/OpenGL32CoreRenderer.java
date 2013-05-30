package de._13ducks.spacebatz.client.graphics.renderer.impl;

import de._13ducks.spacebatz.client.graphics.renderer.CoreRenderer;

/**
 * Neuer Haupt-Renderer, arbeitet ausschließlich mit OpenGL >=3.2,
 * verwendet das Core-Profil ohne Abwärtskompatibilität
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OpenGL32CoreRenderer extends CoreRenderer {

    @Override
    public void setupShaders() {
        System.out.println("AddMe: Setup shaders");
    }

    @Override
    public void render() {
    }

}
