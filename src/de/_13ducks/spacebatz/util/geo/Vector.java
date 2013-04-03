package de._13ducks.spacebatz.util.geo;

import de._13ducks.spacebatz.shared.CompileTimeParameters;

/**
 * Sowohl Position als auch Vektor.
 * Powered by CoR 2 (!)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Vector {

    public static final Vector ZERO = new Vector(0, 0);
    public final double x;
    public final double y;

    /**
     * Erstell einen neuen Vektor mit den angegebenen Koordinaten
     *
     * @param x x
     * @param y y
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Liefert eine Skalar multiplizierte Version dieses Vektors hinzu
     *
     * @param scalar
     * @return
     */
    public Vector multiply(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    /**
     * Liefert einen neuen Vektor, der das Ergebniss einer Summation dieses mit dem gegebenen Vektor darstellt
     *
     * @param vector der zweite summand
     * @return einen neuen Vektor, der das Ergebniss einer Summation dieses mit dem gegebenen Vektor darstellt
     */
    public Vector add(Vector vector) {
        return new Vector(x + vector.x, y + vector.y);
    }

    /**
     * Liefert eine invertierte Kopie dieses Vektors.
     *
     * @return
     */
    public Vector getInverted() {
        return new Vector(-x, -y);
    }

    /**
     * Normiert den Vektor.
     * Falls dieser Vektor der Nullvektor ist, wird der Nullvektor zurückgegeben.
     *
     * @return den Normierten zurück
     */
    public Vector normalize() {
        if (x != 0 || y != 0) {
            double fact = length();
            return new Vector(this.x / fact, this.y / fact);
        } else {
            return Vector.ZERO; // this
        }
    }

    /**
     * Berechnet die Länge des Vektors
     *
     * @return die Länge des Vektors
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Findet heraus, ob dieser Vektor zu dem gegeben Parallel ist.
     * Wie bei allen Vektorvergleichen gibt es eine Toleranz gegen Rundungsfehler.
     * Diese Toleranz beträgt ein Promille pro Richtung.
     * Dies ist eine Behandlung für Richtungsvektoren, ein eventuelles Auf-/Ineinanderliegen von Vektoren wird ignoriert.
     *
     * @param vec der andere Vektor
     * @return true, wenn parallel
     */
    public boolean isParallel(Vector vec) {
        vec = vec.normalize();
        Vector meNormal = this.normalize();
        return (vec.equals(meNormal) || vec.equals(meNormal.getInverted()));
    }

    /**
     * True, wenn der gleiche Vektor, nur in die andere Richtung zeigend
     * Länge egal.
     */
    public boolean isOpposite(Vector vec) {
        return this.normalize().equals(new Vector(-vec.x, -vec.y).normalize());
    }

    /**
     * Berechnet einen Schnittpunkt zwischen diesem und dem gegeben Vector.
     * Liefert null, falls die Vektoren parallel sind (auch, wenn sie aufeinander liegen!!!)
     *
     * @param vec der andere Vektor
     * @return Einen Stützvektor zum Schnittpunkt oder null, wenns keinen gibt.
     */
    public Vector intersectionWith(Vector mySPos, Vector otherS, Vector otherVec) {
        if (isParallel(otherVec)) {
            return null;
        }
        // Es gibt einen Schnittpunkt
        double fact;
        // Sonderfälle:
        if (this.x == 0) {
            // Implizit: otherVec.x != 0, weil sonst paralell
            fact = (mySPos.x - otherS.x) / otherVec.x;
        } else if (this.y == 0) {
            // Implizit: otherVec.y != 0, weil sonst paralell
            fact = (mySPos.y - otherS.y) / otherVec.y;
        } else if (otherVec.x == 0) {
            fact = (mySPos.y + (otherS.x - mySPos.x) / x * y - otherS.y) / otherVec.y;
        } else if (otherVec.y == 0) {
            fact = (mySPos.x + (otherS.y - mySPos.y) / y * x - otherS.x) / otherVec.x;
        } else {
            // Normalfall:
            // Diese Formel hab ich mir durch umwandeln von (a,b)+x(c,d)=(e,f)+y(g,h) gebildet
            fact = (otherS.y * this.x - mySPos.y * this.x - otherS.x * this.y + mySPos.x * this.y) / (otherVec.x * this.y - otherVec.y * this.x);
        }
        return otherS.add(otherVec.multiply(fact));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector) {
            Vector vec = (Vector) o;
            return (Math.abs(this.x - vec.x) < 0.001 && Math.abs(this.y - vec.y) < CompileTimeParameters.DOUBLE_EQUALS_DIST);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "v:" + x + "|" + y;
    }

    public Node toNode() {
        return new Node(x, y);
    }

    /**
     * Überprüft, ob der Vektor ok, ist. Bedeutet: Kein Wert undendlich und kein Wert NaN
     *
     * @return
     */
    public boolean isValid() {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
            return false;
        }
        return true;
    }
}
