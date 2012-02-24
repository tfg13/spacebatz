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
        // name, pic, itemclass, quality, amount, damage, attackcooldown, range, armor
        itemtypelist.add(new ItemTypeStats("Money" ,0 ,0 ,1 , 1, 0, 0, 0, 0));
        itemtypelist.add(new ItemTypeStats("Hat", 1, 2, 1, 0, 0, 0, 0, 23));
        itemtypelist.add(new ItemTypeStats("Better Hat", 1, 2, 4, 0, 0, 0, 0, 42));
        itemtypelist.add(new ItemTypeStats("Laser", 2, 1, 1, 0, 4, 6.0, 9.0, 0));
        itemtypelist.add(new ItemTypeStats("Super Laser", 3, 1, 2, 0, 25, 20.0, 15.0, 0));
    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemTypeStats> getItemtypelist() {
        return itemtypelist;
    }
}
