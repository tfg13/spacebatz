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
     * Das Rendermodul soll hier die Projektions-Matrix etc. so einstellen, wie es sie braucht.
     * Die Grafikengine garantiert, dass diese Matrix so erhalten wird, solange der Renderer verwendet wird. (Sie ist also bei jedem Aufruf von render() so, wie sie hier einmalig definiert wurde.)
     * 
     * Wenn render() gerade nicht arbeitet, kann sich die Projektionsmatrix ändern, das wird dann aber mit pushMatrix() von der Grafikengine gesichert.
     */
    public abstract void defineOpenGLMatrices();
    
    /**
     * Rendert den Inhalt.
     * Muss alle notwendigen Renderbefehle an die Grafikkarte senden.
     * Es ist garantiert, dass das Koordinatensystem (wieder) so eingestellt ist, wie es nach dem Aufruf von defineOpenGLMatrices war.
     */
    public abstract void render();

}
