package de.tum.in.mislcontrol.communication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The ASEP command packet.
 */
public class CommandPacket implements IPacket {
    private byte[] data = {
            0x02, 0x03, //Packet Version
            0x00, 0x04, //Size = 64
            0x00, 0x00, //Receive SeqCnt
            0x75, 0x65, //Command (AFAIK this isn't checked anywhere)
            0x00, 0x00, //CH1 Cmd
            0x00, 0x00, //CH2 Cmd
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

    public void increaseSeqCnt() {
        if (++data[5] == 0) {
            data[6]++; //In case of overflow
        }
    }

    public void setCH1Cmd(short cmd) {
        byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
        data[8] = raw[0];
        data[9] = raw[1];
    }

    public void setCH2Cmd(short cmd) {
        byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
        data[10] = raw[0];
        data[11] = raw[1];
    }

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
            checksum += data[0];
        }
        byte[] raw = ByteBuffer.allocate(2).putShort(checksum).order(ByteOrder.BIG_ENDIAN).array();
        data[12] = raw[0];
        data[13] = raw[1];
    }
}
