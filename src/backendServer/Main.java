package src.backendServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import src.backendServer.Callback.Callback;
import src.backendServer.UdpServer.UdpServer;
import src.backendServer.bankDetails.Bank;
import src.constants.Currency;

public class Main {

    private static UdpServer server;
    private static Bank bank;

    public static void main (String args[]) {
        int port = 1234;
        double loss = 0.0;
        boolean amo = true;     // at-most-once invocation semantics
        
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter a port:");
        port = sc.nextInt();

        System.out.println("Enter packet loss chance (0.0 - 1.0):");
        loss = sc.nextDouble();

        System.out.println("Choose invocation semantics:");
        System.out.println("1. At-most-once");
        System.out.println("2. At-least-once");
        int choice = sc.nextInt();
        if (choice == 1){
            amo = true;
        } else {
            amo = false;
        }

        sc.close();

        try {
            System.out.printf("Port: %d\n", port);
            System.out.printf("Loss: %.1f\n", loss);
            System.out.printf("Invocation semantics: %s\n", amo ? "at-most-once" : "at-least-once");
            System.out.println("Starting server...");


            server = new UdpServer(port);
            bank = new Bank();

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
        String[] arr = payload.split("\\|");

        HashMap<Integer, String> currencyMap = new HashMap<>();
        currencyMap.put(1, "SGD");
        currencyMap.put(2, "USD");
        currencyMap.put(3, "MYR");
        currencyMap.put(4, "JPY");
        currencyMap.put(5, "KRW");

        switch (option) {
            case 1:
                Currency curr = Currency.valueOf(arr[2]);
                int acctNum = bank.createAccount(arr[0],  arr[1], curr, Float.parseFloat(arr[3]));
                response = String.format("Account created with account number %d", acctNum);
                break;
            case 2:
                int result = bank.closeAccount(arr[0],  Integer.parseInt(arr[1]), arr[2]);
                if (result == 1) {
                    response = "Account has been removed";
                }
                else if (result == -1) {
                    response = "Account does not exist";
                } else if (result == -2) {
                    response = "Password is incorrect";
                }
                break;
            case 3:
                float balance = bank.updateBalance(arr[0], Integer.parseInt(arr[1]), arr[2], Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Float.parseFloat(arr[5]));
                if (balance == -1) {
                    response = "Account does not exist";
                }
                else if (balance == -2) {
                    response = "Password is incorrect";
                } else if (balance == -3) {
                    response = "Account balance insufficient";
                } else {
                    String accountCurrency = bank.getAccountCurrency(Integer.parseInt(arr[1]));
                    response = String.format("Success, your updated balance is %.2f %s ", balance, accountCurrency);
                }
                break;


            case 4:
                String address = server.getClientAddress();
                int port = server.getClientPort();
                Callback cb = new Callback(server.getSocket(), address, port, Integer.parseInt(arr[0]));
                bank.addCallback(String.format("%s:%d", address, port), cb);
                response = "Done";
                break;
            case 5:
                float bankBalance = bank.checkBalance(Integer.parseInt(arr[1]), arr[2]);
                if (bankBalance == -1) {
                    response = "Account does not exist";
                }
                else if (bankBalance == -2) {
                    response = "Password is incorrect";
                } else {
                    String accountCurrency = bank.getAccountCurrency(Integer.parseInt(arr[1]));
                    response = String.format("Success, your updated balance is %.2f %s ", bankBalance, accountCurrency);
                }
                break;
            case 6:
                int transferFund = bank.transferFunds(arr[0], Integer.parseInt(arr[1]), arr[2], Integer.parseInt(arr[3]), Float.parseFloat(arr[4]), Integer.parseInt(arr[5]));
                if(transferFund == 1) {
                    response = "Account being transferred to does not exist";
                    //float trfBalance = bank.updateBalance(arr[0], Integer.parseInt(arr[1]), arr[2], Integer.parseInt(arr[3]), 2, Float.parseFloat(arr[4]));
                } else if (transferFund == -1) {
                    response = "Account does not exist";
                } else if (transferFund == -2) {
                    response = "Password is incorrect";
                } else if (transferFund == -3) {
                    response = "Account balance insufficient";
                } else {
                    String transferCurrency = currencyMap.get(Integer.parseInt(arr[3]));
                    response = String.format("Fund transfer successful! %.2f %s has been transferred to %d", Float.parseFloat(arr[4]), transferCurrency, Integer.parseInt(arr[5])) ;
                }

                break;
        }
        return response;
    }
}
