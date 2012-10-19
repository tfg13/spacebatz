package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Rendert Text.
 *
 * @author michael
 */
public class TextWriter {

    private Texture font[] = new Texture[2];
    /**
     * Charset, zum Textoutput-Encoding
     */
    private Charset charset;
    /**
     * Wieviel Platz die einzelnen Buchstaben brauchen.
     */
    private byte[] spaceing;

    /**
     * Lädt die Texturen und Spacing-Informationen.
     */
    public TextWriter() {
        charset = Charset.forName("cp437");
        spaceing = new byte[256];
        InputStream r = ResourceLoader.getResourceAsStream("tex/font_spacing.bin");
        int b;
        int i = 0;
        try {
            while ((b = r.read()) != -1) {
                spaceing[i++] = (byte) b;
            }
            font[0] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/font.png"), GL_NEAREST);
            font[1] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("tex/font_mono.png"), GL_NEAREST);
        } catch (IOException ex) {
            Logger.getLogger(TextWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     */
    public void renderText(String text, float x, float y) {
        renderText(text, x, y, false, 0f, 0f, 0f, 1f);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     * @param mono Monospace-Font und (!) klein?
     */
    public void renderText(String text, float x, float y, boolean mono) {
        renderText(text, x, y, mono, 0f, 0f, 0f, 1f);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (unten links)
     * @param y PositionY (unten rechts)
     * @param red_color Textfarbe Rotanteil
     * @param blue_color Textfarbe Blauanteil
     * @param green_color Textfarbe Grünanteil
     * @param green_color Textfarbe Alpha-anteil
     */
    public void renderText(String text, float x, float y, float red_color, float blue_color, float green_color, float alpha_color) {
        renderText(text, x, y, false, red_color, blue_color, green_color, alpha_color);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position. Vorsicht: Bindet seine eigene Textur, man muss danach
     * selber rebinden!
     *
     * @param text Der zu zeichnende Text
     * @param x Relative X-Position (0-1)
     * @param y Relative Y-Position (0-1)
     * @param mono Monospace-Font und (!) klein?
     * @param red_color Textfarbe Rotanteil
     * @param blue_color Textfarbe Blauanteil
     * @param green_color Textfarbe Grünanteil
     * @param green_color Textfarbe Alpha-anteil
     */
    public void renderText(String text, float x, float y, boolean mono, float red_color, float blue_color, float green_color, float alpha_color) {
        glColor4f(red_color, blue_color, green_color, alpha_color);
        float next = 0;
        byte[] chars = text.getBytes(charset);
        font[mono ? 1 : 0].bind();
        float size = .5f;
        if (mono) {
            size /= GameClient.getEngine().getGraphics().getCamera().getZoomFactor();
        }
        for (int i = 0; i < chars.length; i++) {
            byte c = chars[i];
            int tileX = c % 16;
            int tileY = c / 16;
            float tx = tileX / 16f;
            float ty = tileY / 16f;
            glBegin(GL_QUADS);
            glTexCoord2f(tx, ty);
            glVertex3f(x + next, y + size, 0.0f);
            glTexCoord2f(tx + .0625f, ty);
            glVertex3f(x + next + size, y + size, 0.0f);
            glTexCoord2f(tx + .0625f, ty + .0625f);
            glVertex3f(x + next + size, y, 0.0f);
            glTexCoord2f(tx, ty + .0625f);
            glVertex3f(x + next, y, 0.0f);
            glEnd();
            // Spacing dieses chars weiter gehen:
            next += (mono ? 6 / 16f / GameClient.getEngine().getGraphics().getCamera().getZoomFactor() : spaceing[c] / 16f);
        }
        glColor3f(1f, 1f, 1f);
    }

    public int texAt(int[][] layer, int x, int y) {
        if (x < 0 || y < 0 || x >= layer.length || y >= layer[0].length) {
            return 0;
        } else {
            return layer[x][y];
        }
    }
}
