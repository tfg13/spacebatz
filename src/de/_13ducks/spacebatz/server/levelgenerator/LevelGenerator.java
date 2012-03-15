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
    private static final int texrock = 1;
    private static final int texground = 4;

    public static ServerLevel generateLevel() {
        ArrayList<Circle> circleList = new ArrayList<>();
        ArrayList<Bridge> bridgeList = new ArrayList<>();

        level = new ServerLevel(1000, 1000);
        ground = level.getGround();

        random = new Random(System.nanoTime());

        xSize = level.getSizeX();
        ySize = level.getSizeY();

        // Gegner-Spawn-Gebiet setzen:
//        EnemySpawnArea dangerZone = new EnemySpawnArea(1, 1, xSize - 2, ySize - 2);
//        dangerZone.setMaxSpawns(100);
//        level.addEnemySpawnArea(dangerZone);

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
        level.respawnX = circleList.get(0).getCenter().getX();
        level.respawnY = circleList.get(0).getCenter().getY();

        // Wände am Levelrand
        createWall(0, 0, 1, ySize - 1, level);
        createWall(0, ySize - 1, xSize - 1, ySize - 2, level);
        createWall(xSize - 1, ySize - 1, xSize - 2, 0, level);
        createWall(xSize - 1, 0, 0, 1, level);

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

        Conn.add(notConn.get(firstnearestcir));
        notConn.remove(firstnearestcir);
        Conn.add(notConn.get(0));
        notConn.remove(0);

        while (notConn.size() > 0) {
            // mit nächstem Circle verbinden
            int nearestcir = findNearestCircle(notConn.get(0), Conn);

            // Brücke bauen

            Conn.add(notConn.get(0));
            notConn.remove(0);
        }

        return bridges;
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

    private static ArrayList<Position> findInnerFields(ArrayList<Circle> circleList) {
        ArrayList<Position> innerFields = new ArrayList<>();

        // Für jeden Circle:
        for (int i = 0; i < circleList.size(); i++) {
            // Für jeden Randpunkt:
            for (int a = 0; a < circleList.get(i).getShape().size(); a++) {
                int z = a + 1;
                if (z >= circleList.get(i).getShape().size()) {
                    z = 0;
                }

                ArrayList<Position> trianglepos = findTriangleFields(circleList.get(i).getShape().get(a), circleList.get(i).getShape().get(z), circleList.get(i).getCenter());
                innerFields.addAll(trianglepos);
            }
        }



        return innerFields;
    }

    /**
     * Alle Punkte innerhalb eines Dreiecks finden
     */
    public static ArrayList<Position> findTriangleFields(Position xpos, Position ypos, Position zpos) {
        ArrayList<Position> triangleFields = new ArrayList<>();
        Position a; // oberster Punkt
        Position b; // mittlerer Punkt
        Position c; // unterster Punkt

        // Positionen nach y-Wert sortieren:
        if (xpos.getY() < ypos.getY() && xpos.getY() < zpos.getY()) {
            a = xpos;
            if (ypos.getY() < zpos.getY()) {
                b = ypos;
                c = zpos;
            } else {
                c = ypos;
                b = zpos;
            }
        } else if (ypos.getY() < zpos.getY()) {
            a = ypos;
            if (xpos.getY() < zpos.getY()) {
                b = xpos;
                c = zpos;
            } else {
                c = xpos;
                b = zpos;
            }
        } else {
            a = zpos;
            if (xpos.getY() < ypos.getY()) {
                b = xpos;
                c = ypos;
            } else {
                c = xpos;
                b = ypos;
            }
        }
        
        // Workaround bis mir was besseres einfällt
        if (a.getY() == b.getY()) {
            a.setY(a.getY() - 1);
        }

        // Kehrwert der Steigungen zwischen je 2 Punkten
        double ab = ((double) b.getX() - a.getX()) / (b.getY() - a.getY());
        if (a.getY() == b.getY()) {
            ab = 0;
        }
        double bc = ((double) c.getX() - b.getX()) / (c.getY() - b.getY());
        if (b.getY() == c.getY()) {
            bc = 0;
        }
        double ac = ((double) c.getX() - a.getX()) / (c.getY() - a.getY());
        if (a.getY() == c.getY()) {
            ac = 0;
        }

        double startx = a.getX(); // x-Wert, ab dem die Felder im Dreieck sind
        double endx = a.getX(); // x-Wert, bis zu dem die Felder im Dreieck sind

        if (ab > ac) {
            
            for (int y = a.getY(); y < b.getY(); y++) {
                startx += ac;
                endx += ab;
                for (int x = (int) startx; x <= Math.ceil(endx); x++) {
                    triangleFields.add(new Position(x, y));
                }
            }
            endx = b.getX();
            for (int y = b.getY(); y <= c.getY(); y++) {
                startx += ac;
                endx += bc;
                for (int x = (int) startx; x <= Math.ceil(endx); x++) {
                    triangleFields.add(new Position(x, y));
                }
            }
            
        } else {
            
            for (int y = a.getY(); y < b.getY(); y++) {
                startx += ab;
                endx += ac;
                for (int x = (int) startx; x <= Math.ceil(endx); x++) {
                    triangleFields.add(new Position(x, y));
                }
            }
            startx = b.getX();
            for (int y = b.getY(); y <= c.getY(); y++) {
                startx += bc;
                endx += ac;
                for (int x = (int) startx; x <= Math.ceil(endx); x++) {
                    triangleFields.add(new Position(x, y));
                }
            }
            
        }

        return triangleFields;
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
