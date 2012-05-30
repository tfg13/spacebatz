package de._13ducks.spacebatz.client.graphics;

/**
 * Ein Objekt, das Chars, Bullets, Items etc. besitzen.
 * Regelt das Rendern auf dem Client mithilfe von Animationen.
 * 
 * @author jk
 */
public class RenderObject {
    /**
     * Standard-Animation
     */
    private Animation baseAnim;
    
    /**
     * Konstruktor
     * @param baseAnim Animation, die gerendert wird
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
}
