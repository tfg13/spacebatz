package de._13ducks.spacebatz.client.graphics.util;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author mekhar
 */
public class DraggedImage extends VisibleGUIElement {

    private DynamicTileVAO vao;
    Texture texture;
    int tile;
    int mouseX, mouseY;

    public DraggedImage(int width, int height, String textureName, int tile, int tileWidth) {
        vao = VAOFactory.IOnlyWantToDrawATile(0, 0, 0, 0, textureName, tile, tileWidth);
        texture = RenderUtils.getTextureByName("skilltree.png");
        this.tile = tile;
        vao.setRenderSize(width, height);
        isVisible = false;

    }

    @Override
    public void mouseMove(int mx, int my) {
        mouseX = mx;
        mouseY = my;
    }

    @Override
    public void renderElement() {
        RenderUtils.setTilemap(texture);
        vao.setRenderPosition(mouseX, mouseY);
        vao.render();
    }

    @Override
    public void mousePressed(float x, float y, int button) {
    }

    @Override
    public void mouseReleased(float x, float y, int button) {
    }

    @Override
    public void keyboardInput(int key, boolean down) {
    }

    public void setTile(int tile) {
        this.tile = tile;
        vao.setSourceTile(tile);
    }
}
