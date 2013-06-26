package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.client.graphics.overlay.TriggeredOverlay;
import de._13ducks.spacebatz.client.graphics.util.ContainerGUIElement;
import de._13ducks.spacebatz.client.graphics.util.DraggedImage;
import de._13ducks.spacebatz.client.graphics.util.Rectangle;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_MAP_ABILITY;
import java.util.HashMap;
import org.lwjgl.util.Color;

/**
 * Zeigt den Skilltree an. Der Skilltree zeigt den Status an, den der Server übermittelt und schickt Anfragen an den Server wenn ein Skill verbessert werden soll. Ob das Skillen
 * oder Skill-Mapping geklappt hat erfährt er über die Antworten des Servers.
 *
 * @author michael
 */
public class SkillTreeOverlay extends TriggeredOverlay {

    /**
     * Liste der Skilltreeienträge.
     */
    private HashMap<String, SkillButton> skills;
    /**
     * Die Liste dewr Slots, in die man Skills droppen kann.
     */
    private HashMap<Byte, SkillSlot> skillSlots;
    private ContainerGUIElement skilltree;
    private boolean dragging;
    private int dragTile;
    private String draggedSkill;
    private static Color backgroundColor = new Color((byte) 100, (byte) 100, (byte) 100);
    DraggedImage draggedImage;

    /**
     * Initialisiert das SkilltreeControl.
     *
     * @param renderer
     */
    public SkillTreeOverlay() {
        skills = new HashMap<>();
        skillSlots = new HashMap<>();

        skilltree = new ContainerGUIElement();
        int x, y, width, height;

        // Hintergrund:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.2f);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.2f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.6f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.6f);
        Rectangle background = new Rectangle(x, y, width, height, new float[]{0.42f, 0.42f, 0.42f, 1});
        skilltree.addChild(background);

        // Das Bild, das bei drag+drop angezeigt wird:
        draggedImage = new DraggedImage((int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05f), (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05f), "skilltree.png", dragTile, 32);
        skilltree.addChild(draggedImage);


        // Summon-Fähigkeit:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.4f);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.4f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05f);
        SkillButton summon = new SkillButton("summon", 0, this, x, y, width, height);
        summon.setAvailable(true);
        skills.put("summon", summon);
        skilltree.addChild(summon);

        // MASS-Summon:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.4f);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.6f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05f);
        SkillButton masssummon = new SkillButton("masssummon", 1, this, x, y, width, height);
        masssummon.setAvailable(false);
        skills.put("masssummon", masssummon);
        skilltree.addChild(masssummon);

        // Leertastenzauberslot:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.6f);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.5f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05f);
        SkillSlot primaryAttack = new SkillSlot(4, this, (byte) 1, x, y, width, height);
        skillSlots.put((byte) 1, primaryAttack);
        skilltree.addChild(primaryAttack);

    }

    /**
     * Setzt den Status eines Skills.
     *
     * @param name
     * @param level
     */
    public void setSkillStatus(String name, byte level, boolean available) {
        if (skills.containsKey(name)) {
            skills.get(name).setAvailable(available);
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
    public void triggeredRender() {
//        RenderUtils.drawRectangle(0.3f, 0.3f, 0.4f, 0.4f, backgroundColor);

        skilltree.render();
        // Den gedraggten Skill rendern:
        if (dragging) {
//            RenderUtils.drawTile(dragTile, (float) Mouse.getX() / DefaultSettings.CLIENT_GFX_RES_X, (float) Mouse.getY() / DefaultSettings.CLIENT_GFX_RES_Y, 0.05f, 0.05f);
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

    /**
     * Beginnt mit einer Drag and Drop Operation.
     *
     * @param skillname
     * @param tile
     */
    public void startDrag(String skillname, int tile) {
        dragging = true;
        draggedImage.isVisible = true;
        draggedImage.setTile(tile);
        dragTile = tile;
        draggedSkill = skillname;
    }

    /**
     * Beendet eine Drag and Drop Operation.
     *
     * @param targetKey
     */
    public void stopDrag(byte targetKey) {
        if (dragging) {
            CTS_REQUEST_MAP_ABILITY.sendMapAbility(targetKey, draggedSkill);
            dragging = false;
            draggedImage.isVisible = false;
            dragTile = -1;
            draggedSkill = null;
        }
    }

    @Override
    protected void keyboardInput(int key, boolean pressed) {
    }

//    //@Override
//    public void input() {
//        // Input für alle Controls berechnen:
//        for (SkillButton button : skills.values()) {
//            button.input((float) Mouse.getX() / DefaultSettings.CLIENT_GFX_RES_X, (float) Mouse.getY() / DefaultSettings.CLIENT_GFX_RES_Y);
//        }
//        // Input für alle SkillSlots berechnen:
//        for (SkillSlot slot : skillSlots.values()) {
//            slot.input(Mouse.getX() / DefaultSettings.CLIENT_GFX_RES_X, Mouse.getY() / DefaultSettings.CLIENT_GFX_RES_Y);
//        }
//        // Wenn kein Slot ausgewählt ist das draggen abbrechen:
//        if (!Mouse.isButtonDown(0)) {
//            dragging = false;
//            dragTile = -1;
//            draggedSkill = null;
//        }
//    }
    @Override
    protected void mouseMove(int mx, int my) {
        skilltree.mouseMove(mx, my);
    }

    @Override
    protected void mousePressed(int mx, int my, int button) {
        skilltree.mousePressed(mx, my, button);
    }

    @Override
    protected void mouseReleased(int mx, int my, int button) {
        skilltree.mouseReleased(mx, my, button);
        dragging = false;
        draggedImage.isVisible = false;
        dragTile = -1;
        draggedSkill = null;
    }
}
