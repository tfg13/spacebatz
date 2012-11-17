package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;

/**
 * Ein einzelner grafischer Effekt, z.B. Explosion
 *
 * @author Jojo
 */
public class Fx {

    /**
     * Animation, die abgespielt wird
     */
    private Animation anim;
    /**
     * Position
     */
    private double x;
    /**
     * Position
     */
    private double y;
    /**
     * Lebensdauer des Fx in Ticks
     */
    private int lifetime;
    /**
     * Der Tick, zu dem es angelegt wurde
     */
    private int starttick;
    /**
     * Besitzer des Fx. 
     * Fx wird auf seine Position gerendert wenn != null
     */
    private Char owner;

    /**
     * Konstruktor
     *
     * @param baseAnim Grundanimation, die gerendert wird
     */
    public Fx(Animation anim, double x, double y, int lifetime) {
        this.anim = anim;
        this.x = x;
        this.y = y;
        this.lifetime = lifetime;
        starttick = GameClient.frozenGametick;
    }

    /**
     * @return Standardanimation
     */
    public Animation getAnim() {
        return anim;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the starttick
     */
    public int getStarttick() {
        return starttick;
    }

    /**
     * @return the lifetime
     */
    public int getLifetime() {
        return lifetime;
    }

    /**
     * @return the owner
     */
    public Char getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Char owner) {
        this.owner = owner;
    }
}
