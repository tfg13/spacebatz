package de._13ducks.spacebatz.util.mapgen.data;

/**
 * Eine Sammlung für Farben für Blöcke.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Theme {
    
    public final int ground;
    public final int belowWall;
    public final int spawn;
    public final int border;
    public final int wall;
    
    public Theme(int ground, int belowWall, int spawn, int border, int wall) {
        this.ground = ground;
        this.belowWall = belowWall;
        this.spawn = spawn;
        this.border = border;
        this.wall = wall;
    }

}
