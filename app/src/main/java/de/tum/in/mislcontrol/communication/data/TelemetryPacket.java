package de.tum.in.mislcontrol.communication.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The ASEP telemetry packet.
 */
public class TelemetryPacket implements IPacket {
    private byte[] data = {
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
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00
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
}