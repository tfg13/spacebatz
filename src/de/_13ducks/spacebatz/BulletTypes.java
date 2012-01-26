package de._13ducks.spacebatz;

import java.util.ArrayList;

/**
 *
 * @author bla
 */
public class BulletTypes implements java.io.Serializable{
    private ArrayList<BulletTypeStats> bullettypelist;
    
    public BulletTypes() {
        bullettypelist = new ArrayList<>();
        // damage, picture, numberofhits, speed, spread
        bullettypelist.add(new BulletTypeStats(3, 2, 1, 0.25f, (float) Math.PI / 80, 60));
    }

    /**
     * @return the bullettypelist
     */
    public ArrayList<BulletTypeStats> getBullettypelist() {
        return bullettypelist;
    }
}
