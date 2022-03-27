package client;

import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        
        String hostName = "127.0.0.1";
        int portNumber = 1234;

        Scanner sc = new Scanner(System.in);
        
        try {
            UdpClient client = new UdpClient(hostName, portNumber);

            while (true) {
                System.out.print("Enter message: ");
                String request = sc.nextLine();
                client.send(request);
 
                String response = client.receive();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
