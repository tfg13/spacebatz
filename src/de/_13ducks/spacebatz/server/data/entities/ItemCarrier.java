package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.data.SpellBook;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_EQUIP_ITEM;
import java.util.HashMap;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten. Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends Char {

    /**
     * Items im Inventar
     */
    private static Item[] inventory = new Item[Settings.INVENTORY_SIZE];
    /**
     * Wieviel Materialien der Spieler hat (Geld, Erze, ...)
     */
    private int materials[] = new int[Settings.NUMBER_OF_MATERIALS];
    /**
     * Enthält einzelne Slotarten, z.B. die Waffenslots, Armorslots
     */
    private Item[][] equipslots = new Item[3][];
    /**
     * aktuell ausgewählte Waffe
     */
    private byte selectedweapon = 0;
//    /**
//     * Der Standardangriff ohne Waffe
//     */
//    protected final static Ability defaultAttackAbility = new FireBulletAbility(4, 18.0, 9.5, 0, 0.35, 0.03, 0.0);

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
    public Item[] getItems() {
        return inventory;
    }

    /**
     * @param items the items to add
     */
    public void putItem(int netID, Item item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                return;
            }
        }
        System.out.println("ACHTUNG: Inventar voll");
    }

    /**
     * Gibt zurück, welche Menge der Spieler von einem Material besitzt
     *
     * @param material Materialnummer
     * @return Materialmenge
     */
    public int getMaterial(int material) {
        return materials[material];
    }

    /**
     * Legt fest, welche Menge der Spieler von einem Material besitzt
     *
     * @param material Materialnummer
     * @param amount Materialmenge
     */
    public void setMaterial(int material, int amount) {
        materials[material] = amount;
    }

    /**
     * Item in leeren Slot anlegen
     *
     * @param itemnetID NetID des Items
     * @param selectedslot ausgewählter Slot
     */
    public void equipItem(int itemnetID, byte selectedslot) {
        Item item = null;
        int index = 0;
        while (index < inventory.length) {
            if (inventory[index] != null && inventory[index].getNetID() == itemnetID) {
                item = inventory[index];
                break;
            }
            index++;
        }
        
        // richtiger Itemtyp für diesen Slot?
        int slottype = (int) item.getItemClass();

        if (getEquipslots()[slottype] != null && getEquipslots()[slottype][selectedslot] == null && item != null) {
            // Jetzt neues Item anlegen
            getEquipslots()[slottype][selectedslot] = item;
            inventory[index] = null;
            // die Stats des Items übernehmen:
            addProperties(item.getBonusProperties());

//            if (getActiveWeapon() != null) {
//                // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
//                setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
//            } else {
//                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
//                setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
//            }

            // Item-Anleg-Befehl zum Client senden
            STC_EQUIP_ITEM.sendItemEquip(item.getNetID(), selectedslot, ((Player) this).getClient().clientID);
        }
    }

    /**
     * Gibt zurück, ob noch ein freier Slot im Inventar ist
     */
    public boolean freeInventorySlot() {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return true;
            }
        }
        return false;
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
                removeProperties(itemx.getBonusProperties());
                // passt das Item ins Inventar?
                putItem(itemx.getNetID(), itemx);

//                if (getActiveWeapon() != null) {
//                    // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
//                    setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
//                } else {
//                    // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
//                    setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
//                }

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
                removeProperties(item.getBonusProperties());
//
//                if (getActiveWeapon() != null) {
//                    // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
//                    setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
//                } else {
//                    // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
//                    setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
//                }
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
//            if (getActiveWeapon() != null) {
//                // Waffenfähigkeit wechseln, falls im ausgewählten slot eine Waffe ist
//                setAbility(ACTIVEWEAPONABILITY, getActiveWeapon().getAbility(), this);
//            } else {
//                // Wenn nicht dann Aktive Fähigkeit auf den Standardangriff seten:
//                setAbility(ACTIVEWEAPONABILITY, defaultAttackAbility, this);
//            }
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
}
