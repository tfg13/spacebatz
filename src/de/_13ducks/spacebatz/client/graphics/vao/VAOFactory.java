package de._13ducks.spacebatz.client.graphics.vao;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Baut VAOs.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class VAOFactory {

    private VAOFactory() {
        // private, Utility-Class
    }

    public static VAO createStaticColoredRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, false, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createStaticTexturedRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, true, false, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createStaticTexturedColoredRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, true, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicColoredRectVAO() {
        return new VAO(6, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicTexturedRectVAO() {
        return new VAO(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicTexturedColoredRectVAO() {
        return new VAO(6, true, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createStaticColoredLineVAO(int numberOfLines) {
        return new VAO(numberOfLines * 2, false, true, GL15.GL_STATIC_DRAW, GL11.GL_LINES);
    }

    public static VAO createDynamicColoredLineVAO() {
        return new VAO(2, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_LINES);
    }

    public static VAO createStaticColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, false, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createStaticTexturedTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, false, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createStaticTexturedColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicTexturedTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }

    public static VAO createDynamicTexturedColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES);
    }
}
