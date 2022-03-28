package src.backendServer.Callback;

import java.io.IOException;
import java.net.*;
import java.time.Instant;

public class Callback {

    DatagramSocket socket;
    private String address;
    private int port;
    private long ttl;

    public Callback(DatagramSocket serverSocket, String clientAddress, int clientPort, int timeout) {
        socket = serverSocket;
        address = clientAddress;
        port = clientPort;
        ttl = Instant.now().getEpochSecond() + timeout * 60;
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address), port);
        socket.send(packet);
        System.out.println("Sent");
    }
    
    public boolean stillAlive() {
        long now = Instant.now().getEpochSecond();
        return ttl > now;
    }
}