package server;

public class Main {

    private static UdpServer server;

    public static void main (String args[]) {
        int port = 1234;
        double loss = 0.0;
        boolean amo = true;     // at-most-once invocation semantics
        
        try {
            System.out.printf("Port: %d\n", port);
            System.out.printf("Loss: %.1f\n", loss);
            System.out.printf("Invocation semantics: %s\n", amo ? "at-most-once" : "at-least-once");
            System.out.println("Starting server...");

            server = new UdpServer(port);

            while (true) {
                String request = server.receive();
                System.out.println(request);
                
                String[] arr = request.split("_", 3);
                String requestId = arr[0];
                int option = Integer.parseInt(arr[1]);
                String payload = arr[2];

                String response = "";

                if (amo && server.containsRequest(requestId)) {
                    response = server.getResponse(requestId);
                } else {
                    response = processRequest(option, payload);
                    server.storeResponse(requestId, response);
                }

                if (Math.random() >= loss) {
                    server.send(response);
                } else {
                    System.out.println("Message dropped");
                }
 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String processRequest(int option, String payload) {
        String response = "";
        switch (option) {
            case 1:
                // response = openAccount();
                break;
            case 2:
                // response = closeAccount();
                break;
            case 3:
                // response = depositOrWithdraw();
                break;
            case 4:
                // String address = server.getClientAddress();
                // int port = server.getClientPort();
                // Callback cb = new Callback(server.getSocket(), address, port, 1000);
                // monitorUpdates(String.format("%s:%d", address, port), cb);
                // response = "Done";
                break;
            case 5:
                // response = idempotent();
                break;
            case 6:
                // response = nonIdempotent();
                break;
        }
        return response;
    }
}
