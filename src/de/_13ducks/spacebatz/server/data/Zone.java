package de._13ducks.spacebatz.server.data;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Ein Bereich auf auf der Map des aktuellen Levels.
 * Ein Bereich ist immer Unterelement eines anderen Bereiches.
 * Es gibt einen globalen Bereich, der unendlich groß ist.
 * Ein Bereich hat eine Form, die alles sein kann, was java.awt.geom.Area erlaubt.
 * Bereichen können Werte in eine Hashmap zugewiesen werden.
 * Werte in diesen HashMaps werden an Unterzonen vererbt.
 * Unterzonen können die Werte aber mit eigenen überschreiben.
 *
 * @author tobi
 */
public class Zone {

    /**
     * Die Form dieses Gebiets selber.
     */
    private Area shape;
    /**
     * Der Name, der das Gebiet identifiziert.
     */
    private final String name;
    /**
     * Ist dies das große, globale Gebiet?.
     */
    private final boolean global;
    /**
     * Das globale Gebiet, ein Singleton.
     */
    private static Zone globalZone = new Zone();
    /**
     * Die Liste mit Untergebieten.
     * Untergebiete müssen in diesem Gebiet vollständig drin liegen *und* paarweise disjunkt sein.
     */
    private final ArrayList<Zone> subZones = new ArrayList<>();
    /**
     * Die Werte, die für dieses Gebiet gespeichert werden.
     */
    private HashMap<String, Object> values = new HashMap<>();
    /**
     * Das Obergebiet dieser Zone.
     * Nur global hat keine.
     */
    private final Zone parent;

    /**
     * Erstellt ein neues Untergebiet mit dem angegebenen Namen als Unterelement des angegebenen Areas.
     *
     * @param name Der Name dieses Gebietes, für Quests und debugging.
     * @param parent Das Obergebiet, zu dem das gehören soll.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Zone(String name, Area area, Zone parent) {
	if (name == null) {
	    throw new IllegalArgumentException("name must not be null!");
	}
	if (shape == null) {
	    throw new IllegalArgumentException("shape must not be null");
	}
	if (parent == null) {
	    throw new IllegalArgumentException("parent must not be null!");
	}
	if (parent.testSubArea(area)) {
	    subZones.add(this);
	} else {
	    throw new IllegalArgumentException("new zone does not fulfill requirements!");
	}
	this.name = name;
	global = false;
	this.parent = parent;
    }

    /**
     * Erstellt ein globales Gebiet.
     */
    private Zone() {
	name = "global";
	global = true;
	parent = null;
    }

    /**
     * Liefert das globale Gebiet, über das man an alle Untergebiete kommt.
     *
     * @return das globale Gebiet
     */
    public static Zone getGlobal() {
	return globalZone;
    }

    /**
     * Liefert die für den gegebenen Punkt am stärksten spezialisierte Zone.
     * Das ist also die kleinste Unterzone, die x, y enthält.
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return die gefundene Zone. Im Zweifelsfall die globale Zone.
     */
    public static Zone getMostSpecializedZone(double x, double y) {
	Zone current = globalZone;
	outer:
	while (true) {
	    for (Zone z : current.subZones) {
		if (z.contains(x, y)) {
		    current = z;
		    continue outer;
		}
	    }
	    break;
	}
	return current;
    }

    /**
     * Gibt den Namen des Gebiets zurück
     *
     * @return der Name des Gebiets
     */
    public String getName() {
	return name;
    }

    /**
     * Gibt true zurück wenn der angegebene Punkt enthalten ist
     *
     * @param x die X-Koordinate des Punkts
     * @param y die Y-Koordinate des Punkts
     * @return true wenn der Punkt enthalten ist, false wenn nicht
     */
    public boolean contains(double x, double y) {
	return global || shape.contains(x, y);
    }

    /**
     * Setzt einen Wert für diese Zone.
     * Dieser Wert fungiert als default-Wert für alle Unter-Gebiete.
     *
     * @param s der Schlüssel
     * @param value der Wert
     */
    public void setValue(String s, Object value) {
	values.put(s, value);
    }

    /**
     * Liefert einen gesetzten Wert mit dem gegebenen Schlüssel.
     * Wenn dieses Feld keinen eigenen Wert hat, wird der Wert des Obergebietes verwendet.
     *
     * @param s der Schlüssel
     * @return Wert dieses oder des Obergebietes(spätestens der Wert des globalen)
     */
    public Object getValue(String s) {
	Zone current = this;
	while (!current.values.containsKey(s) && !current.global) {
	    current = current.parent;
	}
	return current.values.get(s);
    }

    /**
     * Testet, ob eine Form als Untergebiet in Ordnung geht.
     * Prüft:
     * Ob die Form vollständig in diesem Gebiet drin liegt.
     * Ob die Form mit keinem anderen Untergebiet kollidiert.
     *
     * @param a die zu testende Form
     * @return true, wenn i. O.
     */
    private boolean testSubArea(Area a) {
	// Echtes Unterelement, wenn geschnitten mit diesem wieder dieses
	Area own = (Area) this.shape.clone();
	own.intersect(a);
	if (own.equals(a)) {
	    for (Zone z : subZones) {
		Area inter = (Area) z.shape.clone();
		inter.intersect(a);
		if (!inter.isEmpty()) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }
}
