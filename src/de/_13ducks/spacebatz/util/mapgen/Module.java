package de._13ducks.spacebatz.util.mapgen;

import java.util.HashMap;

/**
 * Ein Mapgenerator-Modul
 *
 * Jedes Modul hat einen eindeutigen Namen, der nur aus Buchstaben besteht und der einzigartig ist.
 * Ein Modul kann verschiedene Funktionen bereitstellen. Eine Funktion ist z.B. "map überhaupt erst einmal erstellen" aka CREATE_POLY
 * Bestimmte Dinge sind beim Prozess des Map-Erstellens einfach zwingend notwendig und die MapParameter überwachen das, indem sie darauf achten,
 * das alle Grundfunktionen vorhanden sind.
 *
 * Die Erstellung der Map gliedert sich in 2 Phasen.
 * 1. Operationen auf Graphen, also nicht-gerasterten Datenstrukturen.
 * 2. Operationen auf der Rastermap, die später an das Spiel übergeben wird.
 *
 * Module können genau einen Tpy der beiden haben, außer sie sind vom Typ RASTERIZE, erledigen also gerade den Umwandlungsschritt von 1 nach 2.
 * Um eine Map erstellen zu können, muss mindestens ein Modul vorhanden sein, das mit Rastermaps arbeitet (CREATE_RASTER), oder eins mit Polgonen (CREATE_POLY) und ein Umwandler (RASTERIZE).
 *
 * Module bekommen beim Aufruf durch den MapGenerator Parameter (übliche String, String HashMap) übergeben.
 * Von dort ist alles enthalten, was das Module benötigt, um zu arbeiten. Module können anzeigen, dass sie einen Seed benötigen, dann wird der MapGenerator einen nach einem
 * deterministtischen Verfahren erstellen und unter dem Eintrag ("SEED") ablegen. Dieser Seed muss unbedingt verwendet werden, es darf niemals selbst (Pseudo-) Zufall erzeugt werden,
 * denn die Map-Erstellung muss für Debug-Zwecke vollständig Deterministisch ablaufen.
 *
 * Um die Reihenfolge festzulegen, in denen die Module aufgerufen werden, liest der MapGenerator die in den MapParameters gespeicherte Priority aus.
 * Kleine Nummern kommen zuerst, große danach. Nach dem RASTERIZE-Module (das selber kein Prio hat) beginnen die Zahlen erneut bei 0.
 *
 * Um sicherzustellen, das nur sinnvolle Reihenfolgen akzeptiert werden, können optional abhängigkeiten von Funktionen (nicht speziellen Modulen) definiert werden.
 * Dann kann z.B. das vorhergehende Modul wichtige Informationen in Metadaten ablegen, die anschließend ausgelesen werden.
 * Abhängigkeiten sind optional, einige werden aber immer impliziert, z.B. hängen alle Aktivitäten von einer vorausgehenden CREATE_XYZ ab etc.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class Module {

    /**
     * Liefert den Namen des Moduls. Dieser Name darf nur Buchstaben enthalten und muss einzigartig sein.
     *
     * @return den Namen des Moduls.
     */
    public abstract String getName();

    /**
     * True, wenn das Module einen Seed für Zufallsberechnungen braucht.
     * Dann wird in den Parametern einer vom MapGen erzeugt. (heißt SEED)
     * Dieser Seed muss verwendet werden, das Verfahren anschließend vollständig deterministisch sein.
     *
     * @return true, wenn seed, false sonst
     */
    public abstract boolean requiresSeed();

    /**
     * Liefert ein Array mit allen Funktionen, die dieses Modul bietet.
     * Funktionen sind standardisierte Begriffe. Anhand dieser, können bereits die MapParameters prüfen, ob ein Auswahl von Modulen überhaupt eines enthält, das die Map erzeugt.
     *
     * @return ein Array mit allen von diesem Modul bereitgestellten Funktionen
     */
    public abstract String[] provides();

    /**
     * True, wenn das Modul mit Polygonen arbeitet, false wenn auf Rastern gearbeitet wird.
     * Diese Funktion wird für RASTERIZE-Module nicht aufgerufen und ist daher dort nicht definiert.
     *
     * @return true, wenn Polygon-Phase, false wenn Raster
     */
    public abstract boolean computesPolygons();

    /**
     * Ein (im Zweifelsfall leeres) Array mit Funktionen, die dieses Modul unbedingt benötigt, bevor es laufen kann.
     * Dinge wie "RASTERIZE oder CREATE_RASTER" vor Raster-Modulen werden implizit angenommen und brauchen nicht aufzutauchen.
     *
     * @return Array mit Abhängigkeiten
     */
    public abstract String[] requires();

    /**
     * Wichtigste Methode, veranlasst das Modul zu arbeiten.
     * Wird immer mit einer Map aufgerufen, auch für CREATE_XYZ-Module.
     * Von diesen wird aber erwartet, dass sie mit leeren Maps umgehen können.
     * Das Modul soll die gebene Map direkt manipulieren.
     *
     * @param map der aktuelle Bearbeitungszustand der Map
     * @params parameters die Parameter für dieses Modul
     */
    public abstract void computeMap(InternalMap map, HashMap<String, String> parameters);
}
