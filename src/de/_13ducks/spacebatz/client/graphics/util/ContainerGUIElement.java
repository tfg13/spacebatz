package de._13ducks.spacebatz.client.graphics.util;

import java.util.ArrayList;

/**
 * Ein Element, das aus mehrerern GUIElements besteht.
 *
 * @author mekhar
 */
public class ContainerGUIElement extends GUIElement {

    /**
     * Die Unterelemente des Containers
     */
    private ArrayList<GUIElement> children;

    public ContainerGUIElement() {
        children = new ArrayList<>();
    }

    public void addChild(GUIElement element) {
        children.add(element);
    }

    @Override
    public void renderElement() {
        for (GUIElement child : children) {
            child.render();
        }
    }

    @Override
    public void mouseMove(int mx, int my) {
        for (GUIElement child : children) {
            child.mouseMove(mx, my);
        }
    }

    @Override
    public void keyboardInput(int key, boolean down) {
        for (GUIElement child : children) {
            child.keyboardInput(key, down);
        }
    }

    @Override
    public void mousePressed(float x, float y, int button) {
        for (GUIElement child : children) {
            child.mousePressed(x, y, button);
        }
    }

    @Override
    public void mouseReleased(float x, float y, int button) {
        for (GUIElement child : children) {
            child.mouseReleased(x, y, button);
        }
    }
}
