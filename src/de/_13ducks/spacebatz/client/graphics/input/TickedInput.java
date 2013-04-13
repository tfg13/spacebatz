package de._13ducks.spacebatz.client.graphics.input;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * Inputsystem, das nicht an Grafikframes gebunden ist, sondern an Logikticks.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class TickedInput {

    /**
     * Der zuletzt gesendete Bewegungsbefehl.
     */
    private static byte lastMove = 1;
    /**
     * Wie lange läuft diese Bewegung (in diese Richtung) schon?
     */
    private static short ticksMoving = -1;

    private TickedInput() {
    }

    /**
     * Wird einmal pro Logiktick aufgerufen.
     */
    public static void tick() {
        byte move = 0;
        if (!GameClient.getEngine().getGraphics().defactoRenderer().isTerminal()) {
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
        if (move != lastMove) {
            ticksMoving = 1;
            lastMove = move;
        } else {
            ticksMoving++;
        }
        CTS_MOVE.sendMove(move, (float) Math.atan2((Mouse.getY() - Display.getHeight() / 2), (Mouse.getX() - Display.getWidth() / 2)), ticksMoving);
        GameClient.player.predictMovement(move);
    }
}
