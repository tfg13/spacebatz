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
        // name, pic, itemclass, quality, amount, armor, Waffenstats

        itemtypelist.add(new ItemTypeStats("Money", 0, 0, 1, 1, 0, null));

        itemtypelist.add(new ItemTypeStats("Hat", 1, 2, 1, 0, 23, null));

        itemtypelist.add(new ItemTypeStats("Better Hat", 1, 2, 4, 0, 42, null));

        ItemTypeWeaponStats pistol = new ItemTypeWeaponStats(3, 15.0, 10.0, 0, 1, 0.35, 0.025, 0.0);
        itemtypelist.add(new ItemTypeStats("Pistol", 4, 1, 1, 0, 0, pistol));

        ItemTypeWeaponStats laser = new ItemTypeWeaponStats(4, 6.0, 9.0, 0, 3, 0.35, 0.1, 0.0);
        itemtypelist.add(new ItemTypeStats("Laser", 2, 1, 2, 0, 0, laser));

        ItemTypeWeaponStats superlaser = new ItemTypeWeaponStats(25, 25.0, 12.0, 0, 2, 0.5, 0.02, 0.0);
        itemtypelist.add(new ItemTypeStats("Super Laser", 3, 1, 3, 0, 0, superlaser));

        ItemTypeWeaponStats rocketlauncher = new ItemTypeWeaponStats(20, 40.0, 15.0, 0, 4, 0.25, 0.0, 3.0);
        itemtypelist.add(new ItemTypeStats("Rocket Launcher", 5, 1, 1, 0, 0, rocketlauncher));
    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemTypeStats> getItemtypelist() {
        return itemtypelist;
    }
}
