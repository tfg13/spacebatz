package de._13ducks.spacebatz.shared.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Wrapper für einen ObjectOutputstream zum bequemen codieren von Daten.
 *
 * @author michael
 */
public class BitEncoder {

    private DataOutputStream outStream;
    private ByteArrayOutputStream buffer;

    public BitEncoder() {
        buffer = new ByteArrayOutputStream();
        outStream = new DataOutputStream(buffer);
    }

    public byte[] getBytes() {
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return buffer.toByteArray();

    }

    public void writeByte(byte val) {
        try {
            outStream.writeByte(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeLong(long val) {
        try {
            outStream.writeLong(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeInt(int val) {
        try {
            outStream.writeInt(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeFloat(float val) {
        try {
            outStream.writeFloat(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeString(String val) {
        try {
            outStream.writeUTF(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeBoolean(boolean val) {
        try {
            outStream.writeBoolean(val);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
