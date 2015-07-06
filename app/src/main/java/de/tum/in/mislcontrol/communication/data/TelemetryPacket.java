package de.tum.in.mislcontrol.communication.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The ASEP telemetry packet.
 */
public class TelemetryPacket implements IPacket {
    private final byte[] data = {
            0x02, 0x03, //Packet Version
            0x00, 0x40, //Size = 64
            0x00, 0x00, //Recieve SeqCnt
            0x00, 0x00, //TransmitSeqCnt
            0x00, 0x00, 0x00, 0x00, //X Euler
            0x00, 0x00, 0x00, 0x00, //Y Euler
            0x00, 0x00, 0x00, 0x00, //Z Euler
            0x00, 0x00, 0x00, 0x00, //X Accel
            0x00, 0x00, 0x00, 0x00, //Y Accel
            0x00, 0x00, 0x00, 0x00, //Z Accel
            0x00, 0x00, 0x00, 0x00, //Latitude
            0x00, 0x00, 0x00, 0x00, //Longitude
            //12x 2B = 24 TBD (padding)
            0x30, 0x30, 0x31, 0x31, 0x32, 0x32,
            0x33, 0x33, 0x34, 0x34, 0x35, 0x35,
            0x36, 0x36, 0x37, 0x37, 0x38, 0x38,
            0x39, 0x39, 0x3a, 0x3a, 0x3b, 0x3b
    };

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getLength() {
        return data.length;
    }

    @Override
    public short getSeqCnt() {
        return ByteBuffer.wrap(data, 4, 2).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    public float getXEuler() {
        return ByteBuffer.wrap(data, 8, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getYEuler() {
        return ByteBuffer.wrap(data, 12, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getZEuler() {
        return ByteBuffer.wrap(data, 16, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getXAccel() {
        return ByteBuffer.wrap(data, 20, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getYAccel() {
        return ByteBuffer.wrap(data, 24, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getZAccel() {
        return ByteBuffer.wrap(data, 28, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getLatitude() {
        return ByteBuffer.wrap(data, 32, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public float getLongitude() {
        return ByteBuffer.wrap(data, 36, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    /**
     * only to be used for mocking!
     */
    public void setLatitude(float latitude) {
        byte[] raw = ByteBuffer.allocate(4).putFloat(latitude).order(ByteOrder.BIG_ENDIAN).array();
        data[32] = raw[0];
        data[33] = raw[1];
        data[34] = raw[2];
        data[35] = raw[3];
    }

    /**
     * only to be used for mocking!
     */
    public void setLongitude(float longitude) {
        byte[] raw = ByteBuffer.allocate(4).putFloat(longitude).order(ByteOrder.BIG_ENDIAN).array();
        data[36] = raw[0];
        data[37] = raw[1];
        data[38] = raw[2];
        data[39] = raw[3];
    }

    /**
     * only to be used for mocking!
     */
    public void setXEuler(float radianAngle) {
        byte[] raw = ByteBuffer.allocate(4).putFloat(radianAngle).order(ByteOrder.BIG_ENDIAN).array();
        data[8] = raw[0];
        data[9] = raw[1];
        data[10] = raw[2];
        data[11] = raw[3];
    }

    /**
     * only to be used for mocking!
     */
    public void setYEuler(float radianAngle) {
        byte[] raw = ByteBuffer.allocate(4).putFloat(radianAngle).order(ByteOrder.BIG_ENDIAN).array();
        data[12] = raw[0];
        data[13] = raw[1];
        data[14] = raw[2];
        data[15] = raw[3];
    }

    /**
     * only to be used for mocking!
     */
    public void setZEuler(float radianAngle) {
        byte[] raw = ByteBuffer.allocate(4).putFloat(radianAngle).order(ByteOrder.BIG_ENDIAN).array();
        data[16] = raw[0];
        data[17] = raw[1];
        data[18] = raw[2];
        data[19] = raw[3];
    }
}