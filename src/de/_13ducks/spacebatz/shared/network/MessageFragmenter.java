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
            if (messageBytes.length != 5) {
                throw new IllegalStateException("Erwarte ein FRAGMENT_START-Packet, habe aber was anderes bekommen!");
            }
            BitDecoder decoder = new BitDecoder(messageBytes);
            // neue nachricht anfangen:
            complete = false;
            index = 5;
            data = new byte[decoder.readInt() + 5]; //
            data[4] = decoder.readByte(); // netID der Nachricht übernehmen
        } else {
            // weitere Fragmente der aktuellen Nachricht empfangen:
            System.arraycopy(messageBytes, 0, data, index, messageBytes.length);
            index += messageBytes.length;
            if (index == data.length) {
                complete = true;
            } else if (index > data.length) {
                throw new IllegalStateException("OMG");
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
        int numFragments = data.length / 128;
        int restFragmentSize = data.length % 128;
        // enthält das start-packet, die 128erBlöcke und den restblock
        byte fragments[][] = new byte[numFragments + 2][];
        for (int i = 0; i < fragments.length; i++) {
            if (i == 0) {
                // Erstes PAcket // Anfangsnachricht schicken
                BitEncoder encoder = new BitEncoder();
                encoder.writeInt(data.length);
                encoder.writeByte((byte) id);
                fragments[i] = encoder.getBytes();
            } else if (i > 0 && i < fragments.length - 1) {
                // Datenblock
                fragments[i] = new byte[128];
                System.arraycopy(data, 128 * (i - 1), fragments[i], 0, 128);
            } else if (i == fragments.length - 1) {
                // Restblock
                fragments[i] = new byte[restFragmentSize];
                    System.arraycopy(data, numFragments * 128, fragments[i], 0, restFragmentSize);
            }
        }
        return fragments;
    }

    public int wantBytes() {
        if (isComplete() || data == null) {
            return 5;
        } else {
            if (data.length - index > 128) {
                return 128;
            } else {
                return data.length - index;
            }
        }
    }
}
