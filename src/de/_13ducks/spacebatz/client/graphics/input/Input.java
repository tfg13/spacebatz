package de._13ducks.spacebatz.client.graphics.input;

import de._13ducks.spacebatz.client.GameClient;
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
 * Genereller Spiel-Input. (synchon mit Frames)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Input {
    
    /**
     * Die Position der Maus in Spielkoordinaten.
     */
    private double logicMouseX, logicMouseY;

    public void input() {
        logicMouseX = (1f * Mouse.getX() / Display.getWidth() * tilesX) - panX;
        logicMouseY = (1f * Mouse.getY() / Display.getHeight() * tilesY) - panY;
        
        if (!GameClient.getEngine().getGraphics().defactoRenderer().isTerminal()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                sendAbilityRequest((byte) 1);
            }
            if (Mouse.isButtonDown(0)) {
                sendShootRequest();
            }

            outer:
            while (Keyboard.next()) {
                int key = Keyboard.getEventKey();
                boolean pressed = Keyboard.getEventKeyState();
                if (pressed) {
                    switch (key) {
                        case Keyboard.KEY_F1:
                            GameClient.getEngine().getGraphics().defactoRenderer().setTerminal(true);
                            break outer;
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
                }
            }
        } else {
            while (Keyboard.next()) {
                // Nur gedrückte Tasten
                if (Keyboard.getEventKeyState()) {
                    int key = Keyboard.getEventKey();
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
                        break;
                    } else {
                        char c = Keyboard.getEventCharacter();
                        if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= ' ' && c <= '?')) {
                            GameClient.terminal.input(c);
                        }
                    }
                }
            }
        }
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
