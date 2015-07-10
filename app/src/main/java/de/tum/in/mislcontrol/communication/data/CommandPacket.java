package de.tum.in.mislcontrol.communication.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The ASEP command packet.
 */
public class CommandPacket implements IPacket {
    /**
     * The default packet data.
     */
    private final byte[] data = {
            0x02, 0x03, //Packet Version
            0x00, 0x40, //Size = 64
            0x00, 0x01, //Receive SeqCnt
            0x40, 0x56, //Command (AFAIK this isn't checked anywhere)
            0x00, 0x06, //CH1 Cmd
            0x00, 0x06, //CH2 Cmd
            0x00, 0x00, //Checksum
            //25x 2B = 50 Byte padding
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
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
        return ByteBuffer.wrap(data, 5, 2).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    /**
     * Increases the sequence counter.
     * Note: Currently not used, because ASEP is not even looking on this value.
     */
    public void increaseSeqCnt() {
        if (++data[4] == 0) {
            data[5]++; //In case of overflow
        }
    }

    /**
     * Sets the command on channel 1.
     * @param cmd The command value in range[-6, 6].
     */
    public void setCH1Cmd(short cmd) {
        byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
        data[8] = raw[0];
        data[9] = raw[1];
    }

    /**
     * Sets the command on channel 2.
     * @param cmd The command value in range[-6, 6].
     */
    public void setCH2Cmd(short cmd) {
        byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
        data[10] = raw[0];
        data[11] = raw[1];
    }

    /**
     * Calculates the checksum.
     */
    public void calculateChecksum() {
        // Checksum calculation according to "WizFi630Board.c"
        // Note, that the buffer_w has an offset of 2 in comparison to our buffer!
        //
        // Add command packet together to get checksum
        // checksum=(buffer_w[ 2])+(buffer_w[3])+(buffer_w[4])+(buffer_w[5])+(buffer_w[6])+
        //         (buffer_w[7])+(buffer_w[8])+(buffer_w[9])+(buffer_w[10])+(buffer_w[11])+
        //         (buffer_w[12])+(buffer_w[13])+(buffer_w[14]);
        //
        // if(checksum!=buffer_w[15]) // If checksum does not equal received checksum
        // {
        //	 state = PARSESTATE_SEARCHING_FOR_WIZFI630_INSERT_TAG;
        //	 return 0; // Exit! Packet was damaged
        // }
        // return 1;  // HAPPY OUTPUT! A packet has been extracted and stored in payload structure

        short checksum = 0;
        for (int i = 0; i < 12; i++) {
            checksum += data[i];
        }
        byte[] raw = ByteBuffer.allocate(2).putShort(checksum).order(ByteOrder.BIG_ENDIAN).array();
        data[12] = raw[0];
        data[13] = raw[1];
    }
}
