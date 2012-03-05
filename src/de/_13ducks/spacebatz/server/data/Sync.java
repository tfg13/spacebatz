package de._13ducks.spacebatz.server.data;

/**
 * Klasse für Automatisch vom Netzwerksystem synchronisierte Objekte.
 * Alle diese Objekte haben eine netID, und eine Daten-Hashmap.
 * Ihre Erstellung, alle Zustandsänderungen in der Map und das Ableben von syncs wird automatisch vom UDP-System synchronisiert.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Sync {

    /**
     * Die netID der Entity.
     */
    public final int netID;

    public Sync(int netID) {
	this.netID = netID;
    }
}
