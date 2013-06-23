package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.graphics.vao.VAO;

/**
 * Ein Objekt, das Chars, Bullets, (Items) etc. besitzen.
 * Regelt das Rendern auf dem Client mithilfe von Animationen.
 *
 * @author jk
 */
public class RenderObject {

    /**
     * Dieses Renderobject auf der Grafikkarte.
     */
    private VAO vao;
    /**
     * Standard-Animation
     */
    private Animation baseAnim;

    /**
     * Konstruktor
     *
     * @param baseAnim Grundanimation, die gerendert wird
     */
    public RenderObject(Animation baseAnim) {
        this.baseAnim = baseAnim;
    }

    /**
     * @return Standardanimation
     */
    public Animation getBaseAnim() {
        return baseAnim;
    }

    /**
     * @return the vao
     */
    public VAO getVao() {
        return vao;
    }

    /**
     * @param vao the vao to set
     */
    public void setVao(VAO vao) {
        this.vao = vao;
    }
}
