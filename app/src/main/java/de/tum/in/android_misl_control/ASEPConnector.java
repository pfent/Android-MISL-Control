package de.tum.in.android_misl_control;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ASEPConnector {
    private static final int port = 30190;
    private static final byte[] byteAddress = {(byte) 192, (byte) 168, (byte) 16, (byte) 254};
    private static final int timeout = 3 * 1000; // 3 Seconds
    private static InetAddress iPAddress;
    CommandPacket sending = new CommandPacket();
    TelemetryPacket recieving = new TelemetryPacket();
    private DatagramSocket sock;
    private boolean listening = false;

    ASEPConnector() {
        try {
            iPAddress = InetAddress.getByAddress(byteAddress);
            sock = new DatagramSocket(port);
            sock.setSoTimeout(timeout);
        } catch (UnknownHostException | SocketException e) {
            //should not be thrown, probably do some logging
        }
    }

    public synchronized void sendCommand(short ch1, short ch2) throws IOException {
        sending.setCH1Cmd(ch1);
        sending.setCH2Cmd(ch2);
        sending.increaseSeqCnt();
        sending.calculateChecksum();
        DatagramPacket command =
                new DatagramPacket(sending.command, sending.command.length, iPAddress, port);
        sock.send(command);
    }

    public synchronized void listen() {
        listening = true;
        new Runnable() {
            @Override
            public void run() {
                while (listening) {
                    try {
                        TelemetryPacket next = new TelemetryPacket();
                        DatagramPacket packet =
                                new DatagramPacket(next.telemetry, next.telemetry.length);
                        sock.receive(packet);

                        //unsigned comparison
                        if (next.getSeqCont() - recieving.getSeqCont() > 0) {
                            recieving = next;
                        }
                    } catch (InterruptedIOException e) {
                        //this means the timeout expired, connection has been lost
                    } catch (IOException e) {
                        //probably do some logging
                    }
                }
            }
        }.run();
    }

    public synchronized void stopListening() {
        listening = false;
    }

    public void close() {
        stopListening();
        if (sock != null) {
            sock.close();
        }
    }

    class CommandPacket {
        byte[] command = {
                0x02, 0x03, //Packet Version
                0x00, 0x04, //Size = 64
                0x00, 0x00, //Recieve SeqCnt
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

        public void increaseSeqCnt() {
            if (++command[5] == 0) {
                command[6]++; //In case of overflow
            }
        }

        public short getSeqCnt() {
            return ByteBuffer.wrap(command, 5, 2).order(ByteOrder.BIG_ENDIAN).getShort();
        }

        public void setCH1Cmd(short cmd) {
            byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
            command[8] = raw[0];
            command[9] = raw[1];
        }

        public void setCH2Cmd(short cmd) {
            byte[] raw = ByteBuffer.allocate(2).putShort(cmd).order(ByteOrder.BIG_ENDIAN).array();
            command[10] = raw[0];
            command[11] = raw[1];
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
                checksum += command[0];
            }
            byte[] raw = ByteBuffer.allocate(2).putShort(checksum).order(ByteOrder.BIG_ENDIAN).array();
            command[12] = raw[0];
            command[13] = raw[1];
        }
    }

    class TelemetryPacket {
        private byte[] telemetry = {
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

        public short getSeqCont() {
            return ByteBuffer.wrap(telemetry, 4, 2).order(ByteOrder.BIG_ENDIAN).getShort();
        }

        public float getXEuler() {
            return ByteBuffer.wrap(telemetry, 8, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getYEuler() {
            return ByteBuffer.wrap(telemetry, 12, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getZEuler() {
            return ByteBuffer.wrap(telemetry, 16, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getXAccel() {
            return ByteBuffer.wrap(telemetry, 20, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getYAccel() {
            return ByteBuffer.wrap(telemetry, 24, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getZAccel() {
            return ByteBuffer.wrap(telemetry, 28, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getLatitude() {
            return ByteBuffer.wrap(telemetry, 32, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public float getLongitude() {
            return ByteBuffer.wrap(telemetry, 36, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }
    }
}
