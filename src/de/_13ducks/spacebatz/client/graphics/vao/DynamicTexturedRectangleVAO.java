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
public class DynamicTexturedRectangleVAO extends VAO {

    private int x, y, width, height;
    private float sourceX, sourceY, sourceWidth, sourceHeight;
    private Texture texture;

    protected DynamicTexturedRectangleVAO(int x, int y, int width, int height, String textureName, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        super(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, VAOFactory.blockShaderModifications ? -1 : VAOFactory.setShaderColTexModeAdr);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = RenderUtils.getTextureByName(textureName);
        this.sourceX = (float) sourceX / (float) texture.getImageWidth();
        this.sourceY = (float) sourceY / (float) texture.getImageHeight();
        this.sourceWidth = (float) sourceWidth / (float) texture.getImageWidth();
        this.sourceHeight = (float) sourceHeight / (float) texture.getImageHeight();
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
        RenderUtils.setTilemap(texture);
        super.render();
    }
}
