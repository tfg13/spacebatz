package de._13ducks.spacebatz.client.graphics.renderer;

/**
 * Oberklasse für Haupt-Rendermodule.
 *
 * Rendern einen guten Teil des Spiels, kann durch Overlays ergänzt werden.
 *
 * Core-Renderer bekommen keinerlei Input, der wird über das Input-System verarbeitet.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class CoreRenderer {

    /**
     * Wird aufgerufen, wenn das Spiel von einem anderen CoreRenderer zu diesem hier wechselt.
     * Es ist garantiert, dass diese Methode mindestens ein Mal aufgerufen wird: Vor der erstern Verwendung (render) dieses CoreRenderers.
     *
     * Das Rendermodul soll hier Vertex- und Fragmentshader so einstellen, wie es sie braucht.
     * Die Grafikengine garantiert, dass die hier gesetzten Shader immer aktiv sind, wenn render() aufgerufen wird.
     *
     * @param shader Alle geladenene Shader
     */
    public abstract void setupShaders(int[] shader);

    /**
     * Wird aufgerufen, um die Shader dieses CoreRenderers wieder aktivieren zu lassen.
     * Wird aufgerufen, falls die Shader seit dem letzten Aufruf von render() geändert wurden.
     */
    public abstract void reEnableShader();

    /**
     * Rendert den Inhalt.
     * Muss alle notwendigen Renderbefehle an die Grafikkarte senden.
     * Es ist garantiert (durch Aufrufe auf reEnableShader()), dass die Vertex- und Fragmentshader wieder aktiv sind, die es nach dem Aufruf von setupShaders() waren.
     */
    public abstract void render();

    /**
     * Setzt die Mausposition.
     * Notwendig, weil die Grafik sich der Mausposition anpassen will.
     *
     * @param mouseX Maus-X in Pixeln
     * @param mouseY Maus-Y in Pixeln
     */
    public abstract void setMouseXY(double mouseX, double mouseY);

    /**
     * Liefert die Anzahl Felder, um die die Ansicht derzeit verschoben ist.
     *
     * @return panX
     */
    public abstract double getPanX();

    /**
     * Liefert die Anzahl Felder, um die die Ansicht derzeit verschoben ist.
     *
     * @return panY
     */
    public abstract double getPanY();
}
