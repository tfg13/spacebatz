package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import org.newdawn.slick.opengl.Texture;

/**
 * Zeigt den silltree an.
 *
 * @author michael
 */
public class SkillTreeControl extends Control {

    private Texture skilltreeTexture;

    public SkillTreeControl(Renderer renderer) {
        skilltreeTexture = renderer.getTextureByName("ground.png");
    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTexture(skilltreeTexture);
        renderer.drawImage(0, 0,0,10,10);
    }

    @Override
    public void input() {
    }
}
