package src.client.UdpClient;

import java.io.*;
import java.net.*;

public class UdpClient {

    private InetAddress address;
    private int port;
    private DatagramSocket socket;

    public UdpClient(String hostName, int portNumber) throws IOException {
        this.address = InetAddress.getByName(hostName);
        this.port = portNumber;
        this.socket = new DatagramSocket();
    }

    public void send(String message) throws IOException {
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, this.port);
        this.socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return new String(packet.getData(), 0, packet.getLength());
    }

    public String sendAndReceive(String message) throws IOException {
        int timeout = 1000;

		send(message);
		socket.setSoTimeout(timeout);

		while (true) {
			try {
				return receive();
			} catch (SocketTimeoutException e) {
				System.out.println("Timeout, resending request...");
                return sendAndReceive(message);
			}
		}
	}

    public String getAddress() throws UnknownHostException {
        return String.format("%s:%d", InetAddress.getLocalHost().getHostAddress(), socket.getLocalPort());
    }

    public void receiveAll(String payload) {
        String[] arr = payload.split("_");
        int duration = Integer.parseInt(arr[1]);

		final Thread thisThread = Thread.currentThread();
		final int timeToRun = duration * 60000;

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(timeToRun);
					thisThread.interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		while (!Thread.interrupted()) {
			// do something interesting.
			try {
				System.out.println(receive());
			} catch (SocketTimeoutException e) {
				// This is okay
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
