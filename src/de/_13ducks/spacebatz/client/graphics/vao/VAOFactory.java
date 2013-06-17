package de._13ducks.spacebatz.client.graphics.vao;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Baut VAOs.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class VAOFactory {

    /**
     * Die Adresse, die an die VAOs gegeben wird. An dieser Adresse können die VAOs den Textur/Färbemodus des Shaders umschalten.
     */
    private static int setShaderColTexModeAdr = 0;
    /**
     * Wenn true, wird keine Uniformadresse an die VAOs gegeben, diese ändern dann auch keine Shader.
     */
    private static boolean blockShaderModifications = false;

    private VAOFactory() {
        // private, Utility-Class
    }

    /**
     * Setzt die Uniformadresse zum umschalten der Shader.
     * Muss einmal aufgerufen worden sein, bevor die Factory verwendet wird.
     * Wird normalerweise vom Init-Code der Grafikengine erledigt.
     *
     * @param setShaderColTexModeAdr die Adresse des Uniforms
     */
    public static void init(int setShaderColTexModeAdr) {
        VAOFactory.setShaderColTexModeAdr = setShaderColTexModeAdr;
    }

    public static VAO createStaticColoredRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, false, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticTexturedRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, true, false, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticTexturedColoredRectVAO(int numberOfRects) {
        return new VAO(numberOfRects * 6, true, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicColoredRectVAO() {
        return new VAO(6, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedRectVAO() {
        return new VAO(6, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedColoredRectVAO() {
        return new VAO(6, true, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticColoredLineVAO(int numberOfLines) {
        return new VAO(numberOfLines * 2, false, true, GL15.GL_STATIC_DRAW, GL11.GL_LINES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicColoredLineVAO() {
        return new VAO(2, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_LINES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, false, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticTexturedTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, false, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createStaticTexturedColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, true, GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, false, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, false, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedColoredTriangleVAO(int numberOfTriangles) {
        return new VAO(numberOfTriangles, true, true, GL15.GL_DYNAMIC_DRAW, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    /**
     * @return the blockShaderModifications
     */
    public static boolean isBlockShaderModifications() {
        return blockShaderModifications;
    }

    /**
     * Mit dieser Methode lassen sich VAOs erzeugen, die den ColTex-Mode des Fragmentshaders nicht verändern können.
     *
     * NICHT VERWENDEN, WENN MAN NICHT WEISS, WAS DAS IST.
     *
     * @param aBlockShaderModifications the blockShaderModifications to set
     */
    public static void setBlockShaderModifications(boolean aBlockShaderModifications) {
        blockShaderModifications = aBlockShaderModifications;
    }
}
