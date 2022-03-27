package client;

import java.util.Scanner;

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
                    client.send(message);

                    String response = client.receive();
                    System.out.println(response);
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

        System.out.print("Currency type: ");
        String currencyType = sc.next();

        System.out.print("Initial balance: ");
        Float balance = sc.nextFloat();

        return "1_" + String.join("|", name, password, currencyType, balance.toString());
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
        System.out.println("Deposit or withdraw:");
        Integer choice = sc.nextInt();

        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Account number: ");
        Integer acctNum = sc.nextInt();

        System.out.print("Password: ");
        String password = sc.next();

        System.out.print("Currency Type: ");
        String currencyType = sc.next();

        System.out.print("Amount: ");
        Float amount = sc.nextFloat();

        return "3_" + String.join("|", choice.toString(), name, acctNum.toString(), password, currencyType, amount.toString());
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
