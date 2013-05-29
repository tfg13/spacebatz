package de._13ducks.spacebatz.client.graphics.input.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.input.InputMode;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.panX;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.panY;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.tilesX;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.tilesY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_SHOOT;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * Haupt-Inputmodul.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class GameInput extends InputMode {

    /**
     * Die Position der Maus in Spielkoordinaten.
     */
    private static double logicMouseX, logicMouseY;

    @Override
    public void permanentKeyboardInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            sendAbilityRequest((byte) 1);
        }
    }

    @Override
    public void keyboardPressed(int key, boolean pressed) {
        if (pressed) {
            switch (key) {
                case Keyboard.KEY_1:
                    if (GameClient.player.getSelectedattack() != 0) {
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 0);
                    }
                    break;
                case Keyboard.KEY_2:
                    if (GameClient.player.getSelectedattack() != 1) {
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 1);
                    }
                    break;
                case Keyboard.KEY_3:
                    if (GameClient.player.getSelectedattack() != 2) {
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 2);
                    }
                    break;
            }
        }
    }

    @Override
    public void permanentAsyncKeyboardInput(boolean mainInputActive) {
        byte move = 0;
        if (mainInputActive) {
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                move |= 0x20;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                move |= 0x80;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                move |= 0x40;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                move |= 0x10;
            }
        }
        GameClient.player.predictMovement(move);
        CTS_MOVE.sendMove((float) Math.atan2((Mouse.getY() - Display.getHeight() / 2), (Mouse.getX() - Display.getWidth() / 2)), (float) GameClient.player.getX(), (float) GameClient.player.getY());
    }

    @Override
    public void mouseInput() {
        if (Mouse.isButtonDown(0)) {
            sendShootRequest();
        }
    }

    @Override
    public void mouseMovement() {
        logicMouseX = (1f * Mouse.getX() / Display.getWidth() * tilesX) - panX;
        logicMouseY = (1f * Mouse.getY() / Display.getHeight() * tilesY) - panY;
        GameClient.getEngine().getGraphics().defactoRenderer().setMouseXY(Mouse.getX(), Mouse.getY());
    }

    private void sendAbilityRequest(byte ability) {
        CTS_REQUEST_USE_ABILITY.sendAbilityUseRequest(ability, logicMouseX, logicMouseY);
    }

    /**
     * Sagt dem Server, das geschossen werden soll
     */
    private void sendShootRequest() {
        CTS_SHOOT.sendShoot(logicMouseX, logicMouseY);
    }

    /**
     * @return the logicMouseX
     */
    public static double getLogicMouseX() {
        return logicMouseX;
    }

    /**
     * @return the logicMouseY
     */
    public static double getLogicMouseY() {
        return logicMouseY;
    }
}
