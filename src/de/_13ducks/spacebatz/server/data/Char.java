package de._13ducks.spacebatz.server.data;

/**
 * Ein bewwegliches Objekt
 * (z.B. ein Spieler, Mob etc)
 * @author michael
 */
public class Char {
    
    /**
     * Die Position des Chars
     */
    public int posX;
    public int posY;
    
    /**
     * Die Bewegung des Objekts
     */
    public int dX, dY;
    
    /**
     * Der Name des Chars
     */
    public int id;
   
    /**
     * Konstruktor, erstellt einen neuen Char
     * @param x
     * @param y
     * @param name 
     */
    public Char(int x, int y, int id){
        this.posX = x;
        this.posY = y;
        this.id = id;
    }
    
}
