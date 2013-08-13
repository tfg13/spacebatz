package de._13ducks.spacebatz.client.graphics.vao;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Zeichnet eine Tile von einer Textur. Die Textur- und Renderkoordinaten können verändert werden.
 *
 * @author mekhar
 */
public class DynamicTexturedRectangleVAO extends VAO {

    private int x, y, width, height;
    private float sourceX, sourceY, sourceWidth, sourceHeight;
    private String textureName;

    protected DynamicTexturedRectangleVAO(int x, int y, int width, int height, String textureName, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        super(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, VAOFactory.blockShaderModifications ? -1 : VAOFactory.setShaderColTexModeAdr);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureName = textureName;
        this.sourceX = (float) sourceX / RenderUtils.getTextureWidth(textureName);
        this.sourceY = (float) sourceY / RenderUtils.getTextureHeight(textureName);
        this.sourceWidth = (float) sourceWidth / RenderUtils.getTextureWidth(textureName);
        this.sourceHeight = (float) sourceHeight / RenderUtils.getTextureHeight(textureName);
        pushRectT(x, y, width, height, this.sourceX, this.sourceY, this.sourceWidth, this.sourceHeight);
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
