package de._13ducks.spacebatz.client.graphics.vao;

import static de._13ducks.spacebatz.client.graphics.vao.VAOFactory.blockShaderModifications;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Zeichnet ein Rechteck.
 *
 * @author mekhar
 */
public class DynamicRectangleVAO extends VAO {

    private int x, y, width, height;
    private float[] color;

    protected DynamicRectangleVAO(int x, int y, int width, int height, float[] color) {
        super(6, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : VAOFactory.setShaderColTexModeAdr);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (color.length != 4) {
            throw new IllegalArgumentException("RGBA du honk");
        }
        this.color = color;
        pushRectC(x, y, width, height, color, color, color, color);
        upload();
    }

    public void setRenderPosition(int x, int y) {
        resetData();
        this.x = x;
        this.y = y;
        pushRectC(x, y, width, height, color, color, color, color);
        upload();
    }

    public void setRenderSize(int width, int height) {
        resetData();
        this.width = width;
        this.height = height;
        pushRectC(x, y, width, height, color, color, color, color);
        upload();
    }

    @Override
    public void render() {
        super.render();
    }
}
