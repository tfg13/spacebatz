package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_MAP_ABILITY;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

/**
 * Zeigt den Skilltree an.
 *
 * @author michael
 */
public class SkillTreeControl implements Control {

    /**
     * Die Textur mit den Silltree-Symbolen.
     */
    private Texture skilltreeTexture;
    /**
     * Liste der Skilltreeienträge.
     */
    private HashMap<String, SkillButton> skills;
    /**
     * Die Liste dewr Slots, in die man Skills droppen kann.
     */
    private HashMap<Byte, SkillSlot> skillSlots;
    private boolean dragging;
    private int dragTile;
    private String draggedSkill;

    /**
     * Initialisiert das SkilltreeControl.
     *
     * @param renderer
     */
    public SkillTreeControl(Renderer renderer) {
        skilltreeTexture = renderer.getTextureByName("skilltree.png");
        skills = new HashMap<>();
        skillSlots = new HashMap<>();

        SkillButton summon = new SkillButton("summon", 0, this, renderer);
        summon.setGeometry(0.4f, 0.4f, 0.05f, 0.05f);
        skills.put("summon", summon);

        SkillButton masssummon = new SkillButton("masssummon", 1, this, renderer);
        masssummon.setGeometry(0.4f, 0.5f, 0.05f, 0.05f);
        skills.put("masssummon", masssummon);

        SkillSlot primaryAttack = new SkillSlot(4, this, (byte) 1, renderer);
        primaryAttack.setGeometry(0.6f, 0.5f, 0.05f, 0.05f);
        skillSlots.put((byte) 1, primaryAttack);

    }

    /**
     * Setzt den Status eines Skills.
     *
     * @param name
     * @param level
     */
    public void setSkillStatus(String name, byte level) {
        if (skills.containsKey(name)) {
            skills.get(name).setLevel(level);
        } else {
            throw new IllegalArgumentException("Skill " + name + " ist nicht bekannt!");
        }
    }

    /**
     * Ändert den Skill der gerade im angegebenen Skillslot steht.
     *
     * @param skill
     * @param slot
     */
    public void setSkillMapping(String skill, byte slot) {
        if (skillSlots.containsKey(slot)) {
            skillSlots.get(slot).setTile(skills.get(skill).getTile());
        } else {
            throw new IllegalArgumentException("Skillslot " + slot + " nicht vorhanden!");
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTileSize(32, 32);
        renderer.setColor(0.9f, 0.9f, 0.9f);
        renderer.drawRectangle(0.3f, 0.3f, 0.4f, 0.4f);
        renderer.resetColor();

        // Skillbuttons rendern:
        for (SkillButton item : skills.values()) {
            item.render(renderer);
        }
        // SkillSlots rendern:
        for (SkillSlot slot : skillSlots.values()) {
            slot.render(renderer);
        }
        // Den gedraggten Skill rendern:
        if (dragging) {
            renderer.setScreenMapping(0, 1, 0, 1);
            renderer.drawTile(dragTile, (float) Mouse.getX() / Settings.CLIENT_GFX_RES_X, (float) Mouse.getY() / Settings.CLIENT_GFX_RES_Y, 0.05f, 0.05f);
            renderer.restoreScreenMapping();
        }
    }

    @Override
    public void input() {
        // Input für alle Controls berechnen:
        for (SkillButton button : skills.values()) {
            button.input((float) Mouse.getX() / Settings.CLIENT_GFX_RES_X, (float) Mouse.getY() / Settings.CLIENT_GFX_RES_Y);
        }
        // Input für alle SkillSlots berechnen:
        for (SkillSlot slot : skillSlots.values()) {
            slot.input(Mouse.getX() / Settings.CLIENT_GFX_RES_X, Mouse.getY() / Settings.CLIENT_GFX_RES_Y);
        }
        // Wenn kein Slot ausgewählt ist das draggen abbrechen:
        if (!Mouse.isButtonDown(0)) {
            dragging = false;
            dragTile = -1;
            draggedSkill = null;
        }
        // Skilltree wieder verbergen wenn T gedrückt wird:
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_T && Keyboard.getEventKeyState()) {
                GameClient.getEngine().getGraphics().toggleSkillTree();
            }
        }
    }

    /**
     * Investiert einen Skillpunkt.
     *
     * @param skillname
     */
    public void investSkillPoint(String skillname) {
        CTS_INVEST_SKILLPOINT.sendInvestSkillPoint(skillname);
    }

    public void startDrag(String skillname, int tile) {
        dragging = true;
        dragTile = tile;
        draggedSkill = skillname;
    }

    public void stopDrag(byte targetKey) {
        if (dragging) {
            CTS_REQUEST_MAP_ABILITY.sendMapAbility(targetKey, draggedSkill);
            dragging = false;
            dragTile = -1;
            draggedSkill = null;
        }
    }
}
