/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
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
        ArrayList<Bridge> bridgeList;

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

        ArrayList<Position> innerFields = findInnerFields(circleList, bridgeList);

        // Default-Textur (Felsen):
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                ground[x][y] = texrock;
                level.getCollisionMap()[x][y] = true;
            }
        }

        // Begehbare Felder
        for (int i = 0; i < innerFields.size(); i++) {
            if (innerFields.get(i).getX() < 0 || innerFields.get(i).getX() >= xSize) {
                continue;
            }
            if (innerFields.get(i).getY() < 0 || innerFields.get(i).getY() >= ySize) {
                continue;
            }
            ground[innerFields.get(i).getX()][innerFields.get(i).getY()] = texground;
            level.getCollisionMap()[innerFields.get(i).getX()][innerFields.get(i).getY()] = false;
        }

        // Manche begehbaren Felder zu zerstöbaren Bergen machen:
        createDestroyableBlocks();

        // Rohstoffe in Berge setzen:
        createOre();

        // Spawn-Koordinaten setzen:
        setSpawn(circleList.get(0).getCenter());

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

        double minwidth = 8 + random.nextDouble() * xSize * 0.01;
        double maxwidth = 20 + (random.nextDouble() + 0.1) * xSize * 0.08;

        double lineangle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());
        double inverseangle = lineangle + 0.5 * Math.PI;
        if (inverseangle > Math.PI) {
            inverseangle -= 2 * Math.PI;
        }

        for (int i = 0; i <= numberofparts; i++) {
            Position middlepos = new Position((int) (a.getX() + Math.cos(lineangle) * partlength * i), (int) (a.getY() + Math.sin(lineangle) * partlength * i));
            double width = minwidth + random.nextDouble() * (maxwidth - minwidth);
            Position pos1 = new Position((int) (middlepos.getX() + Math.cos(inverseangle) * width), (int) (middlepos.getY() + Math.sin(inverseangle) * width));
            width = minwidth + random.nextDouble() * (maxwidth - minwidth);
            Position pos2 = new Position((int) (middlepos.getX() - Math.cos(inverseangle) * width), (int) (middlepos.getY() - Math.sin(inverseangle) * width));
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

                ArrayList<Position> trianglepos = findTriangleFields(circleList.get(i).getShape().get(a), circleList.get(i).getShape().get(z), circleList.get(i).getCenter());
                innerFields.addAll(trianglepos);
            }
        }

        // Für jede Bridge:
        for (int i = 0; i < bridgeList.size(); i++) {
            // Für jeden Randpunkt:
            for (int a = 0; a < bridgeList.get(i).getShape().size() - 2; a++) {
                ArrayList<Position> trianglepos = findTriangleFields(bridgeList.get(i).getShape().get(a), bridgeList.get(i).getShape().get(a + 1), bridgeList.get(i).getShape().get(a + 2));
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
            ground[pos.getX()][pos.getY()] = groundnumber;
        }
    }

    /**
     * Setzt zerstörbare Blöcke auf die Map
     */
    private static void createDestroyableBlocks() {
        // Zufällige Werte für alle freien Felder!
        for (int i = 0; i < ground[0].length; i++) {
            for (int j = 0; j < ground.length; j++) {
                if (ground[i][j] == 4) {
                    ground[i][j] = -1 - random.nextInt(100);
                }
            }
        }

        // Felder an Nachbarfelder anpassen -> Berge
        for (int a = 0; a < 16; a++) {
            for (int i = 0; i < ground[0].length; i++) {
                for (int j = 0; j < ground.length; j++) {
                    if (ground[i][j] < 0) {
                        int neighbours = 0;

                        // direkte Nachbarn
                        if (i > 0 && (ground[i - 1][j] < -80 || ground[i - 1][j] == 1)) {
                            neighbours += 2;
                        }
                        if (i < xSize - 1 && (ground[i + 1][j] < -80 || ground[i + 1][j] == 1)) {
                            neighbours += 2;
                        }
                        if (j > 0 && (ground[i][j - 1] < -80 || ground[i][j - 1] == 1)) {
                            neighbours += 2;
                        }
                        if (j < ySize - 1 && (ground[i][j + 1] < -80 || ground[i][j + 1] == 1)) {
                            neighbours += 2;
                        }

                        // Nachbarn über Ecke
                        if (i > 0 && j > 0 && (ground[i - 1][j - 1] < -80 || ground[i - 1][j - 1] == 1)) {
                            neighbours++;
                        }
                        if (i < xSize - 1 && j < ySize - 1 && (ground[i + 1][j + 1] < -80 || ground[i + 1][j + 1] == 1)) {
                            neighbours++;
                        }
                        if (i < xSize - 1 && j > 0 && (ground[i + 1][j - 1] < -80 || ground[i + 1][j - 1] == 1)) {
                            neighbours++;
                        }
                        if (i > 0 && j < ySize - 1 && (ground[i - 1][j + 1] < -80 || ground[i - 1][j + 1] == 1)) {
                            neighbours++;
                        }

                        ground[i][j] += (12 - neighbours) * random.nextInt(15) - neighbours * random.nextInt(30);
                        if (ground[i][j] < -100) {
                            ground[i][j] = -100;
                        } else if (ground[i][j] >= 0) {
                            ground[i][j] = -1;
                        }
                    }
                }
            }
        }

        // Zufallswerte wieder zu richtigen Texturen machen:
        for (int i = 0; i < ground[0].length; i++) {
            for (int j = 0; j < ground.length; j++) {
                if (ground[ i][j] < -80) {
                    ground[i][j] = 2;
                    level.getCollisionMap()[i][j] = true;
                } else if (ground[ i][j] < 0) {
                    ground[i][j] = 4;
                }
            }
        }
    }

    /**
     * Setzt den Spielerspawn
     */
    public static void setSpawn(Position a) {
        level.respawnX = a.getX();
        level.respawnY = a.getY();

        // Umliegende Felder freiräumen:
        int spawnsize = 4;
        for (int x = a.getX() - spawnsize; x <= a.getX() + spawnsize; x++) {
            for (int y = a.getY() - spawnsize; y <= a.getY() + spawnsize; y++) {
                if (groundExists(x, y)) {
                    ground[x][y] = 4;
                    level.getCollisionMap()[x][y] = false;
                }
            }
        }
    }

    /**
     * Setzt Rohstoffe in Berge
     */
    public static void createOre() {
        ArrayList<Position> hill = new ArrayList<>(); // Alle Bergfelder (zerstöbare Blöcke)

        // Alle Bergfelder suchen:
        for (int i = 0; i < ground[0].length; i++) {
            for (int j = 0; j < ground.length; j++) {
                if (ground[i][j] == 2) {
                    hill.add(new Position(i, j));
                }
            }
        }

        // 200 Ressourcen-Häufen setzen
        for (int a = 0; a < 200; a++) {
            ArrayList<Position> res = new ArrayList<>(); // Die Bergfelder, die zu Ressourcenblöcken werden

            // zufälliges Bergfeld:
            Position firstpos = hill.get(random.nextInt(hill.size()));
            res.add(firstpos);

            int blub = random.nextInt(8) + 3;
            
            for (int b = 0; b < blub; b++) {      
                Position bla = res.get(random.nextInt(res.size()));
                // direkte Nachbarn
                if (bla.getX() > 0 && (ground[bla.getX() - 1][bla.getY()] < -80 || ground[bla.getX() - 1][bla.getY()] == 2)) {
                    res.add(new Position(bla.getX() - 1, bla.getY()));
                }
                if (bla.getX() < xSize - 1 && (ground[bla.getX() + 1][bla.getY()] < -80 || ground[bla.getX() + 1][bla.getY()] == 2)) {
                    res.add(new Position(bla.getX() + 1, bla.getY()));
                }
                if (bla.getY() > 0 && (ground[bla.getX()][bla.getY() - 1] < -80 || ground[bla.getX()][bla.getY() - 1] == 2)) {
                    res.add(new Position(bla.getX(), bla.getY() - 1));
                }
                if (bla.getY() < ySize - 1 && (ground[bla.getX()][bla.getY() + 1] < -80 || ground[bla.getX()][bla.getY() + 1] == 2)) {
                    res.add(new Position(bla.getX(), bla.getY() + 1));
                }
            }

            // Textur
            for (int i = 0; i < res.size(); i++) {
                ground[res.get(i).getX()][res.get(i).getY()] = 6;
            }

            // Ressourcenfelder aud der Bergfeld-Liste entfernen
            hill.removeAll(res);
        }
    }

    /**
     * Testet, ob dieses Feld gültig (= innerhalb der Map) ist
     */
    public static boolean groundExists(int x, int y) {
        if (x < 0 || x >= xSize || y < 0 || y > ySize) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Kriegt 2 Positions. Gibt eine Gerade dazwischen zurück (als ArrayList<Position>).
     */
    public static ArrayList<Position> findLine(Position alpha, Position beta) {
        ArrayList<Position> Returnthis = new ArrayList<>();

        int vX = beta.getX() - alpha.getX();
        int vY = beta.getY() - alpha.getY();
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

    /**
     * Erzeug eine Mauer, mit Textur UND Kollisionsinformationen
     *
     * @param x1 X-Koordinate des Startpunktes der Wand
     * @param y1 Y-Koordinate des Startpunktes der Wand
     * @param x2 X-Koordinate des Endpunktes der Wand
     * @param y2 Y-Koordinate des Endpunktes der Wand
     * @param level das Level, dem die Wand hinzugefügt wird
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
            level.getCollisionMap()[p.getX()][p.getY()] = collision;

        }
    }
}
