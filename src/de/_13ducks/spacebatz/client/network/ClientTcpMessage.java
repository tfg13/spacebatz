package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.server.data.Client;

/**
 * Eine Klasse , um Tcp-Messages zwischenzuspeichern, bis der Hauptthread sie abarbeitet
 * 
 * @author michael
 */
public class ClientTcpMessage {

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
     * Der Client, der die 'Nachricht gesendet hat
     * (wird nur vom Server benötigt)
     */
    private Client sender;

    /**
     * Initialisiert das Packet mit Daten
     * @param cmdID die KomamndoID
     * @param data die Daten
     */
    public ClientTcpMessage(byte cmdID, byte data[]) {
        this.cmdID = cmdID;
        this.data = data;
        computed = false;
    }

    /**
     * Initialisiert das Packet mit Daten und einem Sender
     * dieser Konstruktor wird vom Serve verwendet
     * @param cmdID die KomamndoID
     * @param data die Daten
     * @param sender der Client der das Packet gesendet hat
     */
    public ClientTcpMessage(byte cmdID, byte data[], Client sender) {
        this.cmdID = cmdID;
        this.data = data;
        computed = false;
        this.sender = sender;
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

    /**
     * Gibt den Client, der das Packet gesendet hat zurück
     * @return der Client. der das Packet gesendet hat
     */
    public Client getSender() {
        return sender;
    }
}
