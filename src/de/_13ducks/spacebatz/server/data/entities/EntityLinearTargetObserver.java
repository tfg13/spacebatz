package de._13ducks.spacebatz.server.data.entities;

/**
 * Muss der Entity beim Streckenlaufen übergeben werden.
 * Wird von der Entity benachrichtigt, wenn das Ziel erfolgreich erreicht wurde, die Kollision die Bewegung abgebrochen hat, oder jemand anderes
 * die Bewegung ändert.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public interface EntityLinearTargetObserver {
    
    /**
     * Wird aufgerufen, wenn die Entity ihr Ziel erreicht hat.
     * Es ist erlaubt und sicher, in der Behandlung dieser Methode schon eine neue Bewegung zu starten.
     */
    public void targetReached();
    
    /**
     * Wird aufgerufen, wenn die Entity ihr Ziel nicht erreichen konnte, weil die Kollision das verhindert hat.
     * Sollte bei gut geplanten Routen nicht vorkommen.
     * Es ist erlaubt und sicher, in der Behandlung dieser Methode schon eine neue Bewegung zu starten.
     */
    public void movementBlocked();
    
    /**
     * Wird aufgerufen, wenn die Entity ihre Bewegung abbrechen musste, jemand neue Bewegungsbefehle erteilt hat.
     * Es ist SEHR SEHR GEFÄHRLICH und SEHR SEHR VERBOTEN in der Behandlung dieser Methode neue Bewegungen zu setzen.
     * Das kann in Endlosschleifen enden.
     * Javadoc nicht lesen + Methode falsch verwenden = RTFM!
     */
    public void movementAborted();

}
