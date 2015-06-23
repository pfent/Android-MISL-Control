package de.tum.in.ASEPMock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ASEPMock {
    
    private static final int DEFAULT_PORT = 30190;
    private static int counter = 0;

    public static void main(String[] args) throws SocketException, IOException {
        DatagramSocket sock = new DatagramSocket(DEFAULT_PORT);
        sock.setSoTimeout(5000);
        
        CommandPacket recievedCommand = new CommandPacket();
        DatagramPacket packet = new DatagramPacket(recievedCommand.getData(), 
                recievedCommand.getLength());
        
        for(;;) {
            try{
            sock.receive(packet);
            } catch (Exception e) {
                System.out.println("no packets for 5 seconds!");
                continue;
            }
            System.out.println("Recieved Command Packet!");
            System.out.println("CH1 command: " + recievedCommand.getCH1Cmd());
            System.out.println("CH2 command: " + recievedCommand.getCH2Cmd());
            System.out.println("Checksum valid? " + recievedCommand.checkChecksum());
            
            byte[] sending = DummyData.
                    mockPackets[(counter++ / 40) % DummyData.mockPackets.length];
            
            DatagramPacket response = new DatagramPacket( sending, sending.length,
                    packet.getAddress(),  DEFAULT_PORT);
            sock.send(response);
        }
    }
    
}
