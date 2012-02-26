package de._13ducks.spacebatz.shared;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Die zufälligen Attribute, die Items bekommen können
 * @author Jojo
 */
public class ItemAttributeTypes {

    private ArrayList<ItemAttribute> attributelist;

    public ItemAttributeTypes() {
        attributelist = new ArrayList<>();
        // name, quality, damage, attackspeed, range, armor, movespeed


        HashMap<String, Double> moreDamage = new HashMap<>();
        moreDamage.put("quality", 1.0);
        moreDamage.put("damage", 0.1);
        attributelist.add(new ItemAttribute("More Damage", moreDamage));

        HashMap<String, Double> evenMoreDamage = new HashMap<>();
        evenMoreDamage.put("quality", 2.0);
        evenMoreDamage.put("damage", 0.3);
        attributelist.add(new ItemAttribute("Even More Damage", evenMoreDamage));

        HashMap<String, Double> moreAttackSpeed = new HashMap<>();
        moreAttackSpeed.put("quality", 1.0);
        moreAttackSpeed.put("attackspeed", 0.1);
        attributelist.add(new ItemAttribute("More Attackspeed", moreAttackSpeed));

        HashMap<String, Double> evenMoreAttackSpeed = new HashMap<>();
        evenMoreAttackSpeed.put("quality", 2.0);
        evenMoreAttackSpeed.put("attackspeed", 0.3);
        attributelist.add(new ItemAttribute("Even More Attackspeed", evenMoreAttackSpeed));

        HashMap<String, Double> moreRange = new HashMap<>();
        moreRange.put("quality", 2.0);
        moreRange.put("range", 0.2);
        attributelist.add(new ItemAttribute("More Range", moreRange));

        HashMap<String, Double> moreEverything = new HashMap<>();
        moreEverything.put("quality", 3.0);
        moreEverything.put("damage", 0.1);
        moreEverything.put("attackspeed", 0.1);
        moreEverything.put("range", 0.1);
        attributelist.add(new ItemAttribute("More Everything", moreEverything));

        HashMap<String, Double> moreArmor = new HashMap<>();
        moreArmor.put("quality", 1.0);
        moreArmor.put("armor", 0.1);
        attributelist.add(new ItemAttribute("More Armor", moreArmor));

        HashMap<String, Double> evenMoreArmor = new HashMap<>();
        evenMoreArmor.put("quality", 2.0);
        evenMoreArmor.put("armor", 0.3);
        attributelist.add(new ItemAttribute("Even More Armor", evenMoreArmor));

        HashMap<String, Double> moreHP = new HashMap<>();
        moreHP.put("quality", 1.0);
        moreHP.put("healthpoints", 5.0);
        attributelist.add(new ItemAttribute("More Healthpoints", moreHP));

        HashMap<String, Double> moreMovespeed = new HashMap<>();
        moreMovespeed.put("quality", 2.0);
        moreMovespeed.put("movespeed", 0.2);
        attributelist.add(new ItemAttribute("More Movespeed", moreMovespeed));

        HashMap<String, Double> unusual = new HashMap<>();
        unusual.put("quality", 2.0);
        attributelist.add(new ItemAttribute("Unusual", unusual));
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
