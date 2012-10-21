package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import org.newdawn.slick.opengl.Texture;

/**
 * Zeigt den silltree an.
 *
 * @author michael
 */
public class SkillTreeControl extends Control {

    /**
     * Die Textur mit den Silltree-Symbolen.
     */
    private Texture skilltreeTexture;
    /**
     * Der Skilltree, der bestimmt was für Fähigkeiten verfügbar sind.
     */
    private SkillTree skilltree;

    /**
     * Initialisiert das SkilltreeControl.
     *
     * @param renderer
     */
    public SkillTreeControl(Renderer renderer) {
        skilltreeTexture = renderer.getTextureByName("skilltree.png");
        skilltree = new SkillTree();

    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTexture(skilltreeTexture);
        renderer.setImageSize(32, 32);
        renderer.drawImage(0, 50, 50, 10, 10);
        renderer.drawImage(1, 50, 60, 10, 10);
        renderer.drawImage(2, 50, 70, 10, 10);
        renderer.drawImage(3, 50, 80, 10, 10);
    }

    @Override
    public void input() {
    }
}
