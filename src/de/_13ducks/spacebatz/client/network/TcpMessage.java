package de._13ducks.spacebatz.client.network;

/**
 * Eine Klasse , um Tcp-Messages zwischenzuspeichern, bis der Hauptthread sie abarbeitet
 * 
 * @author michael
 */
class TcpMessage {

    /**
     * Die Kommando-ID des Packets
     */
    private byte cmdID;
    /**
     * Die Daten des PAckets
     */
    private byte[] data;
    /**
     * Gibt an, ob die Nachricht schon verarbeitet wurde
     */
    private boolean computed;

    /**
     * Initialisiert das Packet mit Daten
     * @param cmdID die KomamndoID
     * @param data die Daten
     */
    public TcpMessage(byte cmdID, byte data[]) {
        this.cmdID = cmdID;
        this.data = data;
        computed = false;
    }

    /**
     * Gibt die bytes zurück, die mit der Nachricht gesendet wurden
     * @return die bytes der Nachricht
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gibt die CmdID der MNachricht zurück
     * @return die cmdID
     */
    public byte getCmdID() {
        return cmdID;
    }

    /**
     * Gibt true zurück, wenn die Nachricht schon verarbeitet wurde
     * @return true, wenn die Nachricht schon erarbetiet wurde, sonst false
     */
    public boolean isComputed() {
        return computed;
    }

    /**
     * Markiert die Nachricht al bearbeitet
     */
    public void setComputed() {
        this.computed = true;
    }
}
