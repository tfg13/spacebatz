/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.ItemAttribute;
import de._13ducks.spacebatz.shared.ItemAttributeTypes;
import de._13ducks.spacebatz.shared.ItemTypes;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_MATERIAL_AMOUNT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CREATE_ITEM;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * DropManager bestimmt, welche Items gedroppt werden
 *
 * @author Jojo
 */
public class DropManager {

    private final static ItemTypes itemtypes = new ItemTypes();
    private final static ItemAttributeTypes itemattribute = new ItemAttributeTypes();
    private static ArrayList<ItemAttribute> itemtypelist = itemtypes.getItemtypelist();

    /**
     * dropItem wÃ¤hlt Item aus, dass von diesem Gegner gedroppt wird
     *
     * @param x x-Position
     * @param y y-Position
     * @param droplevel Gegnerlevel, bestimmt welche Items droppen kÃ¶nnen
     */
    public static void dropItem(double x, double y, int droplevel) {
        for (Client c : Server.game.clients.values()) {

            Random random = new Random(System.nanoTime());

            ArrayList<ItemAttribute> dropableitems = new ArrayList<>();
            for (int i = 0; i < itemtypelist.size(); i++) {
                int itemquality = (int) itemtypelist.get(i).getQuality();
                //Itemquality muss niedriger/gleich Gegnerlevel und ungleich 0 sein
                if (itemquality <= droplevel && itemquality != 0) {
                    dropableitems.add(itemtypelist.get(i));
                }
            }
            ItemAttribute stats = dropableitems.get(random.nextInt(dropableitems.size()));
            Item item = new Item(stats.getName(), stats, Server.game.newNetID());

            // zufällige Itemattribute
            item = addAttributes(item, droplevel);

            //Server.game.getItemMap().put(item.getNetID(), item);

            byte[] serializedItem = null;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream os;
            try {
                os = new ObjectOutputStream(bs);
                os.writeObject(item);
                os.flush();
                bs.flush();
                bs.close();
                os.close();
                serializedItem = bs.toByteArray();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

//            c.getPlayer().putItem(item.getNetID(), item);
//            STC_ITEM_DROP.sendItemDrop(serializedItem, c.clientID);

            // Versucht, dem Spieler das Item zu geben.
            int slot = c.getPlayer().inventory.tryCreateItem(item, c.getPlayer());
            if (slot != -1) {
                STC_CREATE_ITEM.sendCreateItem(item, slot, c);
            } else {
                System.out.println("Kann Item nicht in Inventar einfügen!");
            }

            // Testcode:
            //int money_amount = random.nextInt((int) Math.ceil(Math.pow(droplevel, 1.5) * 3 / 4)) + (int) Math.ceil(Math.pow(droplevel, 1.5) / 4);
            dropMaterial(0, 1);

        }
    }

    // TESTCODE
    public static void dropStartItems(Client c) {

        ArrayList<ItemAttribute> dropableitems = new ArrayList<>();
        for (int i = 0; i < itemtypelist.size(); i++) {
            String itemname = itemtypelist.get(i).getName();
            //Itemquality muss niedriger/gleich Gegnerlevel und ungleich 0 sein
            if (itemname.equals("Drill")) {
                dropableitems.add(itemtypelist.get(i));
            }
        }
        ItemAttribute stats = dropableitems.get(0);
        Item item = new Item(stats.getName(), stats, Server.game.newNetID());

        //Server.game.getItemMap().put(item.getNetID(), item);

        byte[] serializedItem = null;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(item);
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedItem = bs.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        c.getPlayer().putItem(item.getNetID(), item);
//        STC_ITEM_DROP.sendItemDrop(serializedItem, c.clientID);

        // Versucht, dem Spieler das Item zu geben.
        int slot = c.getPlayer().inventory.tryCreateItem(item, c.getPlayer());
        if (slot != -1) {
            STC_CREATE_ITEM.sendCreateItem(item, slot, c);
        } else {
            System.out.println("Kann Item nicht in Inventar einfügen!");
        }
    }

    /**
     * Dem übergebenen Item zufällige Attribute hinzufügen
     *
     * @param item das Item
     * @return wieder das Item
     */
    private static Item addAttributes(Item item, int droplevel) {
        ArrayList<ItemAttribute> attributesofrightclass = new ArrayList<>();
        ArrayList<ItemAttribute> allowedattributes = new ArrayList<>();

        attributesofrightclass.addAll(itemattribute.getAttributelist(droplevel));


        for (int i = 0; i < attributesofrightclass.size(); i++) {
            // nur Attribute mögich, die keinen höheren Level haben als es der vom Enemy ders gedroppt hat
            double test = attributesofrightclass.get(i).getQuality();
            if (test <= droplevel) {
                allowedattributes.add(attributesofrightclass.get(i));
            }
        }

        Random random = new Random();
        int rand = random.nextInt(80);

        // Anzahl Attribute, die das Item kriegt (bis zu)
        int maxatt = 0;
        if (rand > 72) {
            maxatt = 4;
        } else if (rand > 62) {
            maxatt = 3;
        } else if (rand > 47) {
            maxatt = 2;
        } else if (rand > 30) {
            maxatt = 1;
        }

        for (int i = 0; i < maxatt; i++) {
            ItemAttribute randomatt = allowedattributes.get(random.nextInt(allowedattributes.size()));
            if (!item.getItemAttributes().contains(randomatt)) {
                if (item.getWeaponAbility() == null && randomatt.isWeaponAttribute()) {
                    // Überspringen, wenn einem Hut ein Waffenattribut gegeben werden soll.
                    continue;
                }
                item.addAttribute(randomatt);
            }
        }

        return item;
    }

    /**
     * Gibt allen Spielern eine bestimmte Anzahl eines Materials
     *
     * @param material Materialtyp
     * @param amount Anzahl
     */
    public static void dropMaterial(int material, int amount) {
        for (Client c : Server.game.clients.values()) {
            int newamount = c.getPlayer().getMaterial(material) + amount;

            c.getPlayer().setMaterial(material, newamount);
            STC_CHANGE_MATERIAL_AMOUNT.sendMaterialAmountChange(c.clientID, material, newamount);
        }
    }
}
