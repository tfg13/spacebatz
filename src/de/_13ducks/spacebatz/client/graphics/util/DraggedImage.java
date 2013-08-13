package de._13ducks.spacebatz.client.graphics.util;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;

/**
 *
 * @author mekhar
 */
public class DraggedImage extends VisibleGUIElement {

    private DynamicTileVAO vao;
    String textureName;
    int tile;
    int mouseX, mouseY;

    public DraggedImage(int width, int height, String textureName, int tile, int tileWidth) {
        vao = VAOFactory.IOnlyWantToDrawATile(0, 0, 0, 0, textureName, tile, tileWidth);
        this.textureName = textureName;
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
        RenderUtils.bindTexture(textureName);
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
