package de._13ducks.spacebatz.client.graphics;

/**
 * Eine Animation, die aus Bildern und einer Zeit besteht.
 * Wird benutzt um alle möglichen Objekte grafisch darzustellen.
 * Z.B. in Fx und RenderObject
 * 
 * @author jk
 */
public class Animation {

    /**
     * Position des Start-Einzelbildes (oberes linkes Tilemapstück)
     */
    private int startpic;
    /**
     * Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     */
    private int picsizex;
    /**
     * Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     */
    private int picsizey;
    /**
     * Anzahl der Einzelbilder der Animation
     */
    private int numberofpics;
    /**
     * Dauer der Einzelbilder der Animation in Ticks, egal bei Einzelbildern
     */
    private int picduration;
    /**
     * Die Richtung, in die die Animation gerendert wird
     * Wird nicht automatisch beutzt
     */
    private float direction;

    /**
     * Konstruktor.
     * @param startpic Position des Start-Einzelbildes (oberes linkes Tilemapstück)
     * @param picsizex Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     * @param picsizey Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     * @param numberofpics Anzahl der Einzelbilder der Animation
     */
    public Animation(int startpic, int picsizex, int picsizey, int numberofpics, int picduration) {
        this.startpic = startpic;
        this.picsizex = picsizex;
        this.picsizey = picsizey;
        this.numberofpics = numberofpics;
        this.picduration = picduration;
    }

    /**
     * @return the startpic
     */
    public int getStartpic() {
        return startpic;
    }

    /**
     * @return the picsizex
     */
    public int getPicsizex() {
        return picsizex;
    }

    /**
     * @return the picsizey
     */
    public int getPicsizey() {
        return picsizey;
    }

    /**
     * @return the numberofpics
     */
    public int getNumberofpics() {
        return numberofpics;
    }

    /**
     * @return the picduration
     */
    public int getPicduration() {
        return picduration;
    }

    /**
     * @return the direction
     */
    public float getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(float direction) {
        this.direction = direction;
    }
}
