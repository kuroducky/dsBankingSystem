package server;

import java.io.IOException;
import java.net.*;

public class UdpServer {
    private int port;
    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    public UdpServer(int portNumber) throws IOException {
        port = portNumber;
        socket = new DatagramSocket(port);
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
        socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        clientAddress = packet.getAddress();
        clientPort = packet.getPort();

        return new String(packet.getData(), 0, packet.getLength());
    }
}