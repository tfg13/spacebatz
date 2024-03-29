package de._13ducks.spacebatz.client.graphics.vao;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
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
    protected static int setShaderColTexModeAdr = 0;
    /**
     * Wenn true, wird keine Uniformadresse an die VAOs gegeben, diese ändern dann auch keine Shader.
     */
    protected static boolean blockShaderModifications = false;

    public static DynamicTexturedRectangleVAO createDynamicTexturedRectangleVAO(int x, int y, int width, int height, String textureName, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        return new DynamicTexturedRectangleVAO(x, y, width, height, textureName, sourceX, sourceY, sourceWidth, sourceHeight);
    }

    private VAOFactory() {
        // private, Utility-Class
    }

    /**
     * Setzt die Uniformadresse zum umschalten der Shader. Muss einmal aufgerufen worden sein, bevor die Factory verwendet wird. Wird normalerweise vom Init-Code der Grafikengine
     * erledigt.
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
        return new BufferedVAO(6, false, true, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedRectVAO() {
        return new BufferedVAO(6, true, false, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
    }

    public static VAO createDynamicTexturedColoredRectVAO() {
        return new BufferedVAO(6, true, true, GL11.GL_TRIANGLES, blockShaderModifications ? -1 : setShaderColTexModeAdr);
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
     * Erzeugt ein VAO das eine Tile von einer Textur auf den Bildschirm rendert.
     *
     * @param x X-Position
     * @param y Y-Position
     * @param width Breite
     * @param height Höhe
     * @param textureName Name der Textur die gerendert werden soll ( "item.png" oder "player.png" )
     * @param tile Die Tile die gerendert werden soll
     * @param tileSize Die Breite der Tiles auf der Textur in Pixel
     * @return eine fertig initialisierete VAO die direkt gerendert werden kann.
     */
    public static DynamicTileVAO IOnlyWantToDrawATile(int x, int y, int width, int height, String textureName, int tile, int tileSize) {
        return new DynamicTileVAO(x, y, width, height, textureName, tile, tileSize);
    }

    /**
     * Erzeugt ein VAO das einen Teil einer Textur auf den Bildschirm rendert.
     *
     * @param textureName Der Name der Textur ( "item.png" )
     * @param targetX X-Koordinate
     * @param targetY Y-Koordinate
     * @param targetWidth Breite
     * @param targetHeight Höhe
     * @param textureX Textur-X-Koordinate in Pixel von links oben
     * @param textureY Textur-Y-Koordinate in Pixel von links oben
     * @param textureWidth Texturbreite in Pixel
     * @param textureHeight Texturhöhe in Pixel
     * @return ein fertig initialisierter VAO der direkt gerendert werden kann
     */
    public static VAO IOnlyWantToDrawAPartOfATexture(int targetX, int targetY, int targetWidth, int targetHeight, String textureName, int textureX, int textureY, int textureWidth, int textureHeight) {
        VAO vao = createDynamicTexturedRectVAO();
        float sourceX = (float) textureX / RenderUtils.getTextureWidth(textureName);
        float sourceY = (float) textureY / RenderUtils.getTextureHeight(textureName);
        float sourceWidth = (float) textureWidth / RenderUtils.getTextureWidth(textureName);
        float sourceHeight = (float) textureHeight / RenderUtils.getTextureHeight(textureName);
        vao.pushRectT(targetX, targetY, targetWidth, targetHeight, sourceX, sourceY, sourceWidth, sourceHeight);
        vao.upload();
        return vao;
    }

    /**
     * Erzeugt ein bewegbares, farbiges Rechteck.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param f
     * @return
     */
    public static DynamicRectangleVAO createDynamicRectangleVAO(int x, int y, int width, int height, float[] f) {
        return new DynamicRectangleVAO(x, y, width, height, f);
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
