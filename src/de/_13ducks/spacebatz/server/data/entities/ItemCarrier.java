package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.move.Mover;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_DELETE_ITEM;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_INV_ITEM_MOVE;
import java.util.Arrays;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten. Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends Char {

    /**
     * Items im Inventar
     */
    private Item[] inventory = new Item[CompileTimeParameters.INVENTORY_SIZE];
    /**
     * Wieviel Materialien der Spieler hat (Geld, Erze, ...)
     */
    private int materials[] = new int[CompileTimeParameters.NUMBER_OF_MATERIALS];
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
    public ItemCarrier(int netId, byte typeId, Mover mover, Team team) {
        super(netId, typeId, mover, team);
        Item[] wslot = new Item[3];
        Item[] aslot = new Item[1];

        equipslots[1] = wslot;
        equipslots[2] = aslot;

    }

    public Item[][] getEquipslots() {
        return equipslots;
    }

    /**
     * @return Inventar (ohne die Equipslots)
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
     * @param equipslot ausgewählter Slot
     */
    public void equipItem(int inventoryslot, byte equipslot) {
        Item item = inventory[inventoryslot];

        // richtiger Itemtyp für diesen Slot?
        int slottype = item.getItemClass();

        if (getEquipslots()[slottype] != null && getEquipslots()[slottype][equipslot] == null && item != null) {
            // Jetzt neues Item anlegen
            getEquipslots()[slottype][equipslot] = item;
            inventory[inventoryslot] = null;
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
            STC_EQUIP_ITEM.sendItemEquip(inventoryslot, equipslot, ((Player) this).getClient().clientID, (float) getSpeed());
        }
    }

    /**
     * Item aus Inventar löschen
     *
     * @param inventoryslot ausgewählter Slot
     */
    public void deleteItem(int inventoryslot) {
        if (inventoryslot == -1) {
            // gesamtes Inventar löschen
            Arrays.fill(inventory, null);
            STC_DELETE_ITEM.sendItemDelete(inventoryslot, ((Player) this).getClient().clientID);
        } else if (inventory[inventoryslot] != null) {
            // einzelnes Item löschen
            inventory[inventoryslot] = null;
            STC_DELETE_ITEM.sendItemDelete(inventoryslot, ((Player) this).getClient().clientID);
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

    /**
     * Tauscht 2 Items im Inventar
     * 2. Slot mus kein Item enthalten, dann wird nur 1. Item verschoben
     *
     * @param inventoryslot1 erster Inventarplatz
     * @param inventoryslot2 zweiter Inventarplatz
     */
    public void moveInvItems(int inventoryslot1, int inventoryslot2) {
        // Items vertauschen
        Item swapItem = inventory[inventoryslot2];
        inventory[inventoryslot2] = inventory[inventoryslot1];
        inventory[inventoryslot1] = swapItem;

        STC_INV_ITEM_MOVE.sendInvItemMove(inventoryslot1, inventoryslot2, ((Player) this).getClient().clientID);
    }

    /**
     * Reduziert Überhitzung für die drei ausgerüsteten Waffen
     */
    @Override
    public void tick(int gametick) {
        super.tick(gametick);

        for (int i = 0; i <= 2; i++) {
            Item weapon = equipslots[1][i];
            if (weapon != null) {
                // Waffe, die gerade schiesst, soll nicht abkühlen
                if (i != selectedweapon || Server.game.getTick() >= attackCooldownTick - 1) {
                    weapon.increaseOverheat(-weapon.getWeaponAbility().getWeaponStats().getReduceoverheat());
                }
            }
        }
    }
}
