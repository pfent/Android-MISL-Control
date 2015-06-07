package de.tum.in.mislcontrol.communication.data;

/**
 * The packet interface.
 */
public interface IPacket {

    /**
     * Gets the packet data.
     * @return The packet data as a byte array.
     */
    byte[] getData();

    /**
     * Gets the data length.
     * @return The data length.
     */
    int getLength();

    /**
     * Gets the sequence counter value.
     * @return The sequence counter value.
     */
    short getSeqCnt();
}
