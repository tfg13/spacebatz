package de._13ducks.spacebatz.server.levelgenerator;

import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.shared.Position;
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
    private static final int texrock = 1;
    private static final int texground = 4;

    public static ServerLevel generateLevel() {
        long start = System.currentTimeMillis();

        ArrayList<Circle> circleList = new ArrayList<>();
        ArrayList<Bridge> bridgeList = new ArrayList<>();

        level = new ServerLevel(1000, 1000);
        ground = level.getGround();

        random = new Random(System.nanoTime());

        xSize = level.getSizeX();
        ySize = level.getSizeY();



        for (int i = 0; i < 4 + random.nextInt(4); i++) {
            Position center = new Position((int) ((random.nextDouble() * 0.8 + 0.1) * xSize), (int) ((random.nextDouble() * 0.8 + 0.1) * ySize));
            int disthori = Math.min(center.getX(), xSize - center.getX());
            int distvert = Math.min(center.getY(), ySize - center.getY());
            int maxradius = Math.min(disthori, distvert) - 1;
            Circle circle = createCircle(center, maxradius);
            circleList.add(circle);
        }

        // Cicles durch Bridges verbinden
        ArrayList<Circle> circleList2 = (ArrayList<Circle>) circleList.clone();
        bridgeList = createBridges(circleList2);

        ArrayList<Position> innerFields = findInnerFields(circleList, bridgeList);

        // Default-Bodentextur:
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                ground[x][y] = texrock;
                level.getCollisionMap()[x][y] = true;
            }
        }
        for (int i = 0; i < innerFields.size(); i++) {
            if (innerFields.get(i).getX() < 0 || innerFields.get(i).getX() > xSize) {
                continue;
            }
            if (innerFields.get(i).getY() < 0 || innerFields.get(i).getY() > ySize) {
                continue;
            }
            ground[innerFields.get(i).getX()][innerFields.get(i).getY()] = texground;
            level.getCollisionMap()[innerFields.get(i).getX()][innerFields.get(i).getY()] = false;
        }

        // Respawn-Koordinaten setzen:
        level.respawnX = circleList.get(0).getCenter().getX();
        level.respawnY = circleList.get(0).getCenter().getY();

        // Wände am Levelrand
        createWall(0, 0, 1, ySize - 1, level);
        createWall(0, ySize - 1, xSize - 1, ySize - 2, level);
        createWall(xSize - 1, ySize - 1, xSize - 2, 0, level);
        createWall(xSize - 1, 0, 0, 1, level);

        System.out.println("LevelGenerator Zeit: " + (System.currentTimeMillis() - start) + " ms");
        return level;
    }

    /**
     * Erstellt eine grob kreisförmige Fläche auf der Map, wird für freie Fläche benutzt
     */
    public static Circle createCircle(Position center, int maxradius) {
        ArrayList<Position> shape = new ArrayList<>();

        int shapepoints = random.nextInt(11) + 10;

        int[] radius = new int[shapepoints];

        for (int i = 0; i < shapepoints; i++) {
            radius[i] = (int) (maxradius * (random.nextDouble() * 0.6 + 0.35));
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

    /**
     * Gibt eine Liste mit Bridges zurück, die alle Cicles zusammenhängend machen
     */
    private static ArrayList<Bridge> createBridges(ArrayList<Circle> notConn) {
        ArrayList<Bridge> bridges = new ArrayList<>();
        ArrayList<Circle> Conn = new ArrayList<>(); //Die Cicles, die noch nicht verbunden sind

        int firstnearestcir = findNearestCircle(notConn.get(0), notConn);
        // Brücke bauen
        Bridge firstbridge = buildBridge(notConn.get(0), notConn.get(firstnearestcir));
        bridges.add(firstbridge);

        Conn.add(notConn.get(firstnearestcir));
        notConn.remove(firstnearestcir);
        Conn.add(notConn.get(0));
        notConn.remove(0);

        while (notConn.size() > 0) {
            // mit nächstem Circle verbinden
            int nearestcir = findNearestCircle(notConn.get(0), Conn);

            // Brücke bauen
            Bridge bridge = buildBridge(notConn.get(0), Conn.get(nearestcir));
            bridges.add(bridge);

            Conn.add(notConn.get(0));
            notConn.remove(0);
        }

        return bridges;
    }

    private static Bridge buildBridge(Circle circlea, Circle circleb) {
        ArrayList<Position> shape = new ArrayList<>();
        Position a;
        Position b;
        if (circlea.getCenter().getX() < circleb.getCenter().getX()) {
            a = circlea.getCenter();
            b = circleb.getCenter();
        } else {
            a = circleb.getCenter();
            b = circlea.getCenter();
        }

        double length = Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
        int numberofparts = (int) (length / 30); // in einzelne Stücke unterteilen
        double partlength = length / numberofparts; // Länge eines Stücks
        
        double minwidth = 5 + random.nextDouble() * xSize * 0.01;
        double maxwidth = 20 + random.nextDouble() * xSize * 0.03;

        double lineangle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());
        double inverseangle = lineangle + 0.5 * Math.PI;
        if (inverseangle > Math.PI) {
            inverseangle -= 2 * Math.PI;
        }
        
        for (int i = 0; i <= numberofparts; i++) {
            Position middlepos = new Position((int) (a.getX() + Math.cos(lineangle) * partlength * i), (int) (a.getY() + Math.sin(lineangle) * partlength * i));
            double width = minwidth + random.nextDouble() * (maxwidth - minwidth);
            Position pos1 = new Position((int) (middlepos.getX() + Math.cos(inverseangle) * width), (int) (middlepos.getY() + Math.cos(inverseangle) * width));
            Position pos2 = new Position((int) (middlepos.getX() - Math.cos(inverseangle) * width), (int) (middlepos.getY() - Math.cos(inverseangle) * width));
            shape.add(pos1);
            shape.add(pos2);
        }
        
        Bridge bridge = new Bridge(a, b, shape);
        
        return bridge;
    }

    /**
     * Gibt zu dem übergebenen circle den nächsten aus der circleList zurück
     */
    private static int findNearestCircle(Circle circle, ArrayList<Circle> circleList) {
        int nearest = -1;
        double mindistance = 3133337;

        for (int i = 0; i < circleList.size(); i++) {
            int cx = circle.getCenter().getX();
            int cy = circle.getCenter().getY();
            int x = circleList.get(i).getCenter().getX();
            int y = circleList.get(i).getCenter().getY();

            double distance = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
            if (distance < mindistance && distance > 0) {
                mindistance = distance;
                nearest = i;
            }
        }

        return nearest;
    }

    private static ArrayList<Position> findInnerFields(ArrayList<Circle> circleList, ArrayList<Bridge> bridgeList) {
        ArrayList<Position> innerFields = new ArrayList<>();

        // Für jeden Circle:
        for (int i = 0; i < circleList.size(); i++) {
            // Für jeden Randpunkt:
            for (int a = 0; a < circleList.get(i).getShape().size(); a++) {
                int z = a + 1;
                if (z >= circleList.get(i).getShape().size()) {
                    z = 0;
                }

                ArrayList<Position> trianglepos = findTriangle(circleList.get(i).getShape().get(a), circleList.get(i).getShape().get(z), circleList.get(i).getCenter());
                innerFields.addAll(trianglepos);
            }
        }

        // Für jede Bridge:
        for (int i = 0; i < bridgeList.size(); i++) {
            // Für jeden Randpunkt:
            for (int a = 0; a < bridgeList.get(i).getShape().size() - 2; a++) {
                ArrayList<Position> trianglepos = findTriangle(bridgeList.get(i).getShape().get(a), bridgeList.get(i).getShape().get(a + 1), bridgeList.get(i).getShape().get(a + 2));
                innerFields.addAll(trianglepos);
                System.out.println("bla " + bridgeList.get(i).getShape().get(a).getX() + " " + bridgeList.get(i).getShape().get(a).getY());
            }
        }

        return innerFields;
    }

    /**
     * Alle Punkte innerhalb eines Dreiecks finden
     */
    public static ArrayList<Position> findTriangle(Position a, Position b, Position c) {
        ArrayList<Position> triangle = new ArrayList<>();

        // Äußere Grenzen
        int ymin = Math.max(0, Math.min(a.getY(), Math.min(b.getY(), c.getY())));
        int ymax = Math.min(ySize, Math.max(a.getY(), Math.max(b.getY(), c.getY())));
        int xmin = Math.max(0, Math.min(a.getX(), Math.min(b.getX(), c.getX())));
        int xmax = Math.min(xSize, Math.max(a.getX(), Math.max(b.getX(), c.getX())));

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
