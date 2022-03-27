package client;

import java.io.*;
import java.net.*;

public class UdpClient {

    private InetAddress address;
    private int port;
    private DatagramSocket socket;

    public UdpClient(String hostName, int portNumber) throws IOException {
        address = InetAddress.getByName(hostName);
        port = portNumber;
        socket = new DatagramSocket();
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return new String(packet.getData(), 0, packet.getLength());
    }
}
