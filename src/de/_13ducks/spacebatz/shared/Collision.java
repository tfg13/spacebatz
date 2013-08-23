package de._13ducks.spacebatz.shared;

/**
 * Hauptmethoden zur Kollisionsberechnung.
 *
 * Ausgelagert, weil sehr häufig fast die gleiche Methode vorhanden war.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Collision {

    /**
     * Utility-Class, privater Konstruktor
     */
    private Collision() {
    }

    public static CollisionResult computeCollision(double fromX, double fromY, double toX, double toY, double size, boolean[][] collisionMap) {
        CollisionResult result = new CollisionResult();
        result.maxX = toX;
        result.maxY = toY;
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - size / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + size / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - size / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + size / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int searchX = moveAreaStartX; searchX < moveAreaEndX; searchX++) {
            for (int searchY = moveAreaStartY; searchY < moveAreaEndY; searchY++) {
                if (collisionMap[searchX][searchY] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + size / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + size / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((size / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                            result.collidingBlock[0] = searchX;
                            result.collidingBlock[1] = searchY;
                        }
                    }
                }
            }
        }
        double sx = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann ohne kollision:
            sx = fromX + smallestD * deltaX;
        }

        // Für die Y-Berechung die Werte zurücksetzten, für die Block-Berechung aber behalten!
        double globalsmallestD = smallestD;
        smallestD = Double.MAX_VALUE;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int searchX = moveAreaStartX; searchX < moveAreaEndX; searchX++) {
            for (int searchY = moveAreaStartY; searchY < moveAreaEndY; searchY++) {
                if (collisionMap[searchX][searchY] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + size / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + size / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((size / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                        // Näher als die von X?
                        if (d < globalsmallestD) {
                            globalsmallestD = d;
                            result.collidingBlock[0] = searchX;
                            result.collidingBlock[1] = searchY;
                        }
                    }
                }
            }
        }
        double sy = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann
            sy = fromY + smallestD * deltaY;
        }

        // Gab es eine Kollision?
        if (!Double.isNaN(sx)) {
            result.maxX = sx;
            result.collides = true;
        }
        if (!Double.isNaN(sy)) {
            result.maxY = sy;
            result.collides = true;
        }

        return result;
    }

    /**
     * Das Ergebniss einer Kollisionsberechnung
     */
    public static class CollisionResult {

        /**
         * True, wenn eine Kollision gefunden wurde, die gewünschte Bewegung also Korrigiert werden muss.
         * Wenn false, ist collidingBlock nicht definiert, maxX und maxY sind die gewünschte to-Position.
         */
        public boolean collides;
        /**
         * Die X-Position, zu der in der aktuellen Richtung maximal noch gefahren werden kann, um gerade so noch nicht in eine Kollision zu geraten.
         */
        public double maxX;
        /**
         * Die Y-Position, zu der in der aktuellen Richtung maximal noch gefahren werden kann, um gerade so noch nicht in eine Kollision zu geraten.
         */
        public double maxY;
        /**
         * Der Block, mit dem man als erstes Zusammenstoßen würde, wenn man die gewünschte Bewegung durchführt.
         * Das muss nicht notwendigerweise der einzige sein, ist aber garantiert der nächste.
         */
        public int[] collidingBlock = new int[2];
    }
}
