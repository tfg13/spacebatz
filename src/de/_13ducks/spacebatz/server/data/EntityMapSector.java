package de._13ducks.spacebatz.server.data;

import java.util.ArrayList;
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
     * Eine Liste mit diesem Sektor und seinen Nachbarsektoren
     */
    private ArrayList<EntityMapSector> meAndMyNeighborSectors;

    /**
     * Konstruktor, erstellt einen neuen Sektor
     */
    public EntityMapSector() {
        habitants = new LinkedList<>();
        meAndMyNeighborSectors = new ArrayList<>();
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
        if (e == null) {
            throw new IllegalArgumentException("Kann NULL nicht hinzufügen!");
        } else if (habitants.contains(e)) {
            throw new IllegalArgumentException("Sector already contains Entity!");
        } else {
            habitants.add(e);
        }

    }

    /**
     * Entfernt eine Entity aus diesem Sektor
     *
     * @param e die zu entfernende Entity
     */
    public void removeEntity(Entity e) {
        if (e == null) {
            throw new IllegalArgumentException("Kann NULL nicht entfernen!");
        } else if (!habitants.contains(e)) {
            throw new IllegalArgumentException("Cannot remove Entity, it is not contained in this Sector!");
        } else {
            habitants.remove(e);
        }
    }

    /**
     * Eine Liste mit diesem Sektor und seinen Nachbarsektoren
     *
     * @return die Liste mit diesem und seinne NachbarSektoren
     */
    public ArrayList<EntityMapSector> getMeAndMyNeighborSectors() {
        return meAndMyNeighborSectors;
    }

    /**
     * Fügt einen neuen NachbarSektor hinzu
     *
     * @param neighbor der neue NachbarSektor
     */
    public void addNeighborSector(EntityMapSector neighbor) {
        meAndMyNeighborSectors.add(neighbor);
    }
}
