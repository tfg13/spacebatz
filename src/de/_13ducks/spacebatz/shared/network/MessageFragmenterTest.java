package de._13ducks.spacebatz.shared.network;

import java.util.Random;

/**
 *
 * @author michael
 */
public class MessageFragmenterTest {

    public MessageFragmenterTest() {
        MessageFragmenter m = new MessageFragmenter();
        int numTests = 100000;
        for (int i = 0; i < numTests; i++) {
            System.out.println(100 * ((double) i / (double) numTests) + " %");
            byte[] testData;
            Random random = new Random();
            int size = random.nextInt(500) + 128;
            testData = new byte[size];
            for (int j = 0; j < size; j++) {
                testData[j] = (byte) j;
            }

            byte[][] fragmentedData = MessageFragmenter.fragmentMessage((byte) 81, testData);
            for (int j = 0; j < fragmentedData.length; j++) {
                m.fragmentedMessageData(fragmentedData[j]);
            }
            if (m.isComplete()) {
                byte[] resultData = m.getCompletedMessage();
                if (resultData.length != testData.length) {
                    throw new IllegalStateException("Länge nicht gleich!");
                }
                for (int x = 0; x < testData.length; x++) {
                    if (resultData[x] != testData[x]) {
                        throw new IllegalStateException("Daten falsch!");
                    }
                }
            } else {
                throw new IllegalStateException("Noch nicht fertig obwohl alle Packete bearbeitet wurden!");
            }
        }
    }

    public static void main(String args[]) {
        new MessageFragmenterTest();
    }
}
