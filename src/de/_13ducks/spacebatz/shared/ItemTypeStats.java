package de._13ducks.spacebatz.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Die Werte, die ein bestimmter Itemtyp standardmäßig hat (können durch Attribute noch verändert werden)
 * @author Jojo
 */
public class ItemTypeStats implements java.io.Serializable{
    public HashMap<String, Serializable> itemStats;
    
    public ItemTypeStats(String name, int pic, int itemclass, int quality, int amount, int damage, float attackspeed, float range, int armor, int itemtypeID) {
        itemStats = new HashMap<>();
        itemStats.put("name", name);
        itemStats.put("pic", pic);
        itemStats.put("itemclass", itemclass); // 0 - Geld/Meterial, 1 - Waffe, 2 - Rüstung
        itemStats.put("quality", quality); // 0 - wird nicht zufällig gedroppt, sonst: kann gedroppt werden wenn quality <= gegnerlevel
        itemStats.put("amount", amount);
        itemStats.put("damage", damage);
        itemStats.put("attackspeed", attackspeed);
        itemStats.put("range", range);
        itemStats.put("armor", armor);
        itemStats.put("itemtypeID", itemtypeID); // wird evtl nicht mehr benutzt, da Items serialisiert werden
    }
    
    
}
