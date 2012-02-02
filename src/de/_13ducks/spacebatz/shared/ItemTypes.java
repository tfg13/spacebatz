package de._13ducks.spacebatz.shared;

import java.util.ArrayList;

/**
 * Liste aller Grundgegenstände (Geld, Waffen)
 * @author j
 */
public class ItemTypes {
    private ArrayList<ItemTypeStats> itemtypelist;
    
    public ItemTypes() {
        itemtypelist = new ArrayList<>();
        // name, pic, itemclass, quality, amount, damage, attackspeed, range, armor, itemtypeID
        itemtypelist.add(new ItemTypeStats("Money" ,0 ,0 ,1 , 1, 0, 0, 0, 0, 0));
        itemtypelist.add(new ItemTypeStats("Hat", 1, 2, 1, 0, 0, 0, 0, 42, 1));
        itemtypelist.add(new ItemTypeStats("Laser", 2, 1, 1, 0, 23, 5.0f, 10.0f, 0, 2));
        itemtypelist.add(new ItemTypeStats("Super Laser", 3, 1, 2, 0, 1337, 5.0f, 10.0f, 0, 3));
    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemTypeStats> getItemtypelist() {
        return itemtypelist;
    }
}
