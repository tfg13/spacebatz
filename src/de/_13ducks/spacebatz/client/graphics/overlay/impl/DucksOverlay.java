package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import java.io.IOException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Zeigt das 13ducks-Logo an.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DucksOverlay extends Overlay {

    private VAO background;
    private Texture logoTex;
    private VAO logo;

    public DucksOverlay() {
        try {
            float[] white = new float[]{1f, 1f, 1f, 1f};
            background = VAOFactory.createStaticColoredRectVAO(1);
            background.pushRectC(0, 0, DefaultSettings.CLIENT_GFX_RES_X, DefaultSettings.CLIENT_GFX_RES_Y, white, white, white, white);
            background.upload();
            logoTex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/noautoload/13ducks.png"));
            logo = VAOFactory.createStaticTexturedRectVAO(1);
            logo.pushRectT(DefaultSettings.CLIENT_GFX_RES_X / 2 - 241, DefaultSettings.CLIENT_GFX_RES_Y / 2 - 256, 512, 512, 0, 0, 1, 1);
            logo.upload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void render() {
        background.render();
        logoTex.bind();
        logo.render();
        Display.update(false);
    }

    /**
     * Das Overlay braucht man irgendwann nichtmehr, dann aufräumen.
     */
    public void destroy() {
        background.destroy();
        logo.destroy();
        logoTex.release();
    }
}
