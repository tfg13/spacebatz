package de._13ducks.spacebatz.client.graphics.vao;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.newdawn.slick.opengl.Texture;

/**
 * Zeichnet eine Tile von einer Textur. Die Textur- und Renderkoordinaten können verändert werden.
 *
 * @author mekhar
 */
public class DynamicTileVAO extends VAO {

    private int x, y, width, height;
    private float sourceX, sourceY, sourceWidth, sourceHeight;
    private int tileSize;
    private Texture texture;

    protected DynamicTileVAO(int x, int y, int width, int height, String textureName, int tile, int tileSize) {
        super(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, VAOFactory.blockShaderModifications ? -1 : VAOFactory.setShaderColTexModeAdr);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.texture = RenderUtils.getTextureByName(textureName);
        sourceX = RenderUtils.getSourceXForTile(texture, tile, tileSize);
        sourceY = RenderUtils.getSourceYForTile(texture, tile, tileSize);
        sourceWidth = RenderUtils.getSourceWidthForTile(texture, tile, tileSize);
        sourceHeight = RenderUtils.getSourceHeightForTile(texture, tile, tileSize);
        pushRectT(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        upload();
    }

    public void setSourceTile(int newTile) {
        resetData();
        sourceX = RenderUtils.getSourceXForTile(texture, newTile, tileSize);
        sourceY = RenderUtils.getSourceYForTile(texture, newTile, tileSize);
        sourceWidth = RenderUtils.getSourceWidthForTile(texture, newTile, tileSize);
        sourceHeight = RenderUtils.getSourceHeightForTile(texture, newTile, tileSize);
        pushRectT(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        upload();
    }

    public void setRenderPosition(int x, int y) {
        resetData();
        this.x = x;
        this.y = y;
        pushRectT(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        upload();
    }

    public void setRenderSize(int width, int height) {
        resetData();
        this.width = width;
        this.height = height;
        pushRectT(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        upload();
    }

    @Override
    public void render() {
        RenderUtils.setTilemap(texture);
        super.render();
    }
}
