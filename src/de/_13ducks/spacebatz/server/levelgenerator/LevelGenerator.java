package de._13ducks.spacebatz.server.levelgenerator;

import de._13ducks.spacebatz.client.Position;
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
    private static int[][] ground;

    public static Level generateLevel(ServerLevel level) {

        Random random = new Random(System.nanoTime());

        ground = level.getGround();

        int xSize = ground.length;
        int ySize = ground[0].length;

        // Respawn-Koordinaten setzen:
        level.respawnX = 3;
        level.respawnY = 3;

        // Default-Bodentextur:
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                ground[x][y] = 1;
            }
        }
        // Ein Paar Krater zufällig verteilen
        for (int i = 0; i < 50; i++) {
            ground[random.nextInt(xSize)][random.nextInt(ySize)] = 2;
        }


        // Wände am Levelrand
        createWall(0, 0, 1, ySize - 1, level);
        createWall(0, ySize - 1, xSize - 1, ySize - 2, level);
        createWall(xSize - 1, ySize - 1, xSize - 2, 0, level);
        createWall(xSize - 1, 0, 0, 1, level);



        // "Rennstrecke"
        createWall(3, 19, 40, 19, level);
        createWall(40, 19, 40, 40, level);
        createWall(40, 40, 20, 40, level);
        ground[2][19] = 3;
        ground[1][19] = 3;

//        // Linien
//        for (int i = 10; i <= 20; i++) {
//            ground[i][20] = 3;
//            ground[20][i] = 3;
//        }








//        // Volles Rechteck        
//        ArrayList<Position> blub = new ArrayList<>();
//        blub.addAll(findRectangleFull(90, 110, 100, 120));
//        drawPositions(blub, 2);
//
//
//        // Hohles Rechteck
//        int leftborder = random.nextInt(xSize / 3) + 2;
//        int rightborder = xSize - random.nextInt(xSize / 3) - 2;
//        int topborder = random.nextInt(ySize / 3) + 2;
//        int bottomborder = ySize - random.nextInt(ySize / 3) - 2;
//
//        ArrayList<Position> bla = new ArrayList<>();
//        bla.addAll(findRectangleHollow(leftborder, topborder, rightborder, bottomborder));
//        drawPositions(bla, 3);

        level.setGround(ground);

        return level;
    }

    public static void drawPositions(ArrayList<Position> posarray, int groundnumber) {
        for (Position pos : posarray) {
            ground[(int) pos.getX()][(int) pos.getY()] = groundnumber;
        }
    }

    /*
     * Kriegt 2 Positions. Gibt eine Gerade dazwischen zurück (als ArrayList<Position>).
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
     * @param level das Level, dem die Wand hinzugefügt wird
     */
    private static void createWall(int x1, int y1, int x2, int y2, Level level) {

        drawPositions(findLine(new Position(x1, y1), new Position(x2, y2)), 3);
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

    /**
     * Erstellt ein Space-Level
     *
     * @param level
     * @return
     */
    public static Level generateSpaceLevel(ServerLevel level) {

        Random random = new Random(System.nanoTime());

        int width = level.getSizeX();
        int height = level.getSizeY();

        // Respawn-Koordinaten setzen:
        level.respawnX = 3;
        level.respawnY = 3;


        int[][] tex = new int[width][height];

        // unsere eigenen konstanten für hintergrundtexturen:
        int SPACE = 100;
        int METEROID = 101;


        // Weltraum-Hintergrund:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tex[x][y] = SPACE;
                level.getCollisionMap()[x][y] = true;
            }
        }

        // Meteroid erzeugen:
        for (int i = 0; i < 50; i++) {
            int px = random.nextInt((int) (width * 0.7));
            int py = random.nextInt((int) (height * 0.7));
            int size = random.nextInt(8);
            for (int x = px - size; x < px + size; x++) {
                for (int y = py - size; y < py + size; y++) {
                    if (0 < x && x < width && 0 < y && y < height) {
                        tex[x][y] = METEROID;
                        level.getCollisionMap()[x][y] = false;
                    }

                }
            }
        }



        // Weltraum-Hintergrund Textur:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tex[x][y] == SPACE) {
                    level.setSingleTile(x, y, 1 + random.nextInt(6));
                }
            }
        }

        // Meteroid Textur:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tex[x][y] == METEROID) {
                    // env enthält die 9 felder um xy, wobei xy das zentrale feld ist

                    int upperLeft = tex[x - 1][y + 1];
                    int upper = tex[x][y + 1];
                    int upperRight = tex[x + 1][y + 1];
                    int left = tex[x - 1][y];

                    int right = tex[x + 1][y];
                    int bottomLeft = tex[x - 1][y - 1];
                    int bottom = tex[x][y - 1];
                    int bottomRight = tex[x + 1][y - 1];

                    /**
                     * Ecken
                     */
                    // linke obere ecke
                    if (upper == SPACE && left == SPACE && right == METEROID && bottom == METEROID) {
                        level.setSingleTile(x, y, 8);
                    }
                    // rechte obere ecke
                    if (upper == SPACE && left == METEROID && right == SPACE && bottom == METEROID) {
                        level.setSingleTile(x, y, 9);
                    }
                    // linke untere ecke
                    if (upper == METEROID && left == SPACE && right == METEROID && bottom == SPACE) {
                        level.setSingleTile(x, y, 18);
                    }
                    // rechte untere ecke
                    if (upper == METEROID && left == METEROID && right == SPACE && bottom == SPACE) {
                        level.setSingleTile(x, y, 19);
                    }

                    /**
                     * Kanten
                     */
                    // linke kante
                    if (upper == METEROID && left == SPACE && right == METEROID && bottom == METEROID) {
                        level.setSingleTile(x, y, 10);
                    }
                    // rechte kante
                    if (upper == METEROID && left == METEROID && right == SPACE && bottom == METEROID) {
                        level.setSingleTile(x, y, 11);
                    }
                    // obere kante
                    if (upper == SPACE && left == METEROID && right == METEROID && bottom == METEROID) {
                        level.setSingleTile(x, y, 12);
                    }
                    // untere kante
                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == SPACE) {
                        level.setSingleTile(x, y, 13);
                    }

                    /**
                     * Innere Ecken
                     */
                    // rechte untere innere ecke
                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == METEROID && upperLeft == SPACE) {
                        level.setSingleTile(x, y, 14);
                    }
                    // linke untere innere ecke
                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == METEROID && upperRight == SPACE) {
                        level.setSingleTile(x, y, 15);
                    }
                    // rechte obere innere ecke
                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == METEROID && bottomRight == SPACE) {
                        level.setSingleTile(x, y, 17);
                    }
                    // untere kante
                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == METEROID && bottomLeft == SPACE) {
                        level.setSingleTile(x, y, 16);
                    }

                    if (upper == METEROID && left == METEROID && right == METEROID && bottom == METEROID) {
                        level.setSingleTile(x, y, 20);
                    }


                }
            }
        }





        return level;
    }
}
