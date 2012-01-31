package de._13ducks.spacebatz;

import java.util.ArrayList;

/**
 *
 * @author Jojo
 */
public class ItemAttributeTypes {
        private ArrayList<ItemAttributeWeapon> weaponattributelist;
        private ArrayList<ItemAttributeArmor> armorattributelist;
    
    public ItemAttributeTypes() {
        weaponattributelist = new ArrayList<>();
        // name, quality, damage, attackspeed, range
        weaponattributelist.add(new ItemAttributeWeapon("More Damage", 1, 0.2f, 0.0f, 0.0f));
        weaponattributelist.add(new ItemAttributeWeapon("Even More Damage", 2, 0.5f, 0.0f, 0.0f));
        weaponattributelist.add(new ItemAttributeWeapon("More Attackspeed", 1, 0.0f, 0.2f, 0.0f));
        weaponattributelist.add(new ItemAttributeWeapon("Even More Attackspeed", 2, 0.0f, 0.5f, 0.0f));
        weaponattributelist.add(new ItemAttributeWeapon("More Range", 2, 0.0f, 0.0f, 0.2f));
        weaponattributelist.add(new ItemAttributeWeapon("Everything", 2, 0.3f, 0.3f, 0.1f));
        
        armorattributelist = new ArrayList<>();
        // name, quality, armor, movespeed
        armorattributelist.add(new ItemAttributeArmor("More Armor", 1, 0.2f, 0.0f));
        armorattributelist.add(new ItemAttributeArmor("Even More Armor", 2, 0.5f, 0.0f));
        armorattributelist.add(new ItemAttributeArmor("More Movespeed", 2, 0.0f, 0.1f));
    }

    /**
     * @return the weaponattributelist
     */
    public ArrayList<ItemAttributeWeapon> getWeaponattributelist() {
        return weaponattributelist;
    }

    /**
     * @return the armorattributelist
     */
    public ArrayList<ItemAttributeArmor> getArmorattributelist() {
        return armorattributelist;
    }

}
