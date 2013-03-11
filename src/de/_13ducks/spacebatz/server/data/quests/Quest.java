package de._13ducks.spacebatz.server.data.quests;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import java.util.Random;

/**
 * Superklasse für alle Quests.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class Quest {

    /**
     * Zeigt, dass der Quest derzeit noch läuft.
     */
    public static final int STATE_RUNNING = 1;
    /**
     * Zeigt, dass der Quest erfolgreich beendet wurde.
     */
    public static final int STATE_COMPLETED = 2;
    /**
     * Zeigt, dass der Quest fehlgeschlagen ist.
     */
    public static final int STATE_FAILED = 3;
    /**
     * Zeigt, dass der Quest ohne Ergebniss abgebrochen wurde.
     * Dieser Zustand ist etwas unklar definiert und sollte eher nicht verwendet werden.
     * Ist mehr so zum debuggen/zurückgeben bei nichtmehr behebbaren Fehlern.
     */
    public static final int STATE_ABORTED = 4;
    /**
     * Zufälliger Wert zwischen 0 und Servertickrate.
     * Zur automatischen Lastverteilung, immer bei diesem (x-ten) Tick einer Sekunde wird checkState aufgerufen
     */
    private final int randomTick = (int) (Math.random() * (1 / CompileTimeParameters.SERVER_TICKRATE));
    /**
     * Eindeutige ID für diese Instanz.
     * Auf dem Client gleich, zur Zuordnung.
     */
    public final int questID = new Random(System.nanoTime()).nextInt();

    /**
     * Wird jeden Tick ein Mal aufgerufen.
     * Kann vom Quest dazu verwendet werden, das Spielgeschehen zu beeinflussen.
     * Sollte keine zu aufwändigen Berechungen machen. Die Logik dafür sollte eher schon vorher, bei der Mapberechnung passieren.
     * Sollte nicht die Siegbedingungen testen, das sollte die Methode checkState() machen, die seltener ausgeführt wird.
     */
    public abstract void tick();

    /**
     * Interne Testmethode, wird bei jedem Tick vom QuestManager aufgerufen.
     * Ruft im Prinzip nur checkState() auf, verteilt dabei aber die Last.
     *
     * @return der Zustand des Quests
     */
    public final int check() {
        if (Server.game.getTick() % CompileTimeParameters.SERVER_TICKRATE == randomTick) {
            return checkState();
        }
        return -1;
    }

    /**
     * Quests müssen diese Methode überschreiben, und ein sinnvolles Ergebniss zurückliefern.
     * Diese Methode soll berechnen, ob der Quest fertig ist (completed/failed), noch läuft oder abgebrochen wurde.
     * Diese Methode wird ein Mal pro Sekunde aufgerufen.
     * Die Last wird bei mehreren Quests automatisch über die gesamte Sekunde verteilt.
     *
     * @return der Zustand des Quests
     */
    protected abstract int checkState();

    /**
     * Quests müssen diese Methode überschreiben und einen sinnvollen Namen zurückliefern.
     *
     * @return Name des Quests
     */
    public abstract String getName();

    /**
     * True, wenn versteckt.
     * Versteckte Quests werden dem Client nicht gesendet.
     *
     * @return true, wenn hidden.
     */
    public abstract boolean isHidden();

    /**
     * Liefert alle Daten, um den Quest an den Client zu schicken.
     * Erstes Byte: Dem Client bekannte Quest-ID. Rest: Daten in dem Client bekanntem Format
     * Wird für versteckte Quests nicht aufgerufen.
     *
     * @return ID
     */
    public abstract byte[] getClientData();
}
