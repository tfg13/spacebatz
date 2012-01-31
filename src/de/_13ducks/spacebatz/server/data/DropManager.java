package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.ItemAttribute;
import de._13ducks.spacebatz.ItemAttributeWeapon;
import de._13ducks.spacebatz.ItemAttributeTypes;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.ItemTypeStats;
import de._13ducks.spacebatz.ItemTypes;
import de._13ducks.spacebatz.server.Server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * DropManager bestimmt, welche Items gedroppt werden
 * @author Jojo
 */
public class DropManager {

    private final static ItemTypes itemtypes = new ItemTypes();
    private final static ItemAttributeTypes itemattribute = new ItemAttributeTypes();
    private static ArrayList<ItemTypeStats> itemtypelist = itemtypes.getItemtypelist();
    private static ArrayList<ItemAttributeWeapon> attributelist = itemattribute.getWeaponattributelist();

    /**
     * dropItem wÃ¤hlt Item aus, dass von diesem Gegner gedroppt wird
     * @param x x-Position
     * @param y y-Position
     * @param droplevel Gegnerlevel, bestimmt welche Items droppen kÃ¶nnen
     */
    public static void dropItem(double x, double y, int droplevel) {
        Random random = new Random(System.nanoTime());

        // Warscheinlichkeit von Drop abhÃ¤ngig von Spielerzahl
        if (random.nextFloat() > (0.8f - 0.5f / (float) Server.game.clients.size())) {
            return;
        }

        ArrayList<ItemTypeStats> dropableitems = new ArrayList<>();
        for (int i = 0; i < itemtypelist.size(); i++) {
            int itemquality = (int) itemtypelist.get(i).itemStats.get("quality");
            //Itemquality muss niedriger/gleich Gegnerlevel und ungleich 0 sein
            if (itemquality <= droplevel && itemquality != 0) {
                dropableitems.add(itemtypelist.get(i));
            }
        }
        ItemTypeStats stats = dropableitems.get(random.nextInt(dropableitems.size()));
        System.out.println("item: " + stats.itemStats.get("name"));
        Item item = new Item(x, y, stats, Server.game.newNetID());

        if ((int) stats.itemStats.get("itemclass") != 0) {
            item = addAttributes(item, droplevel);
        }

        Server.game.items.add(item);

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
     * @param item das Item
     * @return wieder das Item
     */
    private static Item addAttributes(Item item, int droplevel) {
        ArrayList<ItemAttribute> attributesofrightclass = new ArrayList<>();
        ArrayList<ItemAttribute> qualityallowedattributes = new ArrayList<>();
        ArrayList<ItemAttribute> addattributes = new ArrayList<>();
        
        if (item.stats.itemStats.get("itemclass") == 1) {
            // Waffe
            attributesofrightclass.addAll(itemattribute.getWeaponattributelist());
        } else {
            // Rüstung
            attributesofrightclass.addAll(itemattribute.getArmorattributelist());      
        }
        
        for (int i = 0; i < attributesofrightclass.size(); i++) {
            // nur Attribute mögich, die keinen höheren Level haben als es der vom Enemy ders gedroppt hat
            if (attributesofrightclass.get(i).getQuality() <= droplevel) {
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
                addattributes.add(randomatt);
                System.out.println("Attribute " + randomatt.getName());
            }
        }
        
        item.setItemattributes(addattributes);
        return item;
    }
}
