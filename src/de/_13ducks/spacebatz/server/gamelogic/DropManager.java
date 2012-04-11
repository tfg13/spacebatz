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
import de._13ducks.spacebatz.shared.*;
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
    private static ArrayList<ItemBaseAttribute> itemtypelist = itemtypes.getItemtypelist();

    /**
     * dropItem wÃ¤hlt Item aus, dass von diesem Gegner gedroppt wird
     *
     * @param x x-Position
     * @param y y-Position
     * @param droplevel Gegnerlevel, bestimmt welche Items droppen kÃ¶nnen
     */
    public static void dropItem(double x, double y, int droplevel) {
        Random random = new Random(System.nanoTime());

        //Warscheinlichkeit von Drop abhÃ¤ngig von Spielerzahl
//        if (random.nextFloat() > (0.8f - 0.5f / (float) Server.game.clients.size())) {
//            return;
//        }

        ArrayList<ItemBaseAttribute> dropableitems = new ArrayList<>();
        for (int i = 0; i < itemtypelist.size(); i++) {
            int itemquality = (int) itemtypelist.get(i).getBonusProperty("quality");
            //Itemquality muss niedriger/gleich Gegnerlevel und ungleich 0 sein
            if (itemquality <= droplevel && itemquality != 0) {
                dropableitems.add(itemtypelist.get(i));
            }
        }
        ItemBaseAttribute stats = dropableitems.get(random.nextInt(dropableitems.size()));
        Item item = new Item(stats.getName(), stats, x, y, Server.game.newNetID());

        if ((int) stats.getBonusProperty("itemclass") != 0) {
            item = addAttributes(item, droplevel);
        } else {
            item = addAmount(item, droplevel);
        }

        Server.game.getItemMap().put(item.getNetID(), item);

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

        Server.msgSender.sendItemDrop(serializedItem);
    }

    /**
     * Dem übergebenen Item zufällige Attribute hinzufügen
     *
     * @param item das Item
     * @return wieder das Item
     */
    private static Item addAttributes(Item item, int droplevel) {
        ArrayList<ItemAttribute> attributesofrightclass = new ArrayList<>();
        ArrayList<ItemAttribute> qualityallowedattributes = new ArrayList<>();
        ArrayList<ItemAttribute> addattributes = new ArrayList<>();

        attributesofrightclass.addAll(itemattribute.getAttributelist());


        for (int i = 0; i < attributesofrightclass.size(); i++) {
            // nur Attribute mögich, die keinen höheren Level haben als es der vom Enemy ders gedroppt hat
            double test = attributesofrightclass.get(i).getBonusProperty("quality");
            if (test <= droplevel) {
                qualityallowedattributes.add(attributesofrightclass.get(i));
            }
        }

        Random random = new Random();
        int rand = random.nextInt(21);

        // Anzahl Attribute, die das Item kriegt (bis zu)
        int maxatt;
        if (rand < 6) {
            maxatt = 0;
        } else if (rand < 11) {
            maxatt = 1;
        } else if (rand < 15) {
            maxatt = 2;
        } else if (rand < 18) {
            maxatt = 3;
        } else if (rand < 20) {
            maxatt = 4;
        } else {
            maxatt = 5;
        }

        for (int i = 0; i < maxatt; i++) {
            ItemAttribute randomatt = qualityallowedattributes.get(random.nextInt(qualityallowedattributes.size()));
            if (!addattributes.contains(randomatt)) {
                item.addAttribute(randomatt);
            }
        }


        return item;
    }

    /**
     * Dem Item das gedroppt werden soll eine Anzahl hinzufügen (z.B. bei Geld)
     *
     * @param item Das Item
     * @param droplevel Das Level vom Monster, das es gedroppt hat
     * @return wieder das Item
     */
    private static Item addAmount(Item item, int droplevel) {
        Random random = new Random();
        int amount = random.nextInt((int) Math.ceil(Math.pow(droplevel, 1.5) * 3 / 4)) + (int) Math.ceil(Math.pow(droplevel, 1.5) / 4);
        item.setAmount(amount);
        return item;
    }
}
