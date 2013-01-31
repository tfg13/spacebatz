package de._13ducks.spacebatz.util.mapgen.data;

/**
 * Eine Sammlung für Farben für Blöcke.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Theme {
    
    public final int ground;
    public final int wall;
    public final int spawn;
    public final int border;
    
    public Theme(int ground, int wall, int spawn, int border) {
        this.ground = ground;
        this.wall = wall;
        this.spawn = spawn;
        this.border = border;
    }

}
