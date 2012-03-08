package de._13ducks.spacebatz.server.levelgenerator;

import de._13ducks.spacebatz.shared.Position;
import de._13ducks.spacebatz.server.data.EnemySpawnArea;
import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.shared.Level;
import java.util.ArrayList;
import java.util.Random;

/**
 * Erstellt einen Level
 *
 * @author Nikolaus
 */
public class LevelGenerator {

    /**
     * Erzeugt eine neues Level.
     *
     * @param xSize Größe in X-Richtung
     * @param ySize Größe in Y-Richtung
     */
    private static ServerLevel level;
    private static int[][] ground;
    private static Random random;
    private static int xSize;
    private static int ySize;
    private static Position center;
    private static final int texrock = 1;
    private static final int texground = 4;

    public static ServerLevel generateLevel() {
        ArrayList<Circle> circleList = new ArrayList<>();

        level = new ServerLevel(300, 300);
        ground = level.getGround();

        random = new Random(System.nanoTime());

        xSize = level.getSizeX();
        ySize = level.getSizeY();

        // Gegner-Spawn-Gebiet setzen:
        EnemySpawnArea dangerZone = new EnemySpawnArea(1, 1, xSize - 2, ySize - 2);
        dangerZone.setMaxSpawns(100);
        level.addEnemySpawnArea(dangerZone);

        Circle circle = findCircle();
        circleList.add(circle);
        ArrayList<Position> innerFields = findInnerFields(circleList);

        // Default-Bodentextur:
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                ground[x][y] = texrock;
                level.getCollisionMap()[x][y] = true;
            }
        }
        for (int i = 0; i < innerFields.size(); i++) {
            ground[innerFields.get(i).getX()][innerFields.get(i).getY()] = texground;
            level.getCollisionMap()[innerFields.get(i).getX()][innerFields.get(i).getY()] = false;
        }

        // Respawn-Koordinaten setzen:
        level.respawnX = center.getX();
        level.respawnY = center.getY();

        // WÃ¤nde am Levelrand
        createWall(0, 0, 1, ySize - 1, level);
        createWall(0, ySize - 1, xSize - 1, ySize - 2, level);
        createWall(xSize - 1, ySize - 1, xSize - 2, 0, level);
        createWall(xSize - 1, 0, 0, 1, level);

        return level;
    }

    public static Circle findCircle() {
        ArrayList<Position> shape = new ArrayList<>();

        int shapepoints = random.nextInt(11) + 10;
        int maxradius = Math.min(xSize, ySize) / 2;
        center = new Position(xSize / 2, ySize / 2);

        int[] radius = new int[shapepoints];

        for (int i = 0; i < shapepoints; i++) {
            radius[i] = (int) (maxradius * (random.nextDouble() * 0.5 + 0.45));
        }

        for (int v = 0; v < 1; v++) {
            for (int i = 0; i < shapepoints; i++) {
                int z = i + 1;
                if (z >= radius.length) {
                    z = 0;
                }

                int mid = (radius[i] + radius[z]) / 2;
                radius[i] = (radius[i] * 7 + mid * 3) / 10;
                radius[z] = (radius[z] * 7 + mid * 3) / 10;
            }
        }


        for (int i = 0; i < shapepoints; i++) {
            double angle = 2 * Math.PI * i / shapepoints;
            int x = (int) (radius[i] * Math.cos(angle)) + center.getX();
            int y = (int) (radius[i] * Math.sin(angle)) + center.getY();
            shape.add(new Position(x, y));
        }

        Circle circle = new Circle(center, shape);
        return circle;
    }

    public static ArrayList<Position> findInnerFields(ArrayList<Circle> circleList) {
        ArrayList<Position> innerFields = new ArrayList<>();

        // Für jeden Circle:
        for (int i = 0; i < circleList.size(); i++) {
            // Für jeden Randpunkt:
            for (int a = 0; a < circleList.get(i).getShape().size(); a++) {
                int z = a + 1;
                if (z >= circleList.get(i).getShape().size()) {
                    z = 0;
                }

                ArrayList<Position> trianglepos = findTriangle(circleList.get(i).getShape().get(a), circleList.get(i).getShape().get(z), center);
                innerFields.addAll(trianglepos);
            }
        }



        return innerFields;
    }

    /**
     * Alle Punkte innerhalb eines Dreiecks finden
     */
    public static ArrayList<Position> findTriangle(Position a, Position b, Position c) {
        ArrayList<Position> triangle = new ArrayList<>();

        // Ã¤uÃŸere Grenzen
        int ymin = Math.min(a.getY(), Math.min(b.getY(), c.getY()));
        int ymax = Math.max(a.getY(), Math.max(b.getY(), c.getY()));
        int xmin = Math.min(a.getX(), Math.min(b.getX(), c.getX()));
        int xmax = Math.max(a.getX(), Math.max(b.getX(), c.getX()));

        // Kehrwert der Steigungen zwischen je 2 Punkten
        double ab = ((double) b.getX() - a.getX()) / (b.getY() - a.getY());
        double bc = ((double) c.getX() - b.getX()) / (c.getY() - b.getY());
        double ca = ((double) a.getX() - c.getX()) / (a.getY() - c.getY());

        // jede Zeile durchgehen
        for (int y = ymin; y <= ymax; y++) {

            // Schnittpunkt der Geraden ab, bc und ca mit aktueller Zeile (y)
            double abintersect = (double) ((y - a.getY())) * ab + a.getX();
            double bcintersect = (double) ((y - b.getY())) * bc + b.getX();
            double caintersect = (double) ((y - c.getY())) * ca + c.getX();

            int startx = xmin; // x-Wert, ab dem die Felder im Dreieck sind
            int endx = xmax; // x-Wert, bis zu dem die Felder im Dreieck sind

            // grÃ¶ÃŸter und kleinster x-Wert von ab, bc und ca finden, der innerhalb von xmin und xmax ist
            if (abintersect >= xmin && abintersect <= xmax) {
                startx = (int) abintersect;
                endx = (int) Math.ceil(abintersect);
            }
            if (bcintersect >= xmin && bcintersect <= xmax) {
                startx = Math.min((int) bcintersect, startx);
                endx = Math.max((int) Math.ceil(bcintersect), endx);
            }
            if (caintersect >= xmin && caintersect <= xmax) {
                startx = Math.min((int) caintersect, startx);
                endx = Math.max((int) Math.ceil(caintersect), endx);
            }

            // alle gefundenen inneren Punkte in Liste tun
            for (int x = startx; x < endx; x++) {
                triangle.add(new Position(x, y));
            }
        }

        return triangle;
    }

    public static void drawPositions(ArrayList<Position> posarray, int groundnumber) {
        for (Position pos : posarray) {
            ground[(int) pos.getX()][(int) pos.getY()] = groundnumber;
        }
    }

    /*
     * Kriegt 2 Positions. Gibt eine Gerade dazwischen zurÃƒÂ¼ck (als ArrayList<Position>).
     */
    public static ArrayList<Position> findLine(Position alpha, Position beta) {
        ArrayList<Position> Returnthis = new ArrayList<>();

        int vX = (int) beta.getX() - (int) alpha.getX();
        int vY = (int) beta.getY() - (int) alpha.getY();
        if (Math.abs(vX) >= Math.abs(vY)) {
            if (vX > 0) {
                for (int i = 0; i <= vX; i++) {
                    Position argh = new Position(alpha.getX() + i, alpha.getY() + (i * vY / vX));
                    Returnthis.add(argh);
                }
            } else {
                for (int i = 0; i >= vX; i--) {
                    Position argh = new Position(alpha.getX() + i, alpha.getY() + (i * vY / vX));
                    Returnthis.add(argh);
                }
            }
        } else {
            if (vY > 0) {
                for (int i = 0; i <= vY; i++) {
                    Position argh = new Position(alpha.getX() + (i * vX / vY), alpha.getY() + i);
                    Returnthis.add(argh);
                }
            } else {
                for (int i = 0; i >= vY; i--) {
                    Position argh = new Position(alpha.getX() + (i * vX / vY), alpha.getY() + i);
                    Returnthis.add(argh);
                }
            }
        }
        return Returnthis;
    }

    public static ArrayList<Position> findRectangleHollow(int x1, int y1, int x2, int y2) {
        ArrayList<Position> Returnthis = new ArrayList<>();

        Returnthis.addAll(findLine(new Position(x1, y1), new Position(x2, y1)));
        Returnthis.addAll(findLine(new Position(x1, y1), new Position(x1, y2)));
        Returnthis.addAll(findLine(new Position(x1, y2), new Position(x2, y2)));
        Returnthis.addAll(findLine(new Position(x2, y1), new Position(x2, y2)));

        return Returnthis;
    }

    public static ArrayList<Position> findRectangleFull(int x1, int y1, int x2, int y2) {
        ArrayList<Position> Returnthis = new ArrayList<>();
        for (int a = x1; a <= x2; a++) {
            for (int b = y1; b <= y2; b++) {
                Returnthis.add(new Position(a, b));
            }
        }
        return Returnthis;
    }

    /**
     * Erzeug eine Mauer, mit Textur UND Kollisionsinformationen
     *
     * @param x1 X-Koordinate des Startpunktes der Wand
     * @param y1 Y-Koordinate des Startpunktes der Wand
     * @param x2 X-Koordinate des Endpunktes der Wand
     * @param y2 Y-Koordinate des Endpunktes der Wand
     * @param level das Level, dem die Wand hinzugefÃƒÆ’Ã‚Â¼gt wird
     */
    private static void createWall(int x1, int y1, int x2, int y2, Level level) {

        drawPositions(findLine(new Position(x1, y1), new Position(x2, y2)), texrock);
        setCollision(findLine(new Position(x1, y1), new Position(x2, y2)), true, level);
    }

    /**
     * Setzt die Kollision aller Felder in einer Liste
     *
     * @param positions die Liste der Positionen, deren Kollision gesezt werden soll
     * @param collision der Kollisionswert den die Felder haben sollen, true oder false
     * @param level das Level, auf dem die funktion operieren soll
     */
    private static void setCollision(ArrayList<Position> positions, boolean collision, Level level) {
        for (Position p : positions) {
            level.getCollisionMap()[(int) p.getX()][(int) p.getY()] = collision;

        }
    }
}
