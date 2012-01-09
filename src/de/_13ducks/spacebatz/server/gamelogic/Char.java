package de._13ducks.spacebatz.server.gamelogic;

/**
 * Ein bewwegliches Objekt
 * (z.B. ein Spieler, Mob etc)
 * @author michael
 */
class Char {
    
    /**
     * Die Position des Chars
     */
    public int posX;
    public int posY;
    
    /**
     * Der Name des Chars
     */
    public String name;
   
    /**
     * Konstruktor, erstellt einen neuen Char
     * @param x
     * @param y
     * @param name 
     */
    public Char(int x, int y, String name){
        this.posX = x;
        this.posY = y;
        this.name = name;
    }
    
}
