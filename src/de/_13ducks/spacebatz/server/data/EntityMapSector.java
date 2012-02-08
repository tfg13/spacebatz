package de._13ducks.spacebatz.server.data;

import java.util.LinkedList;

/**
 * Ein Sektor der EntityMap mit der Liste der "Einwohner".
 *
 * Das Level wird in eine Anzahl Quadratischer Sektoren zerlegt, die von dieser Klasse repräsentiert wertden.
 *
 * @author michael
 */
public class EntityMapSector {

    /**
     * Die Liste mit Entities die gerade in diesem Sektor registriert sind.
     */
    private LinkedList<Entity> habitants;

    /**
     * Konstruktor, erstellt einen neuen Sektor
     */
    public EntityMapSector() {
        habitants = new LinkedList<>();
    }

    /**
     * Gibt die Einwohnerliste des Sektors zurück.
     *
     * @return eine Liste mit Entities, die gerade in diesem Sektor registriert sind.
     */
    public LinkedList<Entity> getEntities() {
        return habitants;
    }

    /**
     * Registriert eine neue Entity in diesem Sektor
     *
     * @param e die neue Entity
     */
    public void addEntity(Entity e) {
        if (!habitants.contains(e)) {
            habitants.add(e);
        } else {
            throw new IllegalArgumentException("Sector already contains Entity!");
        }

    }

    /**
     * Entfernt eine Entity aus diesem Sektor
     *
     * @param e die zu entfernende Entity
     */
    public void removeEntity(Entity e) {
        if (habitants.contains(e)) {
            habitants.remove(e);
        } else {
            throw new IllegalArgumentException("Cannot remove Entity, it is not contained in this Sector!");
        }
    }
}
