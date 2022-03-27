package server;

import java.io.IOException;
import java.net.*;

public class Callback {

    DatagramSocket socket;
    private String address;
    private int port;
    private int ttl;

    public Callback(DatagramSocket serverSocket, String clientAddress, int clientPort, int timeout) {
        socket = serverSocket;
        address = clientAddress;
        port = clientPort;
        ttl = timeout;
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address), port);
        socket.send(packet);
        System.out.println("Sent");
    }
    
    public boolean checkTtl() {
        if (ttl <= 0)
            return false;
        ttl -= 1;
        return true; 
    }
}