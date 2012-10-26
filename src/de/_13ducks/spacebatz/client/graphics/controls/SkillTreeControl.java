package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Image;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
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
    private HashMap<String, SkillTreeItem> items;
    /**
     * Gibt an ob die linke Maustaste gedrückt ist.
     */
    private boolean buttonDown;
    /**
     * Die verbleibenden Skillpunkte.
     */
    private int remainingPoints;

    /**
     * Initialisiert das SkilltreeControl.
     *
     * @param renderer
     */
    public SkillTreeControl(Renderer renderer) {
        skilltreeTexture = renderer.getTextureByName("skilltree.png");
        items = new HashMap<>();
        remainingPoints = 10;

        SkillTreeItem summon = new SkillTreeItem(renderer);
        summon.name = "summon";
        summon.setIndex(0);
        summon.level = 0;
        summon.setPosX(0.4f);
        summon.setPosY(0.4f);
        summon.setWidth(0.055f);
        summon.setHeight(0.055f);
        items.put("summon", summon);

        SkillTreeItem masssummon = new SkillTreeItem(renderer);
        masssummon.name = "masssummon";
        masssummon.setIndex(1);
        masssummon.level = 0;
        masssummon.setPosX(0.5f);
        masssummon.setPosY(0.5f);
        masssummon.setWidth(0.055f);
        masssummon.setHeight(0.055f);
        items.put("masssummon", masssummon);

    }

    /**
     * Setzt den Status eines Skills.
     *
     * @param name
     * @param level
     */
    public void setSkillStatus(String name, byte level) {
        if (items.containsKey(name)) {
            items.get(name).level = level;
        } else {
            throw new IllegalArgumentException("Skill " + name + " ist nicht bekannt!");
        }

    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTilemap(skilltreeTexture);
        renderer.setTileSize(32, 32);
        renderer.setColor(0.9f, 0.9f, 0.9f);
        renderer.drawRectangle(0.3f, 0.3f, 0.4f, 0.4f);
        renderer.resetColor();
        for (SkillTreeItem item : items.values()) {
            item.render(renderer);
        }
    }

    @Override
    public void input() {
        if (Mouse.isButtonDown(0)) {
            buttonDown = true;
        } else {
            if (buttonDown) {
                buttonDown = false;
                for (SkillTreeItem item : items.values()) {
                    if (item.isMouseOver((float) Mouse.getX() / Settings.CLIENT_GFX_RES_X, (float) Mouse.getY() / Settings.CLIENT_GFX_RES_Y)) {
                        if (remainingPoints > 0) {
                            CTS_INVEST_SKILLPOINT.sendInvestSkillPoint(item.name);
                            remainingPoints--;
                        }
                    }
                }
            }
        }
        // Skilltree wieder verbergen wenn T gedrückt wird:
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_T && Keyboard.getEventKeyState()) {
                GameClient.getEngine().getGraphics().toggleSkillTree();
            }
        }


    }

    /**
     * Ein Skillbutton, der den Skilllevel anzeigt und angeklickt werden kann um den Skill zu erbessern.
     */
    private class SkillTreeItem extends Image {

        private byte level;
        private String name;

        public SkillTreeItem(Renderer renderer) {
            super();

            setTilemap(renderer.getTextureByName("skilltree.png"));
        }

        protected void render(Renderer renderer) {
            if (level < 0) {
                renderer.setColor(0.1f, 0.1f, 0.1f);
            }
            renderer.setScreenMapping(0.0f, 1.0f, 0.0f, 1.0f);
            renderer.setTileSize(32, 32);
            renderer.drawImage(this);
            renderer.restoreScreenMapping();
            renderer.resetColor();
            renderer.renderText(String.valueOf(level), getPosX() + 0.045f, getPosY());
        }

        protected boolean isMouseOver(float mouseX, float mouseY) {
            return getPosX() < mouseX && mouseX < (getPosX() + getWidth()) && getPosY() < mouseY && mouseY < (getPosX() + getHeight());
        }
    }
}
