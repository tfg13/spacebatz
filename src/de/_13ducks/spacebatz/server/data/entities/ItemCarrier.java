package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Inventory;
import de._13ducks.spacebatz.server.data.ServerInventory;
import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.data.entities.move.Mover;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Item;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten. Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends Char {

    /**
     * Das Inventar.
     */
    public ServerInventory inventory;
    /**
     * Wieviel Materialien der Spieler hat (Geld, Erze, ...)
     */
    private int materials[] = new int[CompileTimeParameters.NUMBER_OF_MATERIALS];
    /**
     * Der gerade aktive Waffenslot
     */
    private int activeWeaponSlot;
    /**
     * Der gerade aktive ToolSlot
     */
    private int activeToolSlot = Inventory.TOOLSLOT1;

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
     * Wählt gerade aktive Waffe aus
     *
     * @param selectedweapon aktiver Waffenslot ( z.B. Inventory.WEAPONSLOT1 )
     */
    public void setSelectedweapon(int selectedweapon) {
        inventory.setActiveWeapon(selectedweapon);
        this.activeWeaponSlot = selectedweapon;

    }

    /**
     * Setzt das aktive Werkzeug.
     *
     */
    public void setSelectedTool(int tool) {
        if (tool == Inventory.TOOLSLOT1 || tool == Inventory.TOOLSLOT2) {
            activeToolSlot = tool;
        }
    }

    /**
     * Gibt die gerade ausgewählte Waffe zurück
     *
     * @return ein Weapon
     */
    public Item getActiveWeapon() {
        return inventory.getActiveWeapon();
    }

    /**
     * Gibt die gerade ausgewählte Waffe zurück
     *
     * @return ein Weapon
     */
    public Item getActiveTool() {
        return inventory.getItem(activeToolSlot);
    }

    /**
     * Reduziert Überhitzung für die drei ausgerüsteten Waffen
     */
    @Override
    public void tick(int gametick) {
        super.tick(gametick);
        for (int i = Inventory.WEAPONSLOT1; i <= Inventory.WEAPONSLOT3; i++) {
            Item weapon = inventory.getItem(i);
            if (weapon != null) {
                // Waffe, die gerade schiesst, soll nicht abkühlen
                if (i != activeWeaponSlot || Server.game.getTick() >= attackCooldownTick - 1) {
                    weapon.increaseOverheat(-weapon.getWeaponAbility().getWeaponStats().getReduceoverheat());
                }
            }
        }
    }
}
