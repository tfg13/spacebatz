package de._13ducks.spacebatz.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Die Werte, die ein bestimmter Itemtyp standardmäßig hat (können durch Attribute noch verändert werden)
 * @author Jojo
 */
public class ItemTypeStats implements java.io.Serializable{
    public HashMap<String, Serializable> itemStats;
    
    public ItemTypeStats(String name, int pic, int itemclass, int quality, int amount, int armor, ItemTypeWeaponStats weaponstats) {
        itemStats = new HashMap<>();
        itemStats.put("name", name);
        itemStats.put("pic", pic);
        itemStats.put("itemclass", itemclass); // 0 - Geld/Meterial, 1 - Waffe, 2 - Rüstung
        itemStats.put("quality", quality); // 0 - wird nicht zufällig gedroppt, sonst: kann gedroppt werden wenn quality <= gegnerlevel
        itemStats.put("amount", amount);
        itemStats.put("armor", armor);
        
        if (weaponstats != null) {
            itemStats.put("damage", weaponstats.getDamage());
            itemStats.put("attackcooldown", weaponstats.getAttackcooldown());
            itemStats.put("range", weaponstats.getRange());
            itemStats.put("weaponpic", weaponstats.getWeaponpic());
            itemStats.put("bulletpic", weaponstats.getBulletpic());
            itemStats.put("bulletspeed", weaponstats.getBulletspeed());
            itemStats.put("spread", weaponstats.getSpread());
            itemStats.put("explosionradius", weaponstats.getExplosionradius());
        }
    }
    
    
}
