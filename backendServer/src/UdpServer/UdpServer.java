package backendServer.src.UdpServer;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class UdpServer {
    private int port;
    private DatagramSocket socket;
    private HashMap<String, String> responseList;

    private InetAddress clientAddress;
    private int clientPort;

    public UdpServer(int portNumber) throws IOException {
        port = portNumber;
        socket = new DatagramSocket(port);
        responseList = new HashMap<>();
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
        socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        clientAddress = packet.getAddress();
        clientPort = packet.getPort();

        return new String(packet.getData(), 0, packet.getLength());
    }

    public boolean containsRequest(String requestId) {
        return responseList.containsKey(requestId);
    }

    public String getResponse(String requestId) {
        return responseList.get(requestId);
    }

    public void storeResponse(String requestId, String response) {
        responseList.put(requestId, response);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public String getClientAddress() {
        return clientAddress.getHostAddress();
    }
    
    public int getClientPort() {
        return clientPort;
    }
}