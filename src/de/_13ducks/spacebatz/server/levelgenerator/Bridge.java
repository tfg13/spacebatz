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

import de._13ducks.spacebatz.shared.Position;
import java.util.ArrayList;

/**
 * Ein rechtecksähnliches Gebilde, das aus Dreiecken zusammengesetzt ist
 * soll einzelne Circles verbinden
 * @author Jojo
 */
public class Bridge {
    private Position a; // Ein Ende der Bridge
    private Position b; // Ein Ende der Bridge
    private ArrayList<Position> shape; // Randpunkte, 2 benachbarte spannen mit Zentrum ein Dreieck auf
    
    public Bridge(Position a, Position b, ArrayList<Position> shape) {
     this.a = a;
     this.b = b;
     this.shape = shape;
    }

    public ArrayList<Position> getShape() {
        return shape;
    }
}
