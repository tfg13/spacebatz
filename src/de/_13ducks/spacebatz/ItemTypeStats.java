package de._13ducks.spacebatz;

import java.util.HashMap;

/**
 *
 * @author Jojo
 */
public class ItemTypeStats {
    public HashMap<String, Object> itemStats;
    
    public ItemTypeStats(String name, int pic, int itemclass, int quality, int amount, int damage, float attackspeed, float range, int armor, int itemtypeID) {
        itemStats = new HashMap<>();
        itemStats.put("name", name);
        itemStats.put("pic", pic);
        itemStats.put("itemclass", itemclass); // 0 - Geld/Meterial, 1 - Rüstung, 2 - Waffe
        itemStats.put("quality", quality); // 0 - wird nicht zufällig gedroppt, sonst: kann gedroppt werden wenn quality <= gegnerlevel
        itemStats.put("amount", amount);
        itemStats.put("damage", damage);
        itemStats.put("attackspeed", attackspeed);
        itemStats.put("range", range);
        itemStats.put("armor", armor);
        itemStats.put("itemtypeID", itemtypeID);
    }
    
    
}
