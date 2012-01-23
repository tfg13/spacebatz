package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.client.Position;
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


        createWall(1, 1, 2, 15, level);
        createWall(2,15, 15,16, level);
        createWall(14,1,15,16, level);
        createWall(14,1, 1,2, level);

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
     */
    private static void createWall(int x1, int y1, int x2, int y2, Level level) {
        Wall collision = new Wall(x1, y1, x2, y2);
        drawPositions(findLine(new Position(x1, y1), new Position(x2, y2)), 3);
        level.addWall(collision);
    }
}
