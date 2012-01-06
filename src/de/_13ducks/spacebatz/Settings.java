package de._13ducks.spacebatz;

/**
 * Spieleinstellungen, der Einfachheit halber hier statisch erreichbar.
 * Achtung: Für Server und Client gleich!!
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class Settings {
    
    /**
     * Private, da Utility-Class
     */
    private Settings() {
        
    }
    
    public static final int CLIENT_GFX_RES_X = 800;
    public static final int CLIENT_GFX_RES_Y = 640;
    public static final int CLIENT_GFX_TILESIZE = 16;
    public static final int CLIENT_GFX_TILEZOOM = 2;

}
