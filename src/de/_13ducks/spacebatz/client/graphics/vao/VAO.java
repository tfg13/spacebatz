package de._13ducks.spacebatz.client.graphics.vao;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Hilfsklasse für VertexArrayObjects.
 *
 * Benutzungshinweise: 1. Sich eines erstellen lassen, siehe dazu VAOFactory. 2. Genau so viele Vertices/Lines/Quads reinpushen, wie bei der Erstellung angelegt. 3. Auf Grafikkarte
 * hochladen 4. Nach belieben Zeichnen
 *
 * --- optional a. Datenpuffer reseten b. Daten neu pushen (geändert) c. Daten neu uploaden d. Daten wieder nach belieben zeichnen e. Nach belieben zu a. gehen --- ende optional
 *
 * 5. VAO löschen (destroy)
 *
 * destroy() muss immer dann aufgerufen werden, wenn der VAO nie wieder gezeichnet werden soll, und deshalb alle Pointer darauf genullt werden (oder einfach so verschwinden). Die
 * GC kann nicht auf die Grafikkarte zugreifen und deshalb auch nicht von alleine alles Aufräumen, das muss also manuell gemacht werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class VAO {

    /**
     * Array mit Vertexkoordinaten (x und y nacheinander).
     */
    protected final float[] vertices;
    /**
     * Array mit Texturkoordinaten (x und y nacheinander). Falls nicht verwendet null.
     */
    protected final float[] texCoords;
    /**
     * Array mit Farben. Falls nicht verwendet null.
     */
    protected final float[] colors;
    /**
     * Index des nächsten, einzutragenden Vertex (diese Zahl mal 2 für das vertices-Array).
     */
    private int vertexIndex = 0;
    /**
     * True, wenn Daten seit letzten Upload geändert wurden. Bedeutungslos, wenn noch nie geuploaded.
     */
    protected boolean modified = true;
    /**
     * True, wenn der Grafikkarte bekannt. Nur dann sind auch die Ids unten definiert.
     */
    private boolean created = false;
    /**
     * True, wenn diese Daten zur Grafikkarte geladen wurden.
     */
    protected boolean uploaded = false;
    /**
     * ID des VAO. Nur definiert, wenn created true ist.
     */
    private int vaoID;
    /**
     * ID des primären VBO. Nur definiert, wenn created true ist.
     */
    private int vboID;
    /**
     * VBO-Buffermodus.
     */
    private final int glmode;
    /**
     * Zeichenmodus (im Wesentlichen TRIANGLE oder LINE).
     */
    private final int drawMode;
    /**
     * Ungleich 0, falls dieses VAO mit einem Shader arbeitet, den man zwischen Col, Tex und TexCol umschalten muss. Hält dann die Adresse des Steuerungs-Uniforms.
     */
    private int shaderColorTexModeAdr;
    /**
     * In welchem Modus der Shader gerade ist.
     */
    private static int lastShaderColorTexMode = 1;

    VAO(int numberOfVertices, boolean useTexture, boolean useColor, int bufferUsage, int drawMode, int shaderColorTexModeAdr) {
        if (!useTexture && !useColor) {
            throw new IllegalArgumentException("Cannot create VAO without texture and color");
        }
        vertices = new float[numberOfVertices * 2];
        if (useTexture) {
            texCoords = new float[numberOfVertices * 2];
        } else {
            texCoords = null;
        }
        if (useColor) {
            colors = new float[numberOfVertices * 4];
        } else {
            colors = null;
        }
        this.glmode = bufferUsage;
        this.drawMode = drawMode;
        this.shaderColorTexModeAdr = shaderColorTexModeAdr;
    }

    /**
     * Pusht die gegebenen Koordinaten als neuen Vertex in den Puffer. Nur beim ersten befüllen und nach einem Reset zu verwenden.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param v V-Koordinate
     * @param w W-Koordinate
     * @param c Farbe
     */
    private void pushVertexTC(float x, float y, float v, float w, float[] c) {
        if (texCoords == null || colors == null) {
            throw new IllegalArgumentException("Illegal VAO usage: Cannot push texture and color to non-textured or non-colored VAO");
        }
        if (vertexIndex * 2 == vertices.length) {
            throw new IllegalArgumentException("Illegal: VAO usage: Cannot push another Vertex, VAO full");
        }

        vertices[vertexIndex * 2] = x;
        vertices[vertexIndex * 2 + 1] = y;
        texCoords[vertexIndex * 2] = v;
        texCoords[vertexIndex * 2 + 1] = w;
        colors[vertexIndex * 4] = c[0];
        colors[vertexIndex * 4 + 1] = c[1];
        colors[vertexIndex * 4 + 2] = c[2];
        colors[vertexIndex * 4 + 3] = c[3];
        vertexIndex++;
        modified = true;
    }

    /**
     * Pusht die gegebenen Koordinaten als neuen Vertex in den Puffer. Nur beim ersten befüllen und nach einem Reset zu verwenden.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param v V-Koordinate
     * @param w W-Koordinate
     */
    private void pushVertexT(float x, float y, float v, float w) {
        if (texCoords == null) {
            throw new IllegalArgumentException("Illegal VAO usage: Cannot push texture to non-textured VAO");
        }
        if (vertexIndex * 2 == vertices.length) {
            throw new IllegalArgumentException("Illegal: VAO usage: Cannot push another Vertex, VAO full");
        }

        vertices[vertexIndex * 2] = x;
        vertices[vertexIndex * 2 + 1] = y;
        texCoords[vertexIndex * 2] = v;
        texCoords[vertexIndex * 2 + 1] = w;
        vertexIndex++;
        modified = true;
    }

    /**
     * Pusht die gegebenen Koordinaten als neuen Vertex in den Puffer. Nur beim ersten befüllen und nach einem Reset zu verwenden.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param c Farbe
     */
    private void pushVertexC(float x, float y, float[] c) {
        if (colors == null) {
            throw new IllegalArgumentException("Illegal VAO usage: Cannot push color to non-colored VAO");
        }
        if (vertexIndex * 2 == vertices.length) {
            throw new IllegalArgumentException("Illegal: VAO usage: Cannot push another Vertex, VAO full");
        }

        vertices[vertexIndex * 2] = x;
        vertices[vertexIndex * 2 + 1] = y;
        colors[vertexIndex * 4] = c[0];
        colors[vertexIndex * 4 + 1] = c[1];
        colors[vertexIndex * 4 + 2] = c[2];
        colors[vertexIndex * 4 + 3] = c[3];
        vertexIndex++;
        modified = true;
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Rechteck in den Puffer.
     *
     * @param x X, Ecke links unten
     * @param y Y, Ecke links unten
     * @param sx X-Länge
     * @param sy Y-Länge
     * @param v V, Ecke links unten
     * @param w W, Ecke links unten
     * @param sv V-Länge
     * @param sw W-Länge
     * @param c00 Farbe links unten
     * @param c01 Farbe links oben
     * @param c10 Farbe rechts unten
     * @param c11 Farbe rechts oben
     */
    public void pushRectTC(float x, float y, float sx, float sy, float v, float w, float sv, float sw, float[] c00, float[] c01, float[] c10, float[] c11) {
        // Das sind 6 Vertices:
        pushVertexTC(x, y, v, w + sw, c00);
        pushVertexTC(x, y + sy, v, w, c01);
        pushVertexTC(x + sx, y + sy, v + sv, w, c11);
        pushVertexTC(x, y, v, w + sw, c00);
        pushVertexTC(x + sx, y + sy, v + sv, w, c11);
        pushVertexTC(x + sx, y, v + sv, w + sw, c10);
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Rechteck in den Puffer.
     *
     * @param x X, Ecke links unten
     * @param y Y, Ecke links unten
     * @param sx X-Länge
     * @param sy Y-Länge
     * @param v V, Ecke links unten
     * @param w W, Ecke links unten
     * @param sv V-Länge
     * @param sw W-Länge
     */
    public void pushRectT(float x, float y, float sx, float sy, float v, float w, float sv, float sw) {
        // Das sind 6 Vertices:
        pushVertexT(x, y, v, w + sw);
        pushVertexT(x, y + sy, v, w);
        pushVertexT(x + sx, y + sy, v + sv, w);
        pushVertexT(x, y, v, w + sw);
        pushVertexT(x + sx, y + sy, v + sv, w);
        pushVertexT(x + sx, y, v + sv, w + sw);
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Rechteck in den Puffer.
     *
     * @param x X, Ecke links unten
     * @param y Y, Ecke links unten
     * @param sx X-Länge
     * @param sy Y-Länge
     * @param c00 Farbe links unten
     * @param c01 Farbe links oben
     * @param c10 Farbe rechts unten
     * @param c11 Farbe rechts oben
     */
    public void pushRectC(float x, float y, float sx, float sy, float[] c00, float[] c01, float[] c10, float[] c11) {
        // Das sind 6 Vertices:
        pushVertexC(x, y, c00);
        pushVertexC(x, y + sy, c01);
        pushVertexC(x + sx, y + sy, c11);
        pushVertexC(x, y, c00);
        pushVertexC(x + sx, y + sy, c11);
        pushVertexC(x + sx, y, c10);
    }

    /**
     * Pusht die gegebenen Koordinaten als neue Linie in den Puffer.
     *
     * @param x1 X, Endpunkt 1
     * @param y1 Y, Endpunkt 1
     * @param x2 X, Endpunkt 2
     * @param y2 Y, Endpunkt 2
     * @param c1 Farbe, Endpunkt 1
     * @param c2 Farbe, Endpunkt 2
     */
    public void pushLineC(float x1, float y1, float x2, float y2, float[] c1, float[] c2) {
        // Das sind 2 Vertices:
        pushVertexC(x1, y1, c1);
        pushVertexC(x2, y2, c2);
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Dreieck in den Puffer.
     *
     * @param x1 X, Eckpunkt 1
     * @param y1 Y, Eckpunkt 1
     * @param x2 X, Eckpunkt 2
     * @param y2 Y, Eckpunkt 2
     * @param x3 X, Eckpunkt 3
     * @param y3 Y, Eckpunkt 3
     * @param v1 V, Eckpunkt 1
     * @param w1 W, Eckpunkt 1
     * @param v2 V, Eckpunkt 2
     * @param w2 W, Eckpunkt 2
     * @param v3 V, Eckpunkt 3
     * @param w3 W, Eckpunkt 3
     * @param c1 Farbe Eckpunkt 1
     * @param c2 Farbe Eckpunkt 2
     * @param c3 Farbe Eckpunkt 3
     */
    public void pushTriagleTC(float x1, float y1, float x2, float y2, float x3, float y3, float v1, float w1, float v2, float w2, float v3, float w3, float[] c1, float[] c2, float[] c3) {
        // Das sind 3 Vertices:
        pushVertexTC(x1, y1, v1, w1, c1);
        pushVertexTC(x2, y2, v2, w2, c2);
        pushVertexTC(x3, y3, v3, w3, c3);
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Dreieck in den Puffer.
     *
     * @param x1 X, Eckpunkt 1
     * @param y1 Y, Eckpunkt 1
     * @param x2 X, Eckpunkt 2
     * @param y2 Y, Eckpunkt 2
     * @param x3 X, Eckpunkt 3
     * @param y3 Y, Eckpunkt 3
     * @param v1 V, Eckpunkt 1
     * @param w1 W, Eckpunkt 1
     * @param v2 V, Eckpunkt 2
     * @param w2 W, Eckpunkt 2
     * @param v3 V, Eckpunkt 3
     * @param w3 W, Eckpunkt 3
     */
    public void pushTriagleT(float x1, float y1, float x2, float y2, float x3, float y3, float v1, float w1, float v2, float w2, float v3, float w3) {
        // Das sind 3 Vertices:
        pushVertexT(x1, y1, v1, w1);
        pushVertexT(x2, y2, v2, w2);
        pushVertexT(x3, y3, v3, w3);
    }

    /**
     * Pusht die gegebenen Koordinaten als neues Dreieck in den Puffer.
     *
     * @param x1 X, Eckpunkt 1
     * @param y1 Y, Eckpunkt 1
     * @param x2 X, Eckpunkt 2
     * @param y2 Y, Eckpunkt 2
     * @param x3 X, Eckpunkt 3
     * @param y3 Y, Eckpunkt 3
     * @param c1 Farbe Eckpunkt 1
     * @param c2 Farbe Eckpunkt 2
     * @param c3 Farbe Eckpunkt 3
     */
    public void pushTriagleTC(float x1, float y1, float x2, float y2, float x3, float y3, float[] c1, float[] c2, float[] c3) {
        // Das sind 3 Vertices:
        pushVertexC(x1, y1, c1);
        pushVertexC(x2, y2, c2);
        pushVertexC(x3, y3, c3);
    }

    /**
     * Läd die Daten zur Grafikkarte hoch. Muss unbedingt vor dem Zeichnen aufgerufen werden.
     */
    public void upload() {
        if (vertexIndex * 2 != vertices.length) {
            throw new IllegalStateException("Illegal: VAO usage: Cannot upload unfilled VAO");
        }
        if (uploaded && !modified) {
            throw new IllegalStateException("Illegal: VAO usage: Will not upload unchanged VAO");
        }
        // Erst noch erstellen?
        if (!created) {
            create();
        }
        // Create buffer
        int bufferSize = vertices.length;
        if (texCoords != null) {
            bufferSize += texCoords.length;
        }
        if (colors != null) {
            bufferSize += colors.length;
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(bufferSize);
        // Copy data
        buffer.put(vertices);
        if (texCoords != null) {
            buffer.put(texCoords);
        }
        if (colors != null) {
            buffer.put(colors);
        }
        buffer.flip();
        // Upload
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, glmode);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        uploaded = true;
        modified = false;
    }

    /**
     * Rendert das VAO. Dazu muss es geuploaded und nicht modifiziert sein.
     */
    public void render() {
        if (!uploaded) {
            throw new IllegalStateException("Illegal: VAO usage: Cannot render, please upload first!");
        }
        if (modified) {
            throw new IllegalStateException("Illegal: VAO usage: Cannot render, data was modified but not re-uploaded");
        }
        checkShaderMode();
        enable();
        GL11.glDrawArrays(drawMode, 0, vertices.length / 2);
        disable();
    }

    /**
     * Setzt die internen Puffer so zurück, damit sie wieder wie neu mit Daten gefüllt werden können. Es müssen unbedingt immer alle Vertices/Lines/Quads geupdated werden. Wenn
     * fertig, mittels upload() neu hochladen.
     */
    public void resetData() {
        vertexIndex = 0;
    }

    /**
     * Löscht die Daten dieses VAOs von der Grafikkarte. Nach diesem Aufruf darf die Referenz verworfen werden, damit die GC die Reste frisst.
     */
    public void destroy() {
        if (!created) {
            throw new IllegalStateException("Illegal: VAO usage: Cannot purge from graphics card, not uploaded");
        }
        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboID);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoID);
        created = false;
    }

    private void checkShaderMode() {
        if (shaderColorTexModeAdr != -1) {
            int targetMode = 0;
            if (texCoords != null && colors == null) {
                targetMode = 1;
            } else if (texCoords != null && colors != null) {
                targetMode = 2;
            }
            if (lastShaderColorTexMode != targetMode) {
                // Umschalten
                GL20.glUniform1i(shaderColorTexModeAdr, targetMode);
                lastShaderColorTexMode = targetMode;
            }
        }
    }

    private void create() {
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        // VBO erstellen
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        //GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tv, GL15.GL_STATIC_DRAW);
        // Daten-Links setzen
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        if (texCoords != null) {
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, vertices.length << 2);
        }
        if (colors != null) {
            if (texCoords != null) {
                GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 0, (vertices.length + texCoords.length) << 2);
            } else {
                GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 0, vertices.length << 2);
            }
        }
        GL30.glBindVertexArray(0);
        created = true;
    }

    private void enable() {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        if (texCoords != null) {
            GL20.glEnableVertexAttribArray(1);
        }
        if (colors != null) {
            GL20.glEnableVertexAttribArray(2);
        }
    }

    private void disable() {
        GL20.glDisableVertexAttribArray(0);
        if (texCoords != null) {
            GL20.glDisableVertexAttribArray(1);
        }
        if (colors != null) {
            GL20.glDisableVertexAttribArray(2);
        }
        GL30.glBindVertexArray(0);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // Warnung ausgebene, falls die Daten noch auf der Grafikkarte sind.
        // Das ist ein riesen Pfusch und darf nie nie sein.
        if (created) {
            System.out.println("GFX: WARN: GFX-memleak !!!");
            // Try to fix it
            destroy();
        }
    }
}
