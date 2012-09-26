package de._13ducks.spacebatz.shared.network;

/**
 * Fragmentiert und Defragmentiert Nachrichten.
 *
 * Byte-Felder werden in mehrere, kleine byte-Blöcke aufgeteilt. Fügt die byte-Blöcke wieder zusammen und macht einen Rohdatenblock für NetCommands daraus.
 *
 * Die Byte-Blöcke müssen in der Reihenfolge in der sie im Array stehen gesendet werden, und auch in der Reihenfolge wieder an fragmentedMessageData() geliefert werden!
 *
 * @author michael
 */
public class MessageFragmenter {

    /**
     * Die Rohdaten der Nachricht werden hier zusammengesetzt.
     */
    private byte data[];
    /**
     * Der Index an den gerade in data[] geschrieben wird.
     */
    private int index;
    private int messageID;
    /**
     * Gibt an ob das Packet das gerade empfangen wird schon fertig ist.
     */
    private boolean complete;

    public MessageFragmenter() {
        complete = true;
    }

    /**
     * Fügt Daten zu der aktuell empfangenen fragmentiereten Nachricht hinzu. Wenn schon eine fertige Nachricht empfangen wurde wird sie gelöscht und eine neue angefangen.
     *
     * @param data das nächte Datenfragment der gerade empfagnenen Nachricht
     */
    public void fragmentedMessageData(byte messageBytes[]) {
        if (complete) {
            if (messageBytes.length != 6) {
                throw new IllegalStateException("Erwarte ein FRAGMENT_START-Packet, habe aber was anderes bekommen!");
            }
            BitDecoder decoder = new BitDecoder(messageBytes);
            // neue nachricht anfangen:
            decoder.readByte(); // Größe skippen
            complete = false;
            index = 0;
            data = new byte[decoder.readInt()]; //
            messageID = decoder.readByte(); // netID der Nachricht übernehmen
            if (messageID < 0) {
                messageID += 256;
            }
        } else {
            // weitere Fragmente der aktuellen Nachricht empfangen:
            System.arraycopy(messageBytes, 1, data, index, messageBytes.length - 1);
            index += messageBytes.length - 1;
            if (index == data.length) {
                complete = true;
            } else if (index > data.length) {
                throw new IllegalStateException("OMG too many dataz");
            }

        }


    }

    /**
     * Gibt eine fertig zusammengesetzte Nachricht als RohdatenBlock zurück.
     *
     * @return
     */
    public byte[] getCompletedMessage() {
        if (isComplete()) {
            return this.data;
        } else {
            throw new IllegalStateException("Die Nachricht wurde noch nicht fertig empfangen!");
        }
    }

    /**
     * Gibt die Id der empfangenen Nachricht zurück.
     *
     * @return
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * Gibt true zurück wenn die Nachricht fertig empfangen ist
     *
     * @return
     */
    public boolean isComplete() {
        return complete && data != null;
    }

    /**
     * Fragmentiert eine Nachricht in Byte-Blöcke, die vom MessageFragmenter wieder zusammengesetzt werden können.
     *
     * @param id die netID der Nachricht, ide fragmentiert werden soll
     * @param data die Daten der Nachricht, ide fragmentiert werden soll
     * @return ein array von byte-arrays, die jeweils höchstens 128 byte lang sind und vom Messagefragmenter wieder zur Nachricht zusammengesetzt werden können
     */
    public static byte[][] fragmentMessage(byte id, byte data[]) {
        int numFragments = data.length / 127;
        int restFragmentSize = data.length % 127;
        int numPackets = (restFragmentSize == 0) ? numFragments + 1 : numFragments + 2;
        byte fragments[][] = new byte[numPackets][];
        // Erstes Packet // Anfangsnachricht schicken
        BitEncoder encoder = new BitEncoder();
        encoder.writeByte((byte) 6);
        encoder.writeInt(data.length);
        encoder.writeByte((byte) id);
        fragments[0] = encoder.getBytes();
        // 127er Fragmente :
        for (int i = 1; i <= numFragments; i++) {
            fragments[i] = new byte[128];
            fragments[i][0] = (byte) 0x80;
            System.arraycopy(data, 127 * (i - 1), fragments[i], 1, 127);
        }
        // Restpacket?
        if (restFragmentSize != 0) {
            fragments[numFragments + 1] = new byte[restFragmentSize + 1];
            fragments[numFragments + 1][0] = (byte) (restFragmentSize + 1);
            System.arraycopy(data, 127 * numFragments, fragments[numFragments + 1], 1, restFragmentSize);
        }
        return fragments;
    }
}
