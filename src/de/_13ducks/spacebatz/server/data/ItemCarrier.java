package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.shared.Item;
import java.util.HashMap;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten.
 * Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends Char {

    /**
     * Items im Inventar
     */
    private HashMap<Integer, Item> items;
    /**
     * Wieviel Geld im Inventar ist
     */
    private int money;
    /**
     * Enthält einzelne Slotarten, z.B. die Waffenslots, Armorslots
     */
    private Item[][] equipslots = new Item[3][];
    /**
     * aktuell ausgewählte Waffe
     */
    private byte selectedweapon = 0;
    /**
     * Der Standardangriff ohne Waffe
     */
    protected final static Ability defaultAttackAbility = new FireBulletAbility(4, 18.0, 9.5, 0, 0.35, 0.03, 0.0);
    
    /**
     * Erzeugt einen neuen ItemCarrier
     *
     * @param posX die X-Koordinate des Carriers
     * @param posY die Y-Koordinate des Carriers
     * @param netId die netId des Carriers
     * @param typeId die typeId des Carriers
     */
    public ItemCarrier(double posX, double posY, int netId, byte typeId) {
        super(posX, posY, netId, typeId);
        items = new HashMap<>();
        Item[] wslot = new Item[3];
        Item[] aslot = new Item[1];

        equipslots[1] = wslot;
        equipslots[2] = aslot;

    }

    public Item[][] getEquipslots() {
        return equipslots;
    }

    /**
     * @return the items
     */
    public HashMap<Integer, Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(HashMap<Integer, Item> items) {
        this.items = items;
    }

    /**
     * @param items the items to add
     */
    public void putItem(int netID, Item item) {
        this.items.put(netID, item);
        if (item.getName().equals("Money")) {
            setMoney(getMoney() + (int) item.getProperty("amount"));
        }
    }

    /**
     * @return the money
     */
    public int getMoney() {
        return money;
    }

    /**
     * @param money the money to set
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * Item in leeren Slot anlegen
     *
     * @param itemnetID NetID des Items
     * @param selectedslot ausgewählter Slot
     */
    public boolean equipItem(int itemnetID, byte selectedslot) {
        Item item = getItems().get(itemnetID);
        // richtiger Itemtyp für diesen Slot?
        int slottype = (int) item.getProperty("itemclass");

        if (getEquipslots()[slottype] != null && getEquipslots()[slottype][selectedslot] == null && item != null) {
            // Jetzt neues Item anlegen
            getEquipslots()[slottype][selectedslot] = item;
            getItems().remove(item.getNetID());
            // die Stats des Items übernehmen:
            addProperties(item);
            
            if (getActiveWeapon() != null) {
                // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
                setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
            } else {
                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
                setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
            }
            
            // Item-Anleg-Befehl zum Client senden
            Server.msgSender.sendItemEquip(item.getNetID(), selectedslot, netID);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gibt zurück, ob noch ein freier Slot im Inventar ist
     */
    public boolean freeInventorySlot() {
        if (getItems().size() < Settings.INVENTORY_SIZE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Entfernt Item aus gegebenem Itemslot, tut es ins Inventar
     *
     * @param slottype Slotart (Waffe, Hut, ...)
     * @param selectedslot Nr. des Slots dieser Art
     */
    public boolean dequipItemToInventar(int slottype, byte selectedslot) {
        if (getEquipslots()[slottype] != null) {
            if (getEquipslots()[slottype][selectedslot] != null) {
                Item itemx = getEquipslots()[slottype][selectedslot];
                getEquipslots()[slottype][selectedslot] = null;
                // die Stats wieder abziehen:
                removeProperties(itemx);
                // passt das Item ins Inventar?
                getItems().put(itemx.getNetID(), itemx);
                
                if (getActiveWeapon() != null) {
                    // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
                    setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
                } else {
                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
                    setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
                }
                
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Entfernt Item aus gegebenem Itemslot, gibt es zurück
     *
     * @param slottype Slotart (Waffe, Hut, ...)
     * @param selectedslot Nr. des Slots dieser Art
     */
    public Item dequipItemToGround(int slottype, byte selectedslot) {
        Item item = null;
        if (getEquipslots()[slottype] != null) {
            if (getEquipslots()[slottype][selectedslot] != null) {
                item = getEquipslots()[slottype][selectedslot];
                getEquipslots()[slottype][selectedslot] = null;
                removeProperties(item);
                
                if (getActiveWeapon() != null) {
                    // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
                    setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
                } else {
                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
                    setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
                }
            }
        }
        return item;
    }

    /**
     * Wählt gerade aktive Waffe aus
     *
     * @param selectedweapon aktiver Waffenslot (0 bis 2)
     * @return Wahr wenn gültiger Slot
     */
    public boolean setSelectedweapon(byte selectedweapon) {
        if (selectedweapon >= 0 && selectedweapon <= 2) {
            this.selectedweapon = selectedweapon;
            if (getActiveWeapon() != null) {
                // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
                setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
            } else {
                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
                setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gibt die gerade ausgewählte Waffe zurück
     *
     * @return ein Weapon
     */
    public Item getActiveWeapon() {
        return equipslots[1][selectedweapon];
    }
    
    /**
     * Stackt das Item auf eins das schon im Inventar ist
     * @param itemname Name des zu stackenden Items
     * @return true wenn gestackt werden kann
     */
    public boolean tryItemStack(Item newitem) {
        boolean returnboolean = false;
        for (Item bla : items.values()) {
            if (bla.getName().equals(newitem.getName())) {
                bla.setAmount(bla.getAmount() + newitem.getAmount());
                returnboolean = true;
                break;
            }
        }
        return returnboolean;
    }
}
