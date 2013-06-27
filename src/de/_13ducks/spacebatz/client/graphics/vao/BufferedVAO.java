package de._13ducks.spacebatz.client.graphics.vao;

import java.util.Arrays;
import org.lwjgl.opengl.GL15;

/**
 * Ein VAO nur für dynamische Inhalte, die sich immer wieder ändern.
 * Dieser VAO vereinfacht den Verwaltungsaufwand in dem die gesetzten Werte zusätzlich Clientseitig gecached werden.
 * Wenn upload() aufgerufen wird, wird untersucht, ob tatsächlich was geändert wurden, und nur dann hochgeladen.
 *
 * BufferedVAOs sind immer dynamisch, der bufferMode ist also fest (GL_DYNAMIC_DRAW)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class BufferedVAO extends VAO {

    private float[] bufferedVertices;
    private float[] bufferedColors;
    private float[] bufferedTexCoords;

    BufferedVAO(int numberOfVertices, boolean useTexture, boolean useColor, int drawMode, int shaderColorTexModeAdr) {
        super(numberOfVertices, useTexture, useColor, GL15.GL_DYNAMIC_DRAW, drawMode, shaderColorTexModeAdr);
        bufferedVertices = new float[vertices.length];
        if (colors != null) {
            bufferedColors = new float[colors.length];
        }
        if (texCoords != null) {
            bufferedTexCoords = new float[texCoords.length];
        }
    }

    @Override
    public void upload() {
        if (checkChange()) {
            // hat sich geändert, hochladen
            super.upload();
            // Änderungen speichern
            copyBuffer();
        } else {
            // Upload faken
            modified = false;
            uploaded = true;
        }
    }

    private void copyBuffer() {
        System.arraycopy(vertices, 0, bufferedVertices, 0, vertices.length);
        if (texCoords != null) {
            System.arraycopy(texCoords, 0, bufferedTexCoords, 0, texCoords.length);
        }
        if (colors != null) {
            System.arraycopy(colors, 0, bufferedColors, 0, colors.length);
        }
    }

    private boolean checkChange() {
        return (!Arrays.equals(vertices, bufferedVertices) || !Arrays.equals(texCoords, bufferedTexCoords) || !Arrays.equals(colors, bufferedColors));
    }
}
