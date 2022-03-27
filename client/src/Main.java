package client.src;

import java.util.Scanner;

import Constants.Currency;
import client.src.UdpClient.UdpClient;

public class Main {
    static Scanner sc;
    public static void main(String args[]) {

        String hostName = "127.0.0.1";
        int portNumber = 1234;

        int count = 0;
        sc = new Scanner(System.in);

        try {
            UdpClient client = new UdpClient(hostName, portNumber);

            while (true) {

                System.out.println("Select a service:");
                System.out.println("1. Open account");
                System.out.println("2. Close account");
                System.out.println("3. Deposit / Withdraw");
                System.out.println("4. Monitor updates");
                System.out.println("5. Idempotent");
                System.out.println("6. Non-idempotent");
                System.out.println("7. Exit");

                System.out.print("Choice: ");
                int choice = sc.nextInt();

                if (choice < 1 || choice > 7) {
                    System.out.println("Invalid option");
                } else if (choice == 7) {
                    System.out.println("Thanks and hope to see you again soon!");
                    break;
                } else {
                    String payload = processRequest(choice);
                    String message = String.format("%s|%d_%s", client.getAddress(), count, payload);
                    count += 1;

                    String response = client.sendAndReceive(message);
                    System.out.println(response);

                    // Special case for monitor updates
                    if (choice == 4) {
                        client.receiveAll(payload);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(int choice) {
        String payload = "";
        switch (choice) {
            case 1:
                payload = openAccount();
                break;
            case 2:
                payload = closeAccount();
                break;
            case 3:
                payload = depositOrWithdraw();
                break;
            case 4:
                payload = monitorUpdates();
                break;
            case 5:
                payload = idempotent();
                break;
            case 6:
                payload = nonIdempotent();
                break;
        }
        return payload;
    }

    private static String openAccount() {
        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Password: ");
        String password = sc.next();

        System.out.println("Currency type:");
        int i = 1;
        Currency[] currencies = Currency.values();
        for (Currency c: currencies) {
            System.out.printf("%d. %s\n", i, c);
            i += 1;
        }
        int choice = sc.nextInt();
        Currency currency = currencies[choice-1];

        System.out.print("Initial balance: ");
        Float balance = sc.nextFloat();

        return "1_" + String.join("|", name, password, currency.toString(), balance.toString());
    }
    private static String closeAccount() {
        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Account number: ");
        Integer acctNum = sc.nextInt();

        System.out.print("Password: ");
        String password = sc.next();

        return "2_" + String.join("|", name, acctNum.toString(), password);
    }

    private static String depositOrWithdraw() {

        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Account number: ");
        Integer acctNum = sc.nextInt();

        System.out.print("Password: ");
        String password = sc.next();

        System.out.println("Deposit or withdraw:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        Integer choice = sc.nextInt();

        System.out.print("Amount: ");
        Float amount = sc.nextFloat();

        return "3_" + String.join("|", name, acctNum.toString(), password, choice.toString(), amount.toString());
    }

    private static String monitorUpdates() {
        System.out.print("How many minutes to monitor: ");
        Integer interval = sc.nextInt();

        return "4_" + String.join("|", interval.toString());
    }

    private static String idempotent() {
        return "5_" + String.join("|", "Placeholder");
    }

    private static String nonIdempotent() {
        return "6_" + String.join("|", "Placeholder");
    }
}
