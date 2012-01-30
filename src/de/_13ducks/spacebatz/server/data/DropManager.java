package de._13ducks.spacebatz.server.data;

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
    private static ArrayList<ItemTypeStats> itemtypelist = itemtypes.getItemtypelist();

    /**
     * dropItem wählt Item aus, dass von diesem Gegner gedroppt wird
     * @param x x-Position
     * @param y y-Position
     * @param droplevel Gegnerlevel, bestimmt welche Items droppen können
     */
    public static void dropItem(double x, double y, int droplevel) {
        Random random = new Random(System.nanoTime());

        // Warscheinlichkeit von Drop abhängig von Spielerzahl
        if (random.nextFloat() > (0.8f - 0.5f /(float) Server.game.clients.size())) {
            return;
        }

        ArrayList<ItemTypeStats> dropableitems = new ArrayList<>();
        for (int i = 0; i < itemtypelist.size(); i++) {
            if ((int) itemtypelist.get(i).itemStats.get("quality") <= droplevel) {
                dropableitems.add(itemtypelist.get(i));
            }
        }
        ItemTypeStats stats = dropableitems.get(random.nextInt(dropableitems.size()));
        System.out.println("item: " + stats.itemStats.get("name"));

        Item item = new Item(x, y, stats, Server.game.newNetID());
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
}
