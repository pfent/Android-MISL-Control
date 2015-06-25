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
            for(int i = 0; i < recievedCommand.getData().length; i++) {
                System.out.print(String.format("%02x ", recievedCommand.getData()[i]));
                if((i + 1) % 4 == 0)
                    System.out.print(" ");
                if((i + 1) % 8 == 0)
                    System.out.print("\n");
            }
            System.out.println("Checksum valid? " + recievedCommand.checkChecksum());
            
            byte[] sending = DummyData.
                    mockPackets[(counter++ / 40) % DummyData.mockPackets.length];
            
            DatagramPacket response = new DatagramPacket( sending, sending.length,
                    packet.getAddress(),  DEFAULT_PORT);
            sock.send(response);
        }
    }
    
}
