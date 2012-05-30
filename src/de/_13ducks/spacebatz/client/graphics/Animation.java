package de._13ducks.spacebatz.client.graphics;

/**
 * Eine Animation, die aus Bildern und einer Zeit besteht.
 * Wird benutzt um alle möglichen Objekte grafisch darzustellen.
 * 
 * @author jk
 */
public class Animation {

    /**
     * Tilemap, auf der sich das/die Bilder finden, mit denen gerendert wird
     */
    private String tilemap;
    /**
     * Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     */
    private int picsizex;
    /**
     * Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     */
    private int picsizey;
    /**
     * Position des Start-Einzelbildes (oberes linkes Tilemapstück)
     */
    private int startpic;
    /**
     * Anzahl der Einzelbilder der Animation
     */
    private int numberofpics;
    
    /**
     * Konstruktor.
     * @param tilemap Tilemap, auf der sich das/die Bilder finden, mit denen gerendert wird
     * @param picsizex Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     * @param picsizey Die Größe der einzelnen Animationsbilder (in Tilemap-Stücken)
     * @param startpic Position des Start-Einzelbildes (oberes linkes Tilemapstück)
     * @param numberofpics Anzahl der Einzelbilder der Animation
     */
    public Animation(String tilemap, int picsizex, int picsizey, int startpic, int numberofpics) {
        this.tilemap = tilemap;
        this.picsizex = picsizex;
        this.picsizey = picsizey;
        this.startpic = startpic;
        this.numberofpics = numberofpics;
    }

    /**
     * @return the tilemap
     */
    public String getTilemap() {
        return tilemap;
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
     * @return the startpic
     */
    public int getStartpic() {
        return startpic;
    }

    /**
     * @return the numberofpics
     */
    public int getNumberofpics() {
        return numberofpics;
    }
}
