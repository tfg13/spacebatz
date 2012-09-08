package de._13ducks.spacebatz.shared.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Wrapper für einen DataInputStream zum bequemen decodieren von Byte-Feldern.
 *
 * @author michael
 */
public class BitDecoder {

    private DataInputStream inStream;

    public BitDecoder(byte data[]) {
        inStream = new DataInputStream(new ByteArrayInputStream(data));
    }

    public int readInt() {
        try {
            return inStream.readInt();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public float readFloat() {
        try {
            return inStream.readFloat();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public byte readByte() {
        try {
            return inStream.readByte();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public short readShort() {
        try {
            return inStream.readShort();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public Long readLong() {
        try {
            return inStream.readLong();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return (long) 0;
    }

    public String readString() {
        try {
            return inStream.readUTF();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public boolean readBoolean() {
        try {
            return inStream.readBoolean();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
