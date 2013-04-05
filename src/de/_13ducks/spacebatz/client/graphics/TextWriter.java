package de._13ducks.spacebatz.client.graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Rendert Text.
 *
 * @author michael
 */
public class TextWriter {

    /**
     * Camera, notwendig für Koordinaten-Transformationen.
     */
    private final Camera camera;
    /**
     * Die geladenen Schriften.
     */
    @SuppressWarnings("deprecation")
    private TrueTypeFont fonts[] = new TrueTypeFont[2];

    /**
     * Lädt die Schriften.
     */
    public TextWriter(Camera camera) {
        this.camera = camera;
        try {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1.0, 1.0);
            InputStream inputStream0 = ResourceLoader.getResourceAsStream("font/Ubuntu-R.ttf");
            Font awtFont0 = Font.createFont(Font.TRUETYPE_FONT, inputStream0);
            awtFont0 = awtFont0.deriveFont(Display.getHeight() / 40f); // set font size
            fonts[0] = new TrueTypeFont(awtFont0, false);

            InputStream inputStream1 = ResourceLoader.getResourceAsStream("font/UbuntuMono-R.ttf");
            Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream1);
            awtFont1 = awtFont1.deriveFont(Display.getHeight() / 40f); // set font size
            fonts[1] = new TrueTypeFont(awtFont1, false);
        } catch (FontFormatException | IOException ex) {
            ex.printStackTrace();
        }
        glPopMatrix();
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position.
     * Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     * Setzt außerdem die aktuelle GL_COLOR.
     * Malt immer schwarz.
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (links)
     * @param y PositionY (unten)
     */
    public void renderText(String text, float x, float y) {
        renderText(text, x, y, false);
    }

    /**
     * Rendert den gegeben Text an die angegebenen Position.
     * Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     * Setzt außerdem die aktuelle GL_COLOR.
     * Malt immer schwarz.
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (mitte)
     * @param y PositionY (unten)
     */
    public void renderTextXCentered(String text, float x, float y) {
        renderText(text, x - ((fonts[0].getWidth(text) / 2f) / Display.getWidth() * camera.getTilesX()), y, false);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position.
     * Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     * Setzt außerdem die aktuelle GL_COLOR.
     * Malt immer schwarz.
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (links)
     * @param y PositionY (unten)
     * @param mono Monospace-Font und (!) klein?
     */
    public void renderText(String text, float x, float y, boolean mono) {
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1.0, 1.0);
        fonts[mono ? 1 : 0].drawString(snapToXRaster(x) / camera.getTilesX() * Display.getWidth(), (1f - (snapToYRaster(y + 0.5f) / camera.getTilesY())) * Display.getHeight(), text, Color.black);
        GL11.glPopMatrix();
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position.
     * Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     * Setzt außerdem die aktuelle GL_COLOR.
     *
     * @param text Der zu zeichnende Text
     * @param x PositionX (links)
     * @param y PositionY (unten)
     * @param red_color Textfarbe Rotanteil
     * @param blue_color Textfarbe Blauanteil
     * @param green_color Textfarbe Grünanteil
     * @param green_color Textfarbe Alpha-anteil
     */
    public void renderText(String text, float x, float y, float red_color, float blue_color, float green_color, float alpha_color) {
        renderText(text, x, y, false, red_color, blue_color, green_color, alpha_color);
    }

    /**
     * Rendert den gegebenen Text an die angegebenen Position.
     * Vorsicht: Bindet seine eigene Textur, man muss danach selber rebinden!
     * Setzt außerdem die aktuelle GL_COLOR.
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
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1.0, 1.0);
        fonts[mono ? 1 : 0].drawString(x / camera.getTilesX() * Display.getWidth(), (1f - ((y + 0.5f) / camera.getTilesY())) * Display.getHeight(), text, new Color(red_color, blue_color, green_color, alpha_color));
        GL11.glPopMatrix();
    }

    /**
     * Rundet die gegebene X-Koordinate auf ganze X-Pixel
     *
     * @param f X-Koordinate
     * @return gerundete Koordinate
     */
    private float snapToXRaster(float f) {
        return Math.round(f / camera.getTilesX() * Display.getWidth()) * camera.getTilesX() / Display.getWidth();
    }

    /**
     * Rundet die gegebene Y-Koordinate auf ganze Y-Pixel
     *
     * @param f Y-Koordinate
     * @return gerundete Koordinate
     */
    private float snapToYRaster(float f) {
        return Math.round(f / camera.getTilesY() * Display.getHeight()) * camera.getTilesY() / Display.getHeight();
    }
}
