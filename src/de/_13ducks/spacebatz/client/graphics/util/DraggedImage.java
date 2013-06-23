package de._13ducks.spacebatz.client.graphics.util;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author mekhar
 */
public class DraggedImage extends VisibleGUIElement {

    private VAO vao;
    private int width, height;
    Texture texture;
    int tile;
    float sourceX, sourceY, sourceWidth, sourceHeight;
    int mouseX, mouseY;

    public DraggedImage(int width, int height, String textureName, int tile, int tileWidth, int tileHeight) {
        vao = VAOFactory.IOnlyWantToDrawATile(0, 0, 0, 0, textureName, tile, tileWidth, tileHeight);
        texture = RenderUtils.getTextureByName("skilltree.png");
        this.tile = tile;
        sourceX = RenderUtils.getSourceXForTile(texture, tile, 32);
        sourceY = RenderUtils.getSourceYForTile(texture, tile, 32);
        sourceWidth = RenderUtils.getSourceWidthForTile(texture, tile, 32);
        sourceHeight = RenderUtils.getSourceHeightForTile(texture, tile, 32);
        this.width = width;
        this.height = height;
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
        vao.resetData();
        vao.pushRectT(mouseX, mouseY, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        vao.upload();
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
        sourceX = RenderUtils.getSourceXForTile(texture, tile, 32);
        sourceY = RenderUtils.getSourceYForTile(texture, tile, 32);
        sourceWidth = RenderUtils.getSourceWidthForTile(texture, tile, 32);
        sourceHeight = RenderUtils.getSourceHeightForTile(texture, tile, 32);
    }
}
