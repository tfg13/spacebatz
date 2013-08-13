package de._13ducks.spacebatz.client.graphics.vao;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Zeichnet eine Tile von einer Textur. Die Textur- und Renderkoordinaten können verändert werden.
 *
 * @author mekhar
 */
public class DynamicTileVAO extends VAO {

    private int x, y, width, height;
    private float sourceX, sourceY, sourceWidth, sourceHeight;
    private int tileSize;
    private String textureName;

    protected DynamicTileVAO(int x, int y, int width, int height, String textureName, int tile, int tileSize) {
        super(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, VAOFactory.blockShaderModifications ? -1 : VAOFactory.setShaderColTexModeAdr);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.textureName = textureName;
        sourceX = RenderUtils.getSourceXForTile(textureName, tile, tileSize);
        sourceY = RenderUtils.getSourceYForTile(textureName, tile, tileSize);
        sourceWidth = RenderUtils.getSourceWidthForTile(textureName, tile, tileSize);
        sourceHeight = RenderUtils.getSourceHeightForTile(textureName, tile, tileSize);
        pushRectT(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight);
        upload();
    }

    public void setSourceTile(int newTile) {
        resetData();
        sourceX = RenderUtils.getSourceXForTile(textureName, newTile, tileSize);
        sourceY = RenderUtils.getSourceYForTile(textureName, newTile, tileSize);
        sourceWidth = RenderUtils.getSourceWidthForTile(textureName, newTile, tileSize);
        sourceHeight = RenderUtils.getSourceHeightForTile(textureName, newTile, tileSize);
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
        RenderUtils.bindTexture(textureName);
        super.render();
    }
}
