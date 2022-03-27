package server;

public class Main {
    public static void main (String args[]) {
        int portNumber = 1234;

        
        try {
            UdpServer server = new UdpServer(portNumber);

            while (true) {
                String request = server.receive();
                System.out.println(request);
                
                String response = "You sent: " + request;
                server.send(response);
 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
