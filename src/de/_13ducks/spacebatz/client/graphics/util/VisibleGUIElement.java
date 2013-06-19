package de._13ducks.spacebatz.client.graphics.util;

import de._13ducks.spacebatz.client.graphics.vao.VAO;
import java.util.ArrayList;

/**
 * Ein sichtbares GUI-Element. Besteht aus einem oder mehreren VAOs die gerendert werden wenn es sichtbar ist.
 *
 * @author mekhar
 */
public abstract class VisibleGUIElement extends GUIElement {

    private ArrayList<VAO> vaos;

    public VisibleGUIElement() {
        vaos = new ArrayList<>();
    }

    /**
     * Fügt diesem Element ein neues VAO zum rendern hinzu.
     *
     * @param vao
     */
    public void addVAO(VAO vao) {
        vaos.add(vao);
    }

    /**
     * Löscht die verwendeten VAOs um Grafikspeicher wieder freizugeben. Muss aufgerufen werden bevor dieses Element der GarbageCollection zum Opfer fällt, sonst gibt es ein
     * Speicherleck.
     */
    public void dispose() {
        for (VAO vao : vaos) {
            vao.destroy();
        }
    }

    @Override
    public void renderElement() {
        for (VAO vao : vaos) {
            vao.render();
        }
    }

    @Override
    public abstract void mouseMove(int mx, int my, int button);

    @Override
    public abstract void mousePressed(float x, float y, int button);

    @Override
    public abstract void mouseReleased(float x, float y, int button);

    @Override
    public abstract void keyboardInput(int key, boolean down);
}
