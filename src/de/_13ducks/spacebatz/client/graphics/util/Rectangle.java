/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client.graphics.util;

import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;

/**
 *
 * @author mekhar
 */
public class Rectangle extends VisibleGUIElement {

    private VAO vao;

    public Rectangle(int x, int y, int width, int height, float[] color) {
        vao = VAOFactory.createDynamicColoredRectVAO();
        vao.pushRectC(x, y, width, height, color, color, color, color);
        vao.upload();
    }

    @Override
    public void renderElement() {
        vao.render();
    }

    @Override
    public void mouseMove(int mx, int my) {
    }

    @Override
    public void mousePressed(float x, float y, int button) {
    }

    @Override
    public void mouseReleased(float x, float y, int button) {
    }

    @Override
    public void keyboardInput(int key, boolean down) {
    }
}
