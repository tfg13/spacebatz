package de._13ducks.spacebatz.client.graphics.overlay;

/**
 * Ein Overlay.
 * 
 * Overlays werden nach dem vom Core-Renderer gezeichneten Inhalt ausgeführt.
 * 
 * Overlays können Maus-Input bekommen. Dazu reservieren sie sich einen rechteckigen Bereich, in dem dann alle Mausevents vom Spiel abgefangen werden und an
 * das Overlay gehen. Overlays können bestimmen, wie die Mausevents abgefangen werden: Entweder gar nicht, nur die die drüber gehen oder immer.
 * Hier ist eine Subtilität zu beachten: Wenn nur drüber-Events abgefangen werden, scrollt die Ansicht im CoreRenderer eventuell noch mit der Mausbewegung mit,
 * denn dann bekommt der Core-Renderer nach wie die Position der Maus, nur Klicks werden abgefangen. Wenn "immer" ausgewählt wird, wird dagegen alles abgefangen,
 * das Overlay darunter bekommt also keinen Input mehr.
 * 
 * Im Gegensatz zu CoreRenderern können Overlays keine eigenen Matrizen/Vertex Shader definieren.
 * (Können Sie schon, aber es wird nicht von der Grafikengine verwaltet und das Overlay müsste den aktuellen Status vorher sichern und nacher wiederherstellen).
 * Wenn render() aufgerufen wird, ist die Pipeline pixelgenau (1:1) definiert.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class Overlay {
    
    /**
     * Rendert das Overlay.
     * Muss alle notwendigen Renderbefehle an die Grafikkarte senden.
     * In der Regel dürfen keine Shader gesetzt werden.
     * Die Pipeline ist so konfiguriert, dass alle Koordinaten genau 1:1 Pixel auf dem Bildschirm entsprechen.
     */
    public abstract void render();

}
