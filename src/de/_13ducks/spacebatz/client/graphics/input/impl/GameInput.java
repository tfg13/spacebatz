package de._13ducks.spacebatz.client.graphics.input.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.input.InputMode;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.GodControl.panX;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.GodControl.panY;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.GodControl.tilesX;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.GodControl.tilesY;
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
    private double logicMouseX, logicMouseY;

    @Override
    public void permanentKeyboardInput() {
        if (!GameClient.getEngine().getGraphics().defactoRenderer().isTerminal()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                sendAbilityRequest((byte) 1);
            }
        }
    }

    @Override
    public void keyboardPressed(int key, boolean pressed) {
        if (pressed) {
            if (!GameClient.getEngine().getGraphics().defactoRenderer().isTerminal()) {
                switch (key) {
                    case Keyboard.KEY_F1:
                        GameClient.getEngine().getGraphics().defactoRenderer().setTerminal(true);
                        //break outer;
                        break;
                    case Keyboard.KEY_I:
                        GameClient.getEngine().getGraphics().toggleInventory();
                        break;
                    case Keyboard.KEY_T:
                        GameClient.getEngine().getGraphics().toggleSkillTree();
                        break;
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
            } else {
                if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
                    GameClient.terminal.enter();
                } else if (key == Keyboard.KEY_BACK) {
                    GameClient.terminal.backspace();
                } else if (key == Keyboard.KEY_UP) {
                    GameClient.terminal.scrollBack();
                } else if (key == Keyboard.KEY_DOWN) {
                    GameClient.terminal.scrollForward();
                } else if (key == Keyboard.KEY_F1) {
                    GameClient.getEngine().getGraphics().defactoRenderer().setTerminal(false);
                    System.out.println("FixMe: Implement disablement!");
                    //reak;
                } else {
                    char c = Keyboard.getEventCharacter();
                    if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= ' ' && c <= '?')) {
                        GameClient.terminal.input(c);
                    }
                }
            }
        }
    }

    @Override
    public void mouseInput() {
        if (!GameClient.getEngine().getGraphics().defactoRenderer().isTerminal()) {
            if (Mouse.isButtonDown(0)) {
                sendShootRequest();
            }
        }
    }

    @Override
    public void mouseMovement() {
        logicMouseX = (1f * Mouse.getX() / Display.getWidth() * tilesX) - panX;
        logicMouseY = (1f * Mouse.getY() / Display.getHeight() * tilesY) - panY;
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
}
